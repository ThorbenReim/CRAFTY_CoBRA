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
		addAgent("NNWCons", Color.pink.brighter());
		addAgent("Bioenergy", Color.cyan.darker());
		addAgent("EA", Color.orange.darker());
		addAgent("EP", Color.red.darker());
		addAgent("IAfodder", Color.yellow.brighter());
		addAgent("IAfood", Color.yellow.darker());
		addAgent("PNNB", Color.red.brighter());
		addAgent("MMW", Color.green.brighter());		
		addAgent("AF", Color.orange.brighter());
		addAgent("PNB", Color.gray.darker());	
		addAgent("PNC", Color.green.darker());	
		addAgent("IP", Color.pink.darker());
		addAgent("PNNC", Color.blue.brighter());
		addAgent("SusAr", Color.cyan.brighter());
		addAgent("VEP", Color.magenta.darker());	
		addAgent("Lazy FR", Color.blue.darker());	

	}
}
