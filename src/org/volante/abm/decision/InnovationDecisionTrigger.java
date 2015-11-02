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
 * Created by Sascha Holzhauer on 3 Jun 2015
 */
package org.volante.abm.decision;

import org.volante.abm.agent.Agent;
import org.volante.abm.agent.bt.LaraBehaviouralComponent;

import de.cesr.lara.components.eventbus.events.LAgentPreprocessEvent;
import de.cesr.lara.components.eventbus.impl.LDcSpecificEventbus;
import de.cesr.lara.components.model.impl.LModel;

/**
 * @author Sascha Holzhauer
 *
 */
public class InnovationDecisionTrigger extends AbstractDecisionTrigger {

	/**
	 * 
	 */
	public InnovationDecisionTrigger() {
	}

	/**
	 * For testing purposes
	 * 
	 * @param dcId
	 */
	public InnovationDecisionTrigger(String dcId) {
		this.dcId = dcId;
	}

	/**
	 * @see org.volante.abm.decision.DecisionTrigger#check(Agent)
	 */
	@Override
	public void check(Agent agent) {
		((LDcSpecificEventbus) LModel.getModel(agent.getRegion())
				.getLEventbus())
.subscribeOnce(
				(LaraBehaviouralComponent) agent.getBC(),
				LAgentPreprocessEvent.class,
 LModel.getModel(agent.getRegion())
						.getDecisionConfigRegistry().get(dcId));
	}
}
