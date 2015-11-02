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
package org.volante.abm.example;


import static org.volante.abm.example.SimpleCapital.HUMAN;
import static org.volante.abm.example.SimpleService.HOUSING;
import static org.volante.abm.example.SimpleService.simpleServices;

import org.junit.Before;
import org.junit.Test;
import org.volante.abm.data.Service;

import com.moseph.modelutils.fastdata.DoubleMap;


public class DynamicMaxProductionTest extends BasicTestsUtils {

	public final String DYNAMIC_MAX_PRODUCTION_XML_FILE = "xml/DynamicMaxProduction.xml";

	DynamicMaxProductionModel prodModel = null;

	DoubleMap<Service> production = new DoubleMap<>(simpleServices);

	/*
	 * Capitals: HUMAN(0), INFRASTRUCTURE(1), ECONOMIC(2), NATURAL_GRASSLAND(3), NATURAL_FOREST(4),
	 * NATURAL_CROPS(5), NATURE_VALUE(6)
	 * 
	 * Services: HOUSING(0), TIMBER(1), FOOD(2), RECREATION(3),
	 */

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// init institution
		persister = runInfo.getPersister();
		try {
			this.prodModel =
					persister.read(DynamicMaxProductionModel.class,
							persister.getFullPath(DYNAMIC_MAX_PRODUCTION_XML_FILE, this.r1.getPeristerContextExtra()));
			this.prodModel.initialise(modelData, runInfo, r1);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Test
	public void testProduction() {
		c11.setBaseCapitals(capitals(1, 1, 1, 1, 1, 1, 1));

		checkProduction("As is in file", Math.pow(1, 0.5), Math.pow(1, 0.5) * 2, 0.5, 1.0 / 3);

		c11.setBaseCapitals(capitals(1, 1, 2, 1, 1, 1, 1));

		checkProduction("ECONOMIC=2", Math.pow(1, 0.5) * 8, 2, 2, 1.0 / 3);

		prodModel.setWeight(HUMAN, HOUSING, 1);
		checkProduction("Weight for HOUSING reg. HUMAN = 1", Math.pow(1, 0.5) * 8, 2, 2, 1.0 / 3);
	}

	void checkProduction(String msg, double... vals) {
		checkProduction(msg, services(vals));
	}


	void checkProduction(String msg, DoubleMap<Service> expected) {
		prodModel.production(c11, production);
		assertEqualMaps(msg, expected, production);
	}
}
