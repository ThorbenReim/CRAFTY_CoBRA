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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.volante.abm.agent.Agent;
import org.volante.abm.agent.fr.FunctionalRole;
import org.volante.abm.agent.fr.VariantProductionFR;
import org.volante.abm.data.Cell;
import org.volante.abm.data.Region;


/**
 * TODO seemed to depend on execution order > make clean to guarantee determiend start conditions
 * 
 * @author Sascha Holzhauer
 * 
 */
public class GiveUpGiveInAllocationTest extends BasicTestsUtils
{
	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(GiveUpGiveInAllocationTest.class);

	@Test
	public void testSimpleAllocation() throws Exception
	{
		//Make an allocation model etc.
		GiveUpGiveInAllocationModel allocation = new GiveUpGiveInAllocationModel();
		allocation.numTakeovers = "1";
		allocation.numCells = "10";
		allocation.probabilityExponent = 1;
		allocation = persister.roundTripSerialise( allocation );
		RegionalDemandModel demand = new RegionalDemandModel();
		SimpleCompetitivenessModel competition = new SimpleCompetitivenessModel();
		competition.setRemoveNegative( true ); //Makes the maths easier if we ignore oversupply
		
		//Create the region
		Region r1 = new Region(allocation, competition, demand,
				behaviouralTypes, functionalRoles, c11);
		r1.initialise( modelData, runInfo, r1 );

		FunctionalRole persistentForestry = new VariantProductionFR(
				"Forestry",	forestryProduction, 20,	-20);
		
		assertEquals(functionalRoles, r1.getFunctionalRoles()); // Check the
																// agents are in
																// the region
																// correctly
		logger.info(r1.getFunctionalRoles());
		logger.info(r2.getFunctionalRoles());
		
		
		r1.setOwnership(this.agentAssemblerR1.assembleAgent(null, "Pseudo",
				persistentForestry.getLabel()), c11); // Start with a forester

		c11.setBaseCapitals( capitals( 1,1,1,1,1,1,1 ) ); //Make a perfect cell
		demand.setDemand( services(0,0,1,0) ); //Set the demand to just be for food
		
		assertEqualMaps(services(0, 10, 0, 4),
				persistentForestry.getExpectedSupply(c11)); // Check
																					// that
																					// both
																					// have
																					// full
																					// productivity
		assertEqualMaps(services(1, 0, 7, 4), farming.getExpectedSupply(c11));
		
		//Farming should have competitiveness proportional to the demand for food
		assertEquals( 7, r1.getCompetitiveness( farming, c11 ), 0.0001 );
		//And forestry should be 0 - no demand for timber
		assertEquals(0, r1.getCompetitiveness(persistentForestry, c11), 0.0001);
		
		//When we allocate the land initially, the forester should stay there
		r1.getAllocationModel().allocateLand( r1 );
		assertEquals(forestry.getSerialID(), c11.getOwnersFrSerialID());
		
		//But when we up the demand, the farmer should force the forester out
		//Not at 14 competitiveness
		demand.setDemand( services(0,0,2,0) ); 
		assertEquals( 14, r1.getCompetitiveness( farming, c11 ), 0.0001 );
		r1.getAllocationModel().allocateLand( r1 );
		assertEquals(persistentForestry.getSerialID(),
				c11.getOwnersFrSerialID());
		
		//But at 21
		demand.setDemand( services(0,0,3,0) ); 
		assertEquals( 21, r1.getCompetitiveness( farming, c11 ), 0.0001 );
		r1.getAllocationModel().allocateLand( r1 );
		assertEquals(farming.getSerialID(), c11.getOwnersFrSerialID()); // And
																		// the
																// farmer's
																// taken over
	}

