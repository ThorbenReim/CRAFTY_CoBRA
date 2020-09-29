/**
 * This file is part of
 * 
 * CRAFTY - Competition for Resources between Agent Functional TYpes
 *
 * Copyright (C) 2020 LUC group, IMK-IFU, KIT, Germany
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
 * LUC group, IMK-IFU, KIT
 * 
 * Created by seo-b on 3 Jun 2020
 */
package org.volante.abm.example;

import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.volante.abm.data.Service;
import org.volante.abm.models.CompetitivenessModel;

import com.moseph.modelutils.curve.Curve;
import com.moseph.modelutils.fastdata.UnmodifiableNumberMap;

/**
 * @author seo-b
 *
 */
public class RelativeThresholdCompetitivenessModel extends CurveCompetitivenessModel {

	/**
	 * Logger
	 */
	private static Logger log = Logger.getLogger(RelativeThresholdCompetitivenessModel.class);

	/**
	 * Residuals are normalised by per cell demand of the particular service. Used to balance differences in services'
	 * dimension before the competition function is applied (therefore, the competition function does not need to take
	 * differences in dimensions into account). Example: If the demand supply gap of cereal is 20% and that of meat is
	 * 50%, the normalised residual is higher for meat, but the absolute residual would be higher for cereal in case the
	 * absolute demand for cereal is much higher.
	 */
	@Attribute(required = false)
	boolean normaliseCellResidual = true;

	/**
	 * Supply as multiplied with the competition curve value is normalised by per cell demand for the particular
	 * service. When true, it is assumed that the value of production is relative to the demand (i.e., it is more
	 * profitable to produce a service whose relative (to demand) cell production is higher, not matter the absolute
	 * production).
	 * 
	 * Actually, a thorough representation would need to consider the market-wide ability to produce the particular
	 * service (which is currently not represented in the CRAFTY framework itself but modelled by the current supply as
	 * subject to competition).
	 */
	@Attribute(required = false)
	boolean normaliseCellSupply = true;

	/**
	 * Every time a threshold is used, it's converted to a proportion of the mean benefit value across the current population of agents. 
	 * It makes difficult to determine the prescribed giving-in and giving-up thresholds as the benefit level changes over time. 
	 * Ideally the current mean benefit value can be compared to the benefit values of a cell.
	 * 
	 * SD gap relative to the current demand
	 * E.g. Gap_i = (S_i - D_i)/D_i
	 * 
	 * org.volante.abm.agent.DefaultLandUseAgent.considerGivingUp() and
	 * org.volante.abm.agent.DefaultLandUseAgent.considerGivingUp.ProductionModel()
	 * @see org.volante.abm.example.NormalisedCurveCompetitivenessModel#addUpMarginalUtilities()
	 * @author seo-b
	 * TODO test
	 * 
	 */


	/**
	 * Adds up marginal utilities (determined by competitiveness for unmet demand) of all services.
	 * 
	 * @param residualDemand
	 * @param supply
	 * @param showWorking
	 *        if true, log details in DEBUG mode
	 * @return summed marginal utilities of all services
	 */


