/**
 * 
 */
package org.volante.abm.institutions;


import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;
import org.volante.abm.agent.Agent;
import org.volante.abm.agent.fr.FunctionalRole;
import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.schedule.RunInfo;
import org.volante.abm.serialization.ABMPersister;

import com.google.common.collect.Table;



import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.volante.abm.data.Cell;

//import com.moseph.modelutils.curve.Curve;
//import com.moseph.modelutils.curve.LinearInterpolator;
import com.moseph.modelutils.fastdata.DoubleMap;

/**
 * Reads preserved land use for ticks from a CSV file and controls land use competition 
 * accordingly. The adjustment is performed at the beginning of each tick
 * (e.g. before perceiving social networks) @todo or after?.   
 * 
 */


/**
 * @see org.volante.abm.institutions.AbstractInstitution#initialise(org.volante.abm.data.ModelData,
 *      org.volante.abm.schedule.RunInfo, org.volante.abm.data.Region)
 */

/**
 * @author Bumsuk Seo
 *
 */
public class LanduseControllingInstitution extends AbstractInstitution {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(LanduseControllingInstitution.class);

	/**
	 * CSV file matrix of functional role serial IDs as column and row names. If the entry is > 0 a transition from the
	 * row FR to the column FR is interpreted as restricted.



	 * Name of CSV file that contains per-tick capital adjustment factors
	 * relative to the base capitals.
	 */
	@Element(required = true)
	protected String csvFileRestrictedLanduse = null;
	/**
	 * Name of column in CSV file that specifies the year a row belongs to
	 */
	@Element(required = false)
	String tickCol = "Year";
	@Element(required = false)
	String xColumn = "x";
	@Element(required = false)
	String yColumn = "y";


	//	protected Table<String, String, Double> restrictedRoles;
	//
	//	protected Set<FunctionalRole> frs = null;

	public void initialise(ModelData data, RunInfo info, Region extent) throws Exception {
		super.initialise(data, info, extent);

		// <- LOGGING
		logger.info("Initialise " + this);
		// LOGGING ->
		final Table<String, String, Double> landuseProhibited;

		try {
			
//			landuseProhibited = ABMPersister.getInstance()..(csvFileRestrictedLanduse, "Restricted", null);
			// @todo read YN values  


		} catch (Exception exception) {
			exception.printStackTrace();
		}


		if (csvFileRestrictedLanduse != null) {
			//		do something here
		}
	}



	/**
	 * Checks configured restriction CSV file.
	 * 
	 * @param fr
	 * @param cell
	 * @return true if the given {@link FunctionalRole} is allowed to occupy the given cell.
	 */
	public boolean isAllowed(FunctionalRole fr, Cell cell) {

		//		DoubleMap<Capital> adjusted = modelData.landUses();
		//
 
		boolean landuseallowed = false;


		// TODO year tick in land 
		//		year tick 
		//		keeps applied until the next restriction rule applied 


		if (landuseallowed) {
			// <- LOGGING
			logger.info("Land use change allowed X" + cell.getX() + "Y" + cell.getY()
					);
			// LOGGING ->
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Landuse Restriction Institution";
	}
}



//
//	/**
//	 * @see org.volante.abm.institutions.AbstractInstitution#adjustCapitals(org.volante.abm.data.Cell)
//	 */
//	@Override
//	public void adjustCapitals(Cell c) {
//		int tick = rInfo.getSchedule().getCurrentTick();
//		DoubleMap<Capital> adjusted = modelData.capitalMap();
//		c.getEffectiveCapitals().copyInto(adjusted);
//		
//		for (Capital capital : modelData.capitals) {
//			if (capitalFactorCurves.containsKey(capital)) {
//				adjusted.put(capital, adjusted.getDouble(capital)
//						* capitalFactorCurves.get(capital).sample(tick));
//			}
//		}
//		c.setEffectiveCapitals(adjusted);
//	}
//

//
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


