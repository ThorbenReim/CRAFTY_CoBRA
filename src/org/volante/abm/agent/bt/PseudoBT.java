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
 * Created by Sascha Holzhauer on 20 Mar 2015
 */
package org.volante.abm.agent.bt;

import java.util.Map.Entry;

import org.volante.abm.agent.Agent;
import org.volante.abm.agent.property.AgentPropertyId;
import org.volante.abm.agent.property.DoublePropertyProvider;
import org.volante.abm.agent.property.DoublePropertyProviderComp;
import org.volante.abm.decision.DecisionTrigger;
import org.volante.abm.example.AgentPropertyIds;


/**
 * @author Sascha Holzhauer
 *
 */
public class PseudoBT extends AbstractBT implements BehaviouralComponent {

	protected DoublePropertyProvider propertyProvider;

	public PseudoBT() {
		this.propertyProvider = new DoublePropertyProviderComp();
	}

	/**
	 * @see org.volante.abm.agent.property.DoublePropertyProvider#isProvided(org.volante.abm.agent.property.AgentPropertyId)
	 */
	@Override
	public boolean isProvided(AgentPropertyId property) {
		return this.propertyProvider.isProvided(property);
	}

	/**
	 * @see org.volante.abm.agent.property.DoublePropertyProvider#getProperty(org.volante.abm.agent.property.AgentPropertyId)
	 */
	@Override
	public double getProperty(AgentPropertyId property) {
		return this.propertyProvider.getProperty(property);
	}

	/**
	 * @see org.volante.abm.agent.property.DoublePropertyProvider#setProperty(org.volante.abm.agent.property.AgentPropertyId,
	 *      double)
	 */
	@Override
	public void setProperty(AgentPropertyId propertyId, double value) {
		this.propertyProvider.setProperty(propertyId, value);
	}

	/**
	 * @see org.volante.abm.agent.bt.BehaviouralType#assignNewBehaviouralComp(org.volante.abm.agent.Agent)
	 */
	@Override
	public Agent assignNewBehaviouralComp(Agent agent) {
		// does not call super.assignNewBehaviouralComp(agent) to prevent
		// throwing 'not initialised' error
		// (region is not required for PseudoBT)

		for (Entry<String, Double> entry : agentProperties2Set.entrySet()) {
			agent.setProperty(AgentPropertyIds.valueOf(entry.getKey()), entry
					.getValue().doubleValue());
		}

		agent.setBC(this);
		return agent;
	}

	/**
	 * @see org.volante.abm.agent.bt.BehaviouralComponent#getType()
	 */
	@Override
	public BehaviouralType getType() {
		return this;
	}

	/**
	 * @see org.volante.abm.agent.bt.BehaviouralComponent#triggerDecisions(Agent)
	 */
	@Override
	public void triggerDecisions(Agent agent) {
		// nothing to do
	}

	/**
	 * @see org.volante.abm.agent.bt.BehaviouralType#addDecisionTrigger(org.volante.abm.decision.DecisionTrigger)
	 */
	@Override
	public void addDecisionTrigger(DecisionTrigger trigger) {
		// nothing to do
	}

	/**
	 * @see org.volante.abm.agent.bt.BehaviouralType#removeDecisionTrigger(String)
	 */
	@Override
	public boolean removeDecisionTrigger(String id) {
		// nothing to do
		return false;
	}
}