	@Override
	public double addUpMarginalUtilities(UnmodifiableNumberMap<Service> residualDemand,
			UnmodifiableNumberMap<Service> supply, boolean showWorking) { // @TODO showWorking is not being used.

		double sum = 0;
		String message = "";

		for (Service s : supply.getKeySet()) {
			Curve c = curves.get(s); /* Gets the curve parameters for this service */

			double perCellDemand = region.getDemandModel().getAveragedPerCellDemand().get(s);
			perCellDemand = (perCellDemand == 0) ? Double.MIN_VALUE : perCellDemand;

			if (c == null) {
				message = "Missing curve for: " + s.getName() + " got: " + curves.keySet();
				log.fatal(message);
				throw new IllegalStateException(message);
			}
			double res = residualDemand.getDouble(s);

			if (perCellDemand > 0.001) {
				log.debug(perCellDemand); 

			}
			log.debug(this + "> addUpMarginalUtilities ");
			log.debug("residualDemand=" + res + " perCellDemand="+perCellDemand + " in " + s.getName()) ;

			// 1967     DEBUG:	RelativeThresholdCompetitivenessModel - residualDemand=1.1089970033307922E-8 perCellDemand=46.45615663357212 in Meat


			if (normaliseCellResidual) {
				res /= perCellDemand;
			}


			log.debug("residualDemand/perCellDemand = " + res );

			// 1967     DEBUG:	RelativeThresholdCompetitivenessModel - residualDemand/perCellDemand = 2.387190597961265E-10


			if (res > 1.0) {
				message = "residualDemand/perCellDemand > 1 : " + s.getName() + " got: " + curves.keySet()
				+ " res = " + res;
				log.fatal(message);
				throw new IllegalStateException(message);
			}

			double marginal = c.sample(res); 
			/*
			 * Get the corresponding 'value' (y-value) for this level of unmet demand
			 */

			message = "marginal = " + marginal;
			log.debug(message);
			// 1967     DEBUG:	RelativeThresholdCompetitivenessModel - marginal = 2.983988247451581E-13 (=2.387190597961265E-10 * 0.00125 (see values in Competition_linear_new_relative.xml)



			double amount = supply.getDouble(s); // get supply for the service

			log.debug("amount = " + amount);
			// 1967     DEBUG:	RelativeThresholdCompetitivenessModel - amount = 86.4036268140081

			if (this.normaliseCellSupply) {
				amount /= perCellDemand;
			}

			// SD gap relative to the current demand
			// Gap_i = (S_i - D_i)/D_i
			amount = (amount-perCellDemand)/amount;
 

			log.debug( "amount/perCellDemand= " + amount);
			// 1967     DEBUG:	RelativeThresholdCompetitivenessModel - amount/perCellDemand= 1.8598961488684032


			if (removeNegative && marginal < 0) {
				marginal = 0;
			}

			double comp = ((marginal == 0 || amount == 0) ? 0 : marginal * amount);

			if (  removeNegative && comp < 0) {
				log.debug(String.format(
						"\t\tService %10s: Residual (%5f) > Marginal (%5f; Curve: %s) * Amount (%5f) = %5f",
						s.getName(), res, marginal, c.toString(), amount, marginal * amount));
			}

			log.debug( "Competitiveness = " + comp);
			//	   	1967     DEBUG:	RelativeThresholdCompetitivenessModel - Competitiveness = 5.549908249703771E-13


			sum += comp;
		}

		log.debug("Competitiveness sum: " + sum);

		return sum;
	}


	//	/**
	//	 * @see org.volante.abm.models.CompetitivenessModel#getDeepCopy()
	//	 * 
	//	 *      TODO test
	//	 */
	//	@Override
	//	public CompetitivenessModel getDeepCopy() {
	//		NormalisedCurveCompetitivenessModel copy = new NormalisedCurveCompetitivenessModel();
	//		for (Entry<Service, Curve> entry : this.curves.entrySet()) {
	//			copy.curves.put(entry.getKey(), entry.getValue());
	//		}
	//		copy.data = this.data;
	//		copy.info = this.info;
	//		copy.region = this.region;
	//
	//		copy.serviceColumn = this.serviceColumn;
	//		copy.slopeColumn = this.slopeColumn;
	//		copy.interceptColumn = this.interceptColumn;
	//		copy.linearCSV = this.linearCSV;
	//		copy.removeCurrentLevel = this.removeCurrentLevel;
	//		copy.removeNegative = this.removeNegative;
	//
	//		copy.normaliseCellResidual = this.normaliseCellResidual;
	//		copy.normaliseCellSupply = this.normaliseCellSupply;
	//
	//		return copy;
	//	}
}