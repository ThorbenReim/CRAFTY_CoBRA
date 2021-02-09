/**
 * 
 */
package org.volante.abm.institutions;


import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;
import org.volante.abm.agent.fr.FunctionalRole;
import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.example.AgentPropertyIds;
import org.volante.abm.schedule.RunInfo;
import org.volante.abm.serialization.transform.IntTransformer;

/**
 * Reads protected land use for ticks from a CSV file and controls land use competition 
 * accordingly. The adjustment is performed at the beginning of each tick
 * (e.g. before perceiving social networks) @TODO or after?.   
 * 
 */


/**
 * @see org.volante.abm.institutions.AbstractInstitution#initialise(org.volante.abm.data.ModelData,
 *      org.volante.abm.schedule.RunInfo, org.volante.abm.data.Region)
 * 		CellCSVReader
 * 		CellRasterReader
 * 		CSVCapitalUpdater
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
	 * Name of CSV file that contains per-tick land use changed allowed YN
	 */
	@Element(required = false)
	protected String csvFileProhibitedLanduse = null;
	
	/**
	 * Name of column in CSV file that specifies the year a row belongs to
	 */
 
	
	@Element(required = false)
	String xColumn = "x";
	@Element(required = false)
	String yColumn = "y";
	@Element(required = false)
	String prohibitedColumn = "Protected";
	
	@Element(required= false)
	String maskChar = "Y";
	
	 
	IntTransformer	xTransformer	= null;
	IntTransformer	yTransformer	= null;

//    @Override
//	public void initialise(ModelData data, RunInfo info, Region extent) throws Exception {
//		super.initialise(data, info, extent);
//
//		// <- LOGGING
//		logger.info("Initialise " + this);
//		// LOGGING ->
// 
//
//	} 
	
	/**
	 * Checks configured restriction CSV file.
	 * 
	 * @param fr
	 * @param cell
	 * @return true if it is chnage the FR of the given cell.
	 */
	@Override
	public boolean isAllowed(FunctionalRole fr, Cell cell) {
 
		boolean landuseallowed = !(boolean) cell.getObjectProperty(AgentPropertyIds.FR_IMMUTABLE);

		if (landuseallowed) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
			logger.debug("Land use change allowed X" + cell.getX() + "Y" + cell.getY());
			// LOGGING ->
			}
			return true;
		} else {
			if (logger.isDebugEnabled()) {
			logger.debug("Land use change prohibited X" + cell.getX() + "Y" + cell.getY());
			}
			return false;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ("Land Use Controlling Institution");
	}
}


 