	@Test
	public void testCreatingIndividualAgentsWithVariation() throws Exception
	{	
		//Models to use
		GiveUpGiveInAllocationModel allocation = new GiveUpGiveInAllocationModel();
		allocation.numTakeovers = "1";
		allocation.numCells = "1";
		allocation.probabilityExponent = 1;
		allocation = persister.roundTripSerialise( allocation );
		RegionalDemandModel demand = new RegionalDemandModel();
		SimpleCompetitivenessModel competition = new SimpleCompetitivenessModel();
		competition.setRemoveNegative( true ); //Makes the maths easier if we ignore oversupply

		// Make it hard to give in and up
		FunctionalRole persistentForestry = new VariantProductionFR(
				"Forestry", forestryProduction, 20, -20);

		// Cells
		Cell c1 = new Cell(0,0);
		
		//Create the region
		Region r = new Region(allocation, competition, demand,
				behaviouralTypes, functionalRoles, c1);
		r.initialise( modelData, runInfo, r );
		
		c1.setBaseCapitals( capitals( 1,1,1,1,1,1,1 ) ); //Perfect cell
		
		demand.setDemand( services(0,1,1,0) ); // demands so that neither has much comp advantage
		
		// Check the supply levels from the forester (10 timber, 4 recreation max)
		assertEqualMaps(services(0, 10, 0, 4),
				persistentForestry.getExpectedSupply(c1));
		
		// Check supply levels from potential farmer
		assertEqualMaps(services(1, 0, 7, 4), farming.getExpectedSupply(c1));
		
		assertEquals(10.0, r.getCompetitiveness(persistentForestry, c1), 0.0001);
		assertEquals( 7.0, r.getCompetitiveness( farming, c1 ), 0.0001 );

		// Give cell to ordinary forester
		r1.setOwnership(this.agentAssemblerR1.assembleAgent(c1, "Pseudo",
				persistentForestry.getLabel()), c1);
		
		assertEquals( functionalRoles, r.getFunctionalRoles() ); //Check the agents are in the region correctly

		// Check competitivenesses (a forester has no value as one already in place; a farmer still has 7.0
		assertEquals(0.0, r.getCompetitiveness(persistentForestry, c1), 0.0001);
		assertEquals( 7.0, r.getCompetitiveness( farming, c1 ), 0.0001 );

		// Now set thresholds:
		// A normal forester will not give in to a farmer...
		persistentForestry = new VariantProductionFR("Forestry",
				forestryProduction, 7.25, -20);

		// A normal farmer will not give in to a forester...
		FunctionalRole newFarmer = new VariantProductionFR("Forestry",
				forestryProduction, 10.5, -20);

		//Now if we allocate land, the forester should stay there
		r.getAllocationModel().allocateLand( r );
		assertEquals(forestry.getSerialID(), c1.getOwnersFrSerialID());
		
		// Now replace the forester with a variant forester

		FunctionalRole vForest = runInfo.getPersister().readXML(
				FunctionalRole.class, "xml/VariantForester1.xml");
		vForest.initialise(modelData, runInfo, r);

		r1.setOwnership(
				this.agentAssemblerR1.assembleAgent(c1, "Pseudo",
						vForest.getLabel()), c1);
		
		// Check the base variant agent is as expected
		assertEquals(vForest.getLabel(), c1.getOwnersFrLabel());
		assertEquals(-20, vForest.getAssociatedGivingUpThreshold(), 0.0001);
		assertEquals(7.25, vForest.getAssociatedGivingInThreshold(), 0.0001);
		assertEquals("VariantForester1", vForest.getLabel());
		assertEquals(1, vForest.getSerialID());
		
		// The base agent has thresholds the same as the simple forester,
		// but the giving-in threshold is then drawn from a [6,6.4] Unif dist.
		// So, farmer should take over....
		r.getAllocationModel().allocateLand( r );
		assertEquals(farming.getSerialID(), c1.getOwnersFrSerialID());
		
		// Now test a variable giving-in farmer:
		// Start with the current farmer, check comp and persistence:
		assertEquals( 10.0, r.getCompetitiveness( forestry, c1 ), 0.0001 );
		assertEquals( 0.0, r.getCompetitiveness( farming, c1 ), 0.0001 );

		// This farmer should not give up or give in:
		r.getAllocationModel().allocateLand( r );
		assertEquals(farming.getSerialID(), c1.getOwnersFrSerialID());
		
		// Now give the land to a variant farmer with a higher giving up distribution [10,11]
		// This farmer should then give up, and the land will go to the ordinary forester
		FunctionalRole vFarmer = runInfo.getPersister().readXML(
				FunctionalRole.class, "xml/VariantFarmer1.xml");
		vFarmer.initialise(modelData, runInfo, r);

		Agent farmer = this.agentAssemblerR1.assembleAgent(c1, "Pseudo",
				vFarmer.getLabel());
		r1.setOwnership(farmer, c1);

		// Check the base variant agent is as expected
		assertEquals(vFarmer.getSerialID(), c1.getOwnersFrSerialID());
		assertEquals( -20, vFarmer.getAssociatedGivingUpThreshold(), 0.0001 );
		assertEquals( 10.5, vFarmer.getAssociatedGivingInThreshold(), 0.0001 );
		assertEquals("VariantFarmer1", farmer.getFC().getFR().getLabel());
		assertEquals(11, vFarmer.getSerialID());
		
		// Check the actual agent has the correct distribution of Giving Up values
		assertTrue(farmer.getProperty(AgentPropertyIds.GIVING_UP_THRESHOLD) >= 10);
		assertTrue(farmer.getProperty(AgentPropertyIds.GIVING_UP_THRESHOLD) <= 11);

		// The farmer should now give up, and the normal forester should take his place
		farmer.considerGivingUp();
		r.getAllocationModel().allocateLand( r );
		assertEquals(forestry.getSerialID(), c1.getOwnersFrSerialID());
	}
}
