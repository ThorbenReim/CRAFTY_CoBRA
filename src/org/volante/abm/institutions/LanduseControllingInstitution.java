/**
 * 
 */
package org.volante.abm.institutions;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.volante.abm.agent.Agent;
import org.volante.abm.agent.fr.FunctionalRole;
import org.volante.abm.agent.property.PropertyId;
import org.volante.abm.data.Capital;
import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.example.CellPropertyIds;
import org.volante.abm.example.SimpleCapital;
import org.volante.abm.output.tablecolumns.RestrictionNumber.RestrictionNumberProperties;
import org.volante.abm.param.RandomPa;
import org.volante.abm.schedule.RunInfo;
import org.volante.abm.serialization.ABMPersister;
import org.volante.abm.serialization.CellCSVReader;
import org.volante.abm.serialization.CellRasterReader;
import org.volante.abm.serialization.ModelRunner;
import org.volante.abm.serialization.RegionLoader;
import org.volante.abm.serialization.transform.IntTransformer;

import com.csvreader.CsvReader;
import com.google.common.collect.Table;



import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.volante.abm.data.Cell;

//import com.moseph.modelutils.curve.Curve;
//import com.moseph.modelutils.curve.LinearInterpolator;
import com.moseph.modelutils.fastdata.DoubleMap;

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
	@Element(required = true)
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

    @Override
	public void initialise(ModelData data, RunInfo info, Region extent) throws Exception {
		super.initialise(data, info, extent);

		// <- LOGGING
		logger.info("Initialise " + this);
		// LOGGING ->
		logger.info("Loading land use restriction CSV from " + csvFileProhibitedLanduse);



		try {

			ABMPersister persister = ABMPersister.getInstance();

  
			logger.info("Loading cell CSV from " + csvFileProhibitedLanduse);
 
			CsvReader reader = persister.getCSVReader(csvFileProhibitedLanduse, this.region.getPersisterContextExtra());

			List<String> columns = Arrays.asList(reader.getHeaders());
			
			if (!columns.contains(prohibitedColumn)) { 
				throw new IllegalStateException(
				        "The land use controlling institution does not have " + prohibitedColumn +  " in the CSV file " + csvFileProhibitedLanduse);
			}
			
			 
			
			while (reader.readRecord()) {
 				if (logger.isDebugEnabled()) {
					logger.debug("Read row " + reader.getCurrentRecord());
				}
 
				int x = Integer.parseInt(reader.get(xColumn));
				if (xTransformer != null) {
					x = xTransformer.transform(x);
				}

				int y = Integer.parseInt(reader.get(yColumn));
				if (yTransformer != null) {
					y = yTransformer.transform(y);
				}
	 
				boolean yn = reader.get(prohibitedColumn).equalsIgnoreCase(maskChar);
 				logger.debug(yn);

				Cell cell = region.getCell(x, y);
				
				cell.setFRmutable(yn);
  
			}


		} catch (Exception exception) {
			exception.printStackTrace();
			logger.fatal("Land Use Controlling Institution failed: " + exception.toString());

			System.exit(0);
		}
 

	}

 



	
	/**
	 * Use the csv file to set the capital levels for the cells
	 * @param file
	 * @throws IOException 
	 */
	void applyFile( CsvReader file ) throws IOException
	{
		//Assume we've got the CSV file, and we've read the headers in
		while( file.readRecord() ) //For each entry
		{
			//Try to get the cell
			Cell cell = region.getCell( Integer.parseInt( file.get(xColumn) ), Integer.parseInt( file.get( yColumn ) ) );
			
			if( cell == null ) //Complain if we couldn't find it - implies there's data that doesn't line up!
			{
				logger.warn("Update for unknown cell:" + file.get(xColumn) + ", " + file.get(yColumn));
				continue; //Go to next line
			}
 
		}
	}
	
	
	
	 
	
	
	/**
	 * Do annual updating
	 */
	@Override
	public void update()
	{
		super.update();
		logger.info(this + "in update() @TODO apply new YN marker");

		try {
//			CsvReader file = getFileForYear();
// 			if( file != null ) {
//				applyFile( file );
//			}
		} catch ( Exception e )
		{
			logger.fatal( "Couldn't update Capitals: " + e.getMessage() );
			e.printStackTrace();
		}
	}
	
	
	// @TODO
	//		keeps applied until the next restriction rule applied 


	/**
	 * If there's a file to be applied this year, then get it.
	 * Next, check to see if the year is in the filename, and there's a file that matches.
	 * Finally if we should re-apply the same file (e.g. if there is time-varying noise being added), return that.
	 * Otherwise, return null
	 * @return
	 * @throws IOException 
	 */
	
//	CsvReader getFileForYear() throws IOException { @TODO create an updater xml file first
//		ABMPersister persister = ABMPersister.getInstance();
//		String fn = null;
//		String yearly = yearlyFilenames.get( info.getSchedule().getCurrentTick() );
//		if (yearly != null
//				&& persister.csvFileOK(getClass(), yearly, region.getPersisterContextExtra(), X_COL, Y_COL)) {
//			fn = yearly;
//		} else if (yearInFilename
//				&& persister.csvFileOK(getClass(), filename, region.getPersisterContextExtra(), X_COL, Y_COL)) {
//			fn = filename;
//		} else if( reapplyPreviousFile && previousFilename != null ) {
//			fn = previousFilename;
//		}
//		
//		logger.debug("Read " + fn);
//
//		
//		if( fn != null )
//		{
//			previousFilename = fn;
//			return persister.getCSVReader(fn, region.getPersisterContextExtra());
//		}
//		
//  	
//		return null;
//	}
//	

	

	
	/**
	 * Checks configured restriction CSV file.
	 * 
	 * @param fr
	 * @param cell
	 * @return true if it is chnage the FR of the given cell.
	 */
	@Override
	public boolean isAllowed(FunctionalRole fr, Cell cell) {
 
		boolean landuseallowed = cell.getFRmutable();

		if (landuseallowed) {
			// <- LOGGING
			logger.debug("Land use change allowed X" + cell.getX() + "Y" + cell.getY());
			// LOGGING ->
			return true;
		} else {
			logger.debug("Land use change prohibited X" + cell.getX() + "Y" + cell.getY());

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


 

