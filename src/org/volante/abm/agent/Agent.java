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
package org.volante.abm.agent;


import java.util.Set;

import org.volante.abm.agent.bt.BehaviouralComponent;
import org.volante.abm.agent.fr.FunctionalComponent;
import org.volante.abm.agent.property.DoublePropertyProvider;
import org.volante.abm.data.Cell;
import org.volante.abm.data.Region;
import org.volante.abm.data.Service;
import org.volante.abm.models.ProductionModel;

import com.moseph.modelutils.fastdata.UnmodifiableNumberMap;

import de.cesr.more.basic.agent.MoreObservingNetworkAgent;



/**
 * An interface detailing all the methods an Agent has to provide.
 *
 * @author dmrust
 *
 */
public interface Agent extends DoublePropertyProvider,
		MoreObservingNetworkAgent<Agent> {

	/**
	 * Returns all the cells the agent manages
	 *
	 * @return cells the agent manages
	 */
	public Set<Cell> getCells();

	/**
	 * @return the cell that is considered as the agent's base cell
	 */
	public Cell getHomeCell();

	/**
	 * Sets this agent's home cell
	 * 
	 * @param homecell
	 *            new home cell
	 */
	public void setHomeCell(Cell homecell);

	/**
	 * Removes the cell from the set the agent manages
	 *
	 * @param c
	 */
	public void removeCell(Cell c);


	/**
	 * Returns the production model of this agent.
	 * 
	 * @return production model
	 */
	public ProductionModel getProductionModel();

	/**
	 * Updates the agent's competitiveness, in response to demand changes etc.
	 */
	public void updateCompetitiveness();

	/**
	 * Recalculates the services this agent can supply
	 */
	public void updateSupply();

	/**
	 * Asks this agent if it wants to give up
	 */
	public void considerGivingUp();

	/**
	 * Returns what this agent could supply on the given cell
	 *
	 * @param c
	 * @return unmodifiable supply map
	 */
	public UnmodifiableNumberMap<Service> supply(Cell c);

	/**
	 * Adds the cell to the cells this agent manages
	 *
	 * @param c
	 */
	public void addCell(Cell c);

	/**
	 * Returns true if this agent has lost all its cells and should be removed
	 *
	 * @return true if this agent is to remove
	 */
	public boolean toRemove();

	/**
	 * Called to remove the agent instance from the system.
	 */
	public void die();

	/**
	 * Returns the agent's ID/type
	 *
	 * @return ID
	 */
	public String getID();

	/**
	 * Return true if this agent is happy to cede to an agent with the given
	 * level of competitiveness
	 *
	 * @param c
	 * @param competitiveness
	 * @return true if an agent with the given competitiveness can take over the
	 *         given cell from this agents
	 */
	public boolean canTakeOver(Cell c, double competitiveness);

	/**
	 *
	 * Returns useful descriptive information about this agent
	 * 
	 * @return info
	 */
	public String infoString();

	/**
	 * Called at the beginning of each tick to allow the agent to do any internal housekeeping
	 */
	public void tickStartUpdate();

	/**
	 * Called at the ending of each tick to allow the agent to do any internal
	 * housekeeping
	 */
	public void tickEndUpdate();


	public void setRegion(Region r);

	public Region getRegion();

	/**
	 * Access Methods
	 */

	/**
	 * Access to this agent's behavioural component
	 * 
	 * @return bc
	 */
	public BehaviouralComponent getBC();

	public FunctionalComponent getFC();

	public void setBC(BehaviouralComponent bt);

	public void setFC(FunctionalComponent fr);


	public static String	NOT_MANAGED_ID			= "NOT MANAGED";
	public static double	NOT_MANAGED_COMPETITION	= -Double.MAX_VALUE;
	
	
	public static Agent NOT_MANAGED = new AbstractAgent(null) {

		@Override
		public ProductionModel getProductionModel() {
			return null;
		}

		@Override
		public void updateSupply() {

		}

		@Override
		public void considerGivingUp() {
			// nothing to do
		}

		@Override
		public UnmodifiableNumberMap<Service> supply(Cell c) {
			return null;
		}

		@Override
		public void die() {
			// nothing to do
		}

		@Override
		public boolean canTakeOver(Cell c, double competitiveness) {
			return true;
		}

		@Override
		public String infoString() {
			return NOT_MANAGED_ID;
		}

		@Override
		public void receiveNotification(
				de.cesr.more.basic.agent.MoreObservingNetworkAgent.NetworkObservation observation,
				Agent object) {
		}

		@Override
		public void setHomeCell(Cell homecell) {
		}
	};
}
