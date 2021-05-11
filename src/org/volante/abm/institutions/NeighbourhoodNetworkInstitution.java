/**
 * 
 */
package org.volante.abm.institutions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;
import org.volante.abm.agent.Agent;
import org.volante.abm.agent.fr.FunctionalRole;
import org.volante.abm.data.Capital;
import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.data.Service;
import org.volante.abm.decision.pa.CompetitivenessAdjustingPa;
import org.volante.abm.example.AgentPropertyIds;
import org.volante.abm.example.RegionalDemandModel;
import org.volante.abm.institutions.pa.RegionalSubsidyPa.RegionalSubsidyPaPreferences;
import org.volante.abm.schedule.PrePreTickAction;
import org.volante.abm.schedule.RunInfo;
import org.volante.abm.update.CSVCapitalUpdater;

import com.moseph.modelutils.curve.Curve;
import com.moseph.modelutils.fastdata.DoubleMap;
import com.moseph.modelutils.fastdata.UnmodifiableNumberMap;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;


/**
 * Calculate AFT density and adjusts competitiveness accordingly. 
 * The adjustment is performed at the beginning of each tick
 * (e.g. before perceiving social networks).
 * 
 * @author Bumsuk Seo
 * 
 */
public class NeighbourhoodNetworkInstitution extends AbstractInstitution {

	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(NeighbourhoodNetworkInstitution.class);


	/**
	 * Name of CSV file that contains per-tick capital adjustment factors
	 * relative to the base capitals.
	 */
	//	@Element(required = false)
	//	String capitalAdjustmentsCSV = null;

	/**
	 * Neighbourhood size in pixel 
	 */
	@Element(name = "neighbourhoodRadius", required = true)
	private int neighbourhoodRadius = 5; 


	/**
	 * Multiplier (alpha) (from -1 to 1)  
	 */
	@Element(name = "effectFactor", required = true)
	private float effectFactor = (float) 0.1;


 


	/**
	 * Update aft density 
	 * Called at the start of each tick to allow this institution to perform any
	 * internal updates necessary.
	 */
	@Override
	public void update() { 
		
		// <- LOGGING
		logger.info("Update neighbourhood density " + this);
		// LOGGING ->

		// @TODO parallelise
		for (Cell c : this.region.getAllCells()) {

			if (c.getOwner() != Agent.NOT_MANAGED && c.getOwner() != null ) {

				int cId = c.getOwnersFrSerialID();
				int sameNeighbours = 0;

				Set<Cell> neighbours = region.getAdjacentCells(c, neighbourhoodRadius);

				// Count neighbouring cells under the same management

				for (Cell n : neighbours) {
					if (n.getOwnersFrSerialID() == cId) {
						sameNeighbours++;
					}
				}
				// Sum of the AFT density multiplied by effectOnCapitalFactor
				float snStrength = (float) sameNeighbours / (float) neighbours.size() * effectFactor;
				// logger.trace("snStrength = density * effectFactor ="+snStrength);

				c.setObjectProperty(AgentPropertyIds.SN_STRENGTH, (double) snStrength);


			}
		}
	}


	@Override
	public double adjustCompetitiveness(FunctionalRole fRole, Cell c, UnmodifiableNumberMap<Service> provision,
			double competitiveness) {
		
		double adjustFactor = 1.0;

		if (c.getOwner() != Agent.NOT_MANAGED && c.getOwner() != null ) {

			//		try {
			adjustFactor += (double) c.getObjectProperty(AgentPropertyIds.SN_STRENGTH) ; 
			//		} catch (IllegalStateException ise) { 
			//			ise.printStackTrace();
			//		}
		}			

		double adjustedComp = competitiveness * adjustFactor;
		//logger.trace("comp adjusted from " + competitiveness + " to " + adjustedComp);

		return adjustedComp;
	}

	/**
	 * @see org.volante.abm.institutions.AbstractInstitution#initialise(org.volante.abm.data.ModelData,
	 *      org.volante.abm.schedule.RunInfo, org.volante.abm.data.Region)
	 */
	public void initialise(ModelData data, RunInfo info, Region extent)
			throws Exception {
		super.initialise(data, info, extent);

		extent.setHasCompetitivenessAdjustingInstitution();

		// initialise sn strength
		for (Cell c : this.region.getAllCells()) {

			c.setObjectProperty(AgentPropertyIds.SN_STRENGTH, 0.0);

		}

		// <- LOGGING
		logger.info("Initialise " + this);
		// LOGGING ->

		//extent.setRequiresEffectiveCapitalData();
		//		if (capitalAdjustmentsCSV != null) {
		//			loadCapitalFactorCurves();
		//		}
	}


	//	/**
	//	 * @throws IOException
	//	 */
	//	void loadCapitalFactorCurves() throws IOException {
	//		// <- LOGGING
	//		logger.info("Load capital adjustment factors from "
	//				+ capitalAdjustmentsCSV);
	//		// LOGGING ->
	//
	//		try {
	//			Map<String, LinearInterpolator> curves = rInfo.getPersister()
	//					.csvVerticalToCurves(capitalAdjustmentsCSV, tickCol,
	//							modelData.capitals.names(), this.region.getPersisterContextExtra());
	//			for (Capital c : modelData.capitals) {
	//				if (curves.containsKey(c.getName())) {
	//					capitalFactorCurves.put(c, curves.get(c.getName()));
	//				}
	//			}
	//		} catch (NumberFormatException e) {
	//			logger.error("A required number could not be parsed from " + capitalAdjustmentsCSV
	//					+ ". Make "
	//					+ "sure the CSV files contains columns " + modelData.services.names());
	//			throw e;
	//		}
	//	}
	

	//	/**
	//	 * 	
	//	 *	Relevant classes: productivity innovation, innovation, restrictlandusepa, ConnectivityMeasure
	//	 * @see org.volante.abm.institutions.AbstractInstitution#adjustCapitals(org.volante.abm.data.Cell)
	//	 */
	//	@Override
	//	public void adjustCapitals(Cell c) {
	//
	//		if ((c.getOwner() == Agent.NOT_MANAGED) || (c.getOwner() != null)) { 
	//			logger.trace("do not adjust capital levels");
	//			return;
	//		}
	//
	//		DoubleMap<Capital> adjusted = modelData.capitalMap();
	//		c.getEffectiveCapitals().copyInto(adjusted);
	//
	//
	//		logger.trace("capital before adjustment= " + adjusted);
	//
	//		int cId = c.getOwnersFrSerialID();
	//		int sameNeighbours = 0;
	//
	//		Set<Cell> neighbours = region.getAdjacentCells(c, neighbourhoodRadius);
	//
	//		// Count neighbouring cells under the same management
	//
	//		for (Cell n : neighbours) {
	//			if (n.getOwnersFrSerialID() == cId) {
	//				sameNeighbours++;
	//			}
	//		}
	//		// Sum of the AFT density multiplied by effectOnCapitalFactor
	//		float adjustFactor = (float) sameNeighbours / (float) neighbours.size() * effectFactor;
	//
	//		logger.trace("adjustFactor = density * effectFactor ="+adjustFactor);
	//
	//		for (Capital capital : modelData.capitals) {
	//
	//			adjusted.put(capital, adjusted.getDouble(capital)
	//					* (1.0 + adjustFactor));
	//		}
	//
	//		logger.trace("capital adjusted= " + adjusted);
	//
	//		c.setEffectiveCapitals(adjusted);
	//
	//	}
	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Adjust competitiveness based on density of the same AFTs";
	}
}
