package org.volante.abm.comi.visualisation;

import java.awt.Color;

import org.volante.abm.visualisation.AgentTypeDisplay;

public class UKAgents extends AgentTypeDisplay
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8136199562481069547L;

	public UKAgents()
	{
		addAgent("NWCons", Color.magenta.brighter());//FF00FF
		addAgent("Bioenergy", Color.cyan.darker()); //00B2B2
		addAgent("EA", Color.orange.darker()); 		//B28C00
		addAgent("EP", new Color(251,154,153));     //FB9A99
		addAgent("IAfodder", Color.red.brighter()); //FF0000
		addAgent("IAfood", Color.blue.darker());    //0000b2
		addAgent("PNNB", Color.yellow.brighter() ); //FFFF00
		addAgent("MW", Color.green.brighter());		//00FF00
		addAgent("AF", new Color(31,120,180)); 		//1F78B4
		addAgent("PNB", new Color(255,127,0) );		//ff7f00
		addAgent("PNC", Color.green.darker());		//00b200
		addAgent("PNNC", Color.pink.darker());		//b27a7a
		addAgent("IP",new Color(178,223,138) );		//b2df8a
		addAgent("SusAr", Color.cyan.brighter());	//00ffff
		addAgent("VEP", new Color(51,160,44)); 		//33a02c
		addAgent("Urban", Color.gray.darker() ); 	//595959	
		addAgent("Lazy FR", Color.white.brighter());//ffffff
	}
}

//String.format("%06x", 0xFFFFFF & Color.BLUE.getRGB())

// urban = red, cropland intensity classes from yellow to orange,
// cropland is yellow/orange/brown, 
// urban is red (or sometimes black), 
// water is blue, grassland is light green, forest is dark green. 
// Natural areas perhaps brown or purple, ...