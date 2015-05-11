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
 * School of Geoscience, University of Edinburgh, Edinburgh, UK
 * 
 */
package org.volante.abm.agent.fr;


import org.apache.log4j.Logger;
import org.volante.abm.agent.Agent;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.models.ProductionModel;
import org.volante.abm.schedule.RunInfo;


public class DefaultFR extends AbstractFR {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(DefaultFR.class);



	public DefaultFR() {
	}

	public DefaultFR(String label, ProductionModel production) {
		super(label, production);
	}

	public DefaultFR(String label, ProductionModel production, double givingUp,
			double givingIn) {
		super(label, production, givingUp, givingIn);
	}

	public FunctionalComponent getNewFunctionalComp(Agent agent) {
		return new DefaultFC(agent, this, production);
	}

	/**
	 * @see org.volante.abm.agent.fr.FunctionalRole#assignNewFunctionalComp(org.volante.abm.agent.Agent)
	 */
	@Override
	public Agent assignNewFunctionalComp(Agent agent) {
		agent.setFC(getNewFunctionalComp(agent));
		return agent;
	}

	@Override
	public void initialise(ModelData data, RunInfo info, Region region)
			throws Exception {
		super.initialise(data, info, region);
		logger.debug("Functional Role initialised: " + getLabel());
		logger.trace("Production: \n" + production);
	}
}
