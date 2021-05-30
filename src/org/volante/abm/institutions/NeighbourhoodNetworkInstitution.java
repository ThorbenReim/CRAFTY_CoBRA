/**
 * 
 */
package org.volante.abm.institutions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;
import org.volante.abm.agent.Agent;
import org.volante.abm.agent.fr.FunctionalRole;
import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.data.Service;
import org.volante.abm.schedule.RunInfo;
import com.moseph.modelutils.fastdata.UnmodifiableNumberMap;
//import org.volante.abm.agent.fr.FunctionalRole;


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
	 * Update AFT density 
	 * Called at the start of each tick to allow this institution to perform any
	 * internal updates necessary.
	 */
	@Override
	public void update() { 

		// <- LOGGING
		logger.info("Update neighbourhood density " + this);
		// LOGGING ->

		// For-loop version
		//		for (Cell c : this.region.getAllCells()) {
		//
		//		//	if (c.getOwner() != Agent.NOT_MANAGED && c.getOwner() != null ) { might not need to exclude those cases
		//
		//				int cId = c.getOwnersFrSerialID();
		//				var sameNeighbours = 0;
		//
		//				Set<Cell> neighbours = region.getAdjacentCells(c, neighbourhoodRadius);
		//
		//				// Count neighbouring cells under the same management
		//
		//				for (Cell n : neighbours) {
		//					if (n.getOwnersFrSerialID() == cId) {
		//						sameNeighbours++;
		//					}
		//				}
		//				// Sum of the AFT density multiplied by effectOnCapitalFactor
		//				float snStrength = (float) sameNeighbours / (float) neighbours.size() * effectFactor;
		// 
		//				c.setObjectProperty(AgentPropertyIds.SN_STRENGTH, (double) snStrength);
		//
		//		//	}
		//		}

		// parallelised stream

		((Collection<Cell>) this.region.getAllCells()).parallelStream().forEach(c -> {

				region.getFunctionalRoles().forEach(fRole -> setDensity(c, fRole.getSerialID()));
		});		
	}

	@Override
	public void takeOver(Cell c) {
		// super.takeOver(c);
		// <- LOGGING
		// logger.trace("Update neighbourhood density for cell" + c.toString());
		// LOGGING ->

		region.getFunctionalRoles().forEach(fRole -> setDensity(c, fRole.getSerialID()));
			 

	}


	public  void setDensity(Cell c, int cId) { 

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

		c.setNeighbourhoodFrDensity(cId, snStrength);

	}






	@Override
	public double adjustCompetitiveness(FunctionalRole fRole, Cell c, UnmodifiableNumberMap<Service> provision,
			double competitiveness) {

		double adjustFactor = 1.0;

		if (c.getOwner() != Agent.NOT_MANAGED && c.getOwner() != null ) {

			adjustFactor += (double) c.getNeighbourhoodFrDensity(fRole);

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

		// Inform the model to adjust competitiveness from now on
		extent.setHasCompetitivenessAdjustingInstitution();

		// initialise SN strength (otherwise later threw IllegalStateException) 		
		//	for (Cell c : this.region.getAllCells()) {
		//		c.setObjectProperty(AgentPropertyIds.SN_STRENGTH, 0.0);
		//	}

		// parallelised
		((Collection<Cell>) this.region.getAllCells()).parallelStream().forEach(c -> {
			c.neighbourhoodFrDensity = new float[region.getFunctionalRoles().size()];
			Arrays.fill(c.neighbourhoodFrDensity, (float) 0.0);
		});

		// <- LOGGING
		logger.info("Initialise " + this);
		// LOGGING ->

	}




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
		return "Adjust competitiveness based on AFT density";
	}
}
