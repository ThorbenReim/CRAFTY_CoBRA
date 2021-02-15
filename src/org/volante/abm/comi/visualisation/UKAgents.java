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
		addAgent("NWCons", Color.magenta.brighter());
		addAgent("Bioenergy", Color.cyan.darker());
		addAgent("EA", Color.orange.darker());
		addAgent("EP", new Color(251,154,153));
		addAgent("IAfodder", Color.red.brighter());
		addAgent("IAfood", Color.blue.darker());
		addAgent("PNNB", Color.yellow.brighter() );
		addAgent("MW", Color.green.brighter());		
		addAgent("AF", new Color(31,120,180)); // #1f78b4
		addAgent("PNB", Color.gray.darker());	
		addAgent("PNC", Color.green.darker());	
		addAgent("PNNC", Color.pink.darker());
		addAgent("IP",new Color(178,223,138) );// #b2df8a
		addAgent("SusAr", Color.cyan.brighter());
		addAgent("VEP", new Color(51,160,44)); //	#33a02c
		addAgent("Urban", Color.lightGray );	
		addAgent("Lazy FR", Color.white.brighter());	
	}
}
