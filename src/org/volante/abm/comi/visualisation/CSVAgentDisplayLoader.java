/**
 * This file is part of
 * 
 * CRAFTY - Competition for Resources between Agent Functional TYpes
 *
 * Copyright (C) 2021
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
 */

package org.volante.abm.comi.visualisation;

import org.simpleframework.xml.Attribute;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Regions;
import org.volante.abm.schedule.RunInfo;
import org.volante.abm.visualisation.AgentTypeDisplay;
import com.csvreader.CsvReader;
import java.awt.Color;
 
import org.apache.log4j.Logger;


// Read agent colors from a csv file (Apr21 by ABS)

public class CSVAgentDisplayLoader extends AgentTypeDisplay {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5182974347882438506L;
 
	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(CSVAgentDisplayLoader.class);


	@Attribute(name = "file", required= true)
	String	file		= "";
	@Attribute(name = "indexed", required = false)
	boolean	indexed		= true;
	@Attribute(name = "nameColumn", required = false)
	String	nameColumn	= "Name";
	@Attribute(name = "colorColumn", required = false)
	String	colorColumn	= "Color";

	public CSVAgentDisplayLoader() {
		logger.info("Read agent colors from a csv file:" + file);
	}

	@Override
	public void initialise(ModelData data, RunInfo info, Regions region) throws Exception {
		super.initialise(data, info, region);
		CsvReader reader = info.getPersister().getCSVReader(file, null);

		while (reader.readRecord()) {
			String aft = reader.get(nameColumn);
			logger.info(aft + ": " + reader.get(colorColumn));
			addAgent(aft, Color.decode(reader.get(colorColumn))); //conver #hexcode to java.awt.Color
		}

	}
}


