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

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.volante.abm.agent.fr.FunctionalRole;
import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.data.Service;
import org.volante.abm.schedule.RunInfo;
import org.volante.abm.serialization.ScenarioLoader;

import com.moseph.modelutils.fastdata.DoubleMap;
import com.moseph.modelutils.fastdata.UnmodifiableNumberMap;


/**
 * @author Sascha Holzhauer
 *
 */
public class GlobalSubsidyInstitution extends AbstractGlobalInstitution {

	DoubleMap<Service> serviceSubsidies = null;

	@ElementMap(inline = true, required = false, entry = "serviceSubsidy", attribute = true, key = "service")
	Map<String, Double> serialServiceSubsidies = new HashMap<String, Double>();

	@Element(required = false)
	protected double overallEffect = 1.0;


	/**
	 * @see org.volante.abm.institutions.Institution#adjustCompetitiveness(org.volante.abm.agent.fr.FunctionalRole,
	 *      org.volante.abm.data.Cell, com.moseph.modelutils.fastdata.UnmodifiableNumberMap, double)
	 */
	@Override
	public double adjustCompetitiveness(FunctionalRole agent, Cell location, UnmodifiableNumberMap<Service> provision,
			double competitiveness) {
		double result = competitiveness;
		double subsidy = provision.dotProduct(serviceSubsidies);
		result += subsidy * overallEffect;
		return result;
	}

	public void initialise(RunInfo rinfo, ModelData mdata, ScenarioLoader sloader) {
		super.initialise(rinfo, mdata, sloader);

		for (Region region : sloader.getRegions().getAllRegions()) {
			region.setHasCompetitivenessAdjustingInstitution();
		}

		serviceSubsidies = mdata.serviceMap();

		for (Entry<String, Double> e : serialServiceSubsidies.entrySet()) {
			if (mdata.services.contains(e.getKey())) {
				serviceSubsidies.put(mdata.services.forName(e.getKey()), e.getValue());
			}
		}
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
