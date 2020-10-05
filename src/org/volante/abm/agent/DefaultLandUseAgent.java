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


import org.apache.log4j.Logger;
import org.volante.abm.agent.fr.FunctionalRole;
import org.volante.abm.agent.fr.LazyFR;
import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.data.Service;
import org.volante.abm.example.AgentPropertyIds;
import org.volante.abm.models.ProductionModel;
import org.volante.abm.models.nullmodel.NullProductionModel;
import org.volante.abm.param.RandomPa;

import com.moseph.modelutils.fastdata.DoubleMap;
import com.moseph.modelutils.fastdata.UnmodifiableNumberMap;


/**
 * This is a default agent
 * 
 * @author jasper
 * 
 */
public class DefaultLandUseAgent extends AbstractLandUseAgent {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(DefaultLandUseAgent.class);


	public DefaultLandUseAgent(String id, ModelData data) {
		this(LazyFR.getInstance(), id, data, null,
				NullProductionModel.INSTANCE, -Double.MAX_VALUE,
				Double.MAX_VALUE);
	}

	public DefaultLandUseAgent(FunctionalRole fRole, ModelData data, Region r,
			ProductionModel prod,
			double givingUp, double givingIn) {
		this(fRole, "NA", data, r, prod, givingUp, givingIn);
	}

	public DefaultLandUseAgent(FunctionalRole fRole, String id, ModelData data,
			Region r) {
		this(fRole, id, data, r, fRole.getProduction(), fRole
				.getMeanGivingUpThreshold(),
				fRole.getMeanGivingInThreshold());
	}

	public DefaultLandUseAgent(FunctionalRole fRole, String id, ModelData data,
			Region r, ProductionModel prod, double givingUp, double givingIn) {
		super(r);
		this.id = id;
		this.propertyProvider.setProperty(
				AgentPropertyIds.GIVING_UP_THRESHOLD, givingUp);
		this.propertyProvider.setProperty(
				AgentPropertyIds.GIVING_IN_THRESHOLD, givingIn);
		fRole.assignNewFunctionalComp(this);
		productivity = new DoubleMap<Service>(data.services);
	}

	@Override
	public void updateSupply() {
		this.productivity.clear();
		for (Cell c : cells) {
			this.getProductionModel().production(c, c.getModifiableSupply());

			if (logger.isDebugEnabled()) {
				logger.debug(this + "(cell " + c.getX() + "|" + c.getY() + "): " + c.getModifiableSupply().prettyPrint());
			}
			c.getSupply().addInto(productivity);
		}
	}

	/**
	 * @see org.volante.abm.agent.Agent#getProductionModel()
	 */
	@Override
	public ProductionModel getProductionModel() {
		return this.getFC().getProduction();
	}





	@Override
	public void considerGivingUp() {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(this + "> Consider giving up: "
					+ this.getProperty(AgentPropertyIds.COMPETITIVENESS)
					+ " (competitiveness) < "
					+ this.getProperty(AgentPropertyIds.GIVING_UP_THRESHOLD)
					+ " (threshold)?");
		}
		// LOGGING ->



		/**
		 * Every time a threshold is used, it's converted to a proportion of the mean benefit value across the current
		 * population of agents. It makes difficult to determine the prescribed giving-in and giving-up thresholds as the
		 * benefit level changes over time. Ideally the current mean benefit value can be compared to the benefit values of
		 * a cell.
		 * 
		 * SD gap relative to the current demand E.g. Gap_i = (S_i - D_i)/D_i
		 * 
		 * org.volante.abm.agent.DefaultLandUseAgent.considerGivingUp() and
		 * org.volante.abm.agent.DefaultLandUseAgent.considerGivingUp.ProductionModel()
		 * 
		 * @see org.volante.abm.example.NormalisedCurveCompetitivenessModel#addUpMarginalUtilities()
		 * @author seo-b TODO test
		 * 
		 */


		double givingUpThreshold =  this.getProperty(AgentPropertyIds.GIVING_UP_THRESHOLD);

		double compThresholdDiff = givingUpThreshold - this.getProperty(AgentPropertyIds.COMPETITIVENESS);



		if (compThresholdDiff > 0.0) {

			double random = this.region.getRandom().getURService().nextDouble(RandomPa.RANDOM_SEED_RUN_GIVINGUP.name());

			double probability = this.getProperty(AgentPropertyIds.GIVING_UP_PROB)
					* Math.pow(
							compThresholdDiff
							/ this.region.getMaxGivingUpThresholdDeviation().get(this.getFC().getFR()),
							this.getProperty(AgentPropertyIds.GIVING_UP_PROB_WEIGHT).doubleValue());
			if (random < probability) {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug(this + "> GivingUp (random number: " + random + ", probability: " + probability + ")");
				}
				// LOGGING ->

				giveUp();
			} else {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug(this + "> GivingUp rejected! (random number: " + random + ", probability: "
							+ probability + ")");
				}
				// LOGGING ->
			}
		}
	}

	@Override
	public boolean canTakeOver(Cell c, double incoming) {

		double givingInThreshold =  this.getProperty(AgentPropertyIds.GIVING_IN_THRESHOLD);
		double competitiveness = this.getProperty(AgentPropertyIds.COMPETITIVENESS);

		// able to give in?
		boolean takeover = incoming > (competitiveness + givingInThreshold);
 
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(this + "> canTakeOver?" + takeover);
		}

		return (takeover);
	}

	@Override
	public UnmodifiableNumberMap<Service> supply(Cell c) {
		DoubleMap<Service> prod = productivity.duplicate();
		this.getFC().getProduction().production(c, prod);
		return prod;
	}

	public void setProductionFunction(ProductionModel f) {
		this.getFC().setProductionFunction(f);
	}

	public ProductionModel getProductionFunction() {
		return this.getFC().getProduction();
	}

	@Override
	public String infoString() {
		return "Giving up: "
				+ this.propertyProvider
				.getProperty(AgentPropertyIds.GIVING_UP_THRESHOLD)
				+ ", Giving in: "
				+ this.propertyProvider
				.getProperty(AgentPropertyIds.GIVING_IN_THRESHOLD)
				+ ", nCells: " + cells.size();
	}

	@Override
	public void receiveNotification(
			de.cesr.more.basic.agent.MoreObservingNetworkAgent.NetworkObservation observation,
			Agent object) {
	}

	/**
	 * @see org.volante.abm.agent.Agent#die()
	 */
	@Override
	public void die() {
		// nothing to do
	}
}
