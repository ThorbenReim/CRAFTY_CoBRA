/**
 * This file is part of
 * 
 * CRAFTY - Competition for Resources between Agent Functional TYpes
 *
 * Copyright (C) 2015 School of GeoScience, University of Edinburgh, Edinburgh, UK
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
 * School of Geoscience, University of Edinburgh, Edinburgh, UK
 * 
 * Created by Sascha Holzhauer on 19 Mar 2015
 */
package org.volante.abm.agent.bt;

import org.volante.abm.agent.Agent;
import org.volante.abm.decision.pa.CraftyPa;
import org.volante.abm.lara.CobraLAgentComp;

import de.cesr.lara.components.agents.LaraAgentComponent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.model.impl.LModel;

/**
 * @author Sascha Holzhauer
 *
 */
public class CognitiveBC extends AbstractIndividualBC implements
 LaraBehaviouralComponent {


	protected CobraLAgentComp laraComp;

	public CognitiveBC(BehaviouralType bType, Agent agent) {
		super(bType, agent);
		this.laraComp = new CobraLAgentComp(
				LModel.getModel(this.agent.getRegion()), this, null);
	}
	/**
	 * @see de.cesr.lara.components.agents.LaraAgent#getAgentId()
	 */
	@Override
	public String getAgentId() {
		return this.agent.getID();
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgent#getLaraComp()
	 */
	@Override
	public LaraAgentComponent<LaraBehaviouralComponent, CraftyPa<?>> getLaraComp() {
		return this.laraComp;
	}

	/**
	 * @see de.cesr.lara.components.eventbus.LaraEventSubscriber#onEvent(de.cesr.lara.components.eventbus.events.LaraEvent)
	 */
	@Override
	public <T extends LaraEvent> void onEvent(T event) {
		// nothing to do
	}

	/**
	 * @see org.volante.abm.agent.bt.LaraBehaviouralComponent#subscribeOnce(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public void subscribeOnce(LaraDecisionConfiguration dc) {
		((CobraLAgentComp) this.getLaraComp()).subscribeOnce(dc);
	}
}
