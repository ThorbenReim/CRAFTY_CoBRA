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
 * Created by Sascha Holzhauer on 29 Jul 2015
 */
package org.volante.abm.institutions.global;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.volante.abm.agent.fr.FunctionalRole;
import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.data.Service;
import org.volante.abm.decision.trigger.DecisionTrigger;
import org.volante.abm.schedule.PreTickAction;
import org.volante.abm.schedule.RunInfo;
import org.volante.abm.serialization.GloballyInitialisable;
import org.volante.abm.serialization.ScenarioLoader;

import com.moseph.modelutils.fastdata.DoubleMap;
import com.moseph.modelutils.fastdata.UnmodifiableNumberMap;


/**
 * 
 * Applies adjusted competitiveness when service trigger checks are positive.
 * 
 * @author Sascha Holzhauer
 * 
 */
public class GlobalSubsidisingInstitution extends AbstractCognitiveGlobalInstitution implements PreTickAction {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(GlobalSubsidisingInstitution.class);

	/**
	 * @param id
	 */
	public GlobalSubsidisingInstitution(@Attribute(name = "id") String id) {
		super(id);
	}

	Map<Service, DecisionTrigger> serviceTriggers = null;

	/**
	 * Contains only values for defined services
	 */
	DoubleMap<Service> definedServiceSubsidies = null;

	/**
	 * Contains default factor = 1.0 for undefined subsidies.
	 */
	DoubleMap<Service> appliedServiceSubsidies = null;

	@ElementMap(inline = true, required = true, entry = "serviceTrigger", attribute = true, key = "service")
	Map<String, DecisionTrigger> serialServiceTriggers = new HashMap<String, DecisionTrigger>();

	/**
	 * Factor the service provision is multiplied with.
	 */
	@ElementMap(inline = true, required = false, entry = "serviceSubsidyFactor", attribute = true, key = "service")
	Map<String, Double> serialServiceSubsidies = new HashMap<String, Double>();

	@Element(required = false)
	protected double overallEffect = 1.0;


	public void initialise(RunInfo rinfo, ModelData mdata, ScenarioLoader sloader) {
		super.initialise(rinfo, mdata, sloader);

		rinfo.getSchedule().register(this);

		for (Region region : sloader.getRegions().getAllRegions()) {
			region.setHasCompetitivenessAdjustingInstitution();
		}

		definedServiceSubsidies = mdata.serviceMap();
		appliedServiceSubsidies = mdata.serviceMap();
		serviceTriggers = new HashMap<>();

		for (Entry<String, DecisionTrigger> e : serialServiceTriggers.entrySet()) {
			if (mdata.services.contains(e.getKey())) {
				if (e.getValue() instanceof GloballyInitialisable) {
					try {
						((GloballyInitialisable) e.getValue()).initialise(mdata, rInfo, sloader.getRegions());
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				serviceTriggers.put(mdata.services.forName(e.getKey()), e.getValue());
			} else {
				logger.warn("The specified service (" + e.getKey()
						+ ") for the trigger is not defined in the model (defined: " + mdata.services + ")!");
			}
		}

		for (Entry<String, Double> e : serialServiceSubsidies.entrySet()) {
			if (mdata.services.contains(e.getKey())) {
				definedServiceSubsidies.put(mdata.services.forName(e.getKey()), e.getValue());
			} else {
				logger.warn("The specified service (" + e.getKey() + ") for the subsidy is not defined in the model!");
			}
		}
	}

	/**
	 * @see org.volante.abm.schedule.PreTickAction#preTick()
	 */
	@Override
	public void preTick() {
		for (Service service : this.modelData.services) {
			if (this.serviceTriggers.containsKey(service) && this.serviceTriggers.get(service).check(this)) {
				this.appliedServiceSubsidies.addDouble(service, this.definedServiceSubsidies.get(service));
			} else {
				this.appliedServiceSubsidies.addDouble(service, 0.0);
			}
		}
	}

	/**
	 * Multiplies given provision with service-specific subsidy factors, then multiplies this dot-product with
	 * <code>overallEffect</code>.
	 * 
	 * @see org.volante.abm.institutions.Institution#adjustCompetitiveness(org.volante.abm.agent.fr.FunctionalRole,
	 *      org.volante.abm.data.Cell, com.moseph.modelutils.fastdata.UnmodifiableNumberMap, double)
	 */
	@Override
	public double adjustCompetitiveness(FunctionalRole agent, Cell location, UnmodifiableNumberMap<Service> provision,
			double competitiveness) {
		double result = competitiveness;
		double subsidy = provision.dotProduct(appliedServiceSubsidies);
		result += subsidy * overallEffect;
		return result;
	}

	/**
	 * @return overall effect factor
	 */
	public double getOverallEffect() {
		return overallEffect;
	}

	/**
	 * @param overallEffect
	 */
	public void setOverallEffect(double overallEffect) {
		this.overallEffect = overallEffect;
	}
}
