/**
 * This file is part of
 * 
 * CRAFTY - Competition for Resources between Agent Functional TYpes
 *
 * Copyright (C) 2014 School of GeoScience, University of Edinburgh, Edinburgh, UK
 * 
 * CRAFTY is free software: You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * CRAFTY is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * School of GeoScience, University of Edinburgh, Edinburgh, UK
 */
package org.volante.abm.output;


import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.simpleframework.xml.Attribute;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.data.Regions;
import org.volante.abm.decision.pa.CraftyPa;
import org.volante.abm.schedule.PrePreTickAction;
import org.volante.abm.schedule.RunInfo;
import org.volante.abm.serialization.GloballyInitialisable;


/**
 * @author Sascha Holzhauer
 *
 */
public class ActionCSVOutputter extends TableOutputter<CraftyPa<?>> implements GloballyInitialisable, ActionObserver,
		PrePreTickAction {


	@Attribute(required = false)
	boolean addTick = true;

	@Attribute(required = false)
	boolean addBtLabel = true;

	/**
	 * Only considered when <code>perRegion==false</code> (otherwise, regions are considered in column headers).
	 */
	@Attribute(required = false)
	boolean addRegion = true;

	Map<Region, Set<CraftyPa<?>>> actions = new HashMap<>();


	@Override
	public void initialise(ModelData data, RunInfo info, Regions regions) throws Exception {
		for (Region r : regions.getAllRegions()) {
			r.setActionObserver(this);
		}
		info.getSchedule().register(this);
	}

	/**
	 * @see org.volante.abm.output.AbstractOutputter#getDefaultOutputName()
	 */
	@Override
	public String getDefaultOutputName() {
		return "ActionCSVOutputter";
	}

	/**
	 * @see org.volante.abm.output.AbstractOutputter#setOutputManager(org.volante.abm.output.Outputs)
	 */
	@Override
	public void setOutputManager(Outputs outputs) {
		super.setOutputManager(outputs);

		if (addTick) {
			addColumn(new TickColumn<CraftyPa<?>>());
		}

		if (addRegion && perRegion) {
			addColumn(new RegionsColumn<CraftyPa<?>>());
		}

		if (addBtLabel) {
			addColumn(new BtLabelColumn());
		}

		addColumn(new AgentColumn());
		addColumn(new ActionColumn());
	}

	public void writeData(Iterable<CraftyPa<?>> data, Regions r) throws IOException {
		super.writeData(data, r);

	}

	public class BtLabelColumn implements TableColumn<CraftyPa<?>> {

		public BtLabelColumn() {
		}

		@Override
		public String getHeader() {
			return "BT";
		}

		@Override
		public String getValue(CraftyPa<?> pa, ModelData data, RunInfo info, Regions rs) {
			return pa.getAgent().getType().getLabel();
		}
	}

	public class AgentColumn implements TableColumn<CraftyPa<?>> {

		public AgentColumn() {
		}

		@Override
		public String getHeader() {
			return "Agent";
		}

		@Override
		public String getValue(CraftyPa<?> pa, ModelData data, RunInfo info, Regions rs) {
			return pa.getAgent().getAgent().getID();
		}
	}

	public class ActionColumn implements TableColumn<CraftyPa<?>> {

		public ActionColumn() {
		}

		@Override
		public String getHeader() {
			return ("Action");
		}

		/**
		 * @see org.volante.abm.output.TableColumn#getValue(java.lang.Object, org.volante.abm.data.ModelData,
		 *      org.volante.abm.schedule.RunInfo, org.volante.abm.data.Regions)
		 */
		public String getValue(CraftyPa<?> pa, ModelData data, RunInfo info, Regions rs) {
			return pa.getKey();
		}
	}

	/**
	 * @see org.volante.abm.output.ActionObserver#observeAction(org.volante.abm.agent.Agent,
	 *      org.volante.abm.decision.pa.CraftyPa)
	 */
	@Override
	public void observeAction(CraftyPa<?> pa) {
		if (!this.actions.containsKey(pa.getAgent().getAgent().getRegion())) {
			this.actions.put(pa.getAgent().getAgent().getRegion(), new HashSet<CraftyPa<?>>());
		}
		this.actions.get(pa.getAgent().getAgent().getRegion()).add(pa);
	}

	/**
	 * @see org.volante.abm.output.TableOutputter#getData(org.volante.abm.data.Regions)
	 */
	@Override
	public Iterable<CraftyPa<?>> getData(Regions r) {
		Set<CraftyPa<?>> pas = new HashSet<>();
		for (Region region : r.getAllRegions()) {
			if (actions.containsKey(region)) {
				pas.addAll(actions.get(region));
			}
		}
		return pas;
	}

	/**
	 * @see org.volante.abm.schedule.PrePreTickAction#prePreTick()
	 */
	@Override
	public void prePreTick() {
		for (Region region : actions.keySet()) {
			actions.get(region).clear();
		}
	}
}
