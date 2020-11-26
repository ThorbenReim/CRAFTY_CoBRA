package org.volante.abm.comi.visualisation;

import java.awt.Color;

import org.volante.abm.visualisation.AgentTypeDisplay;

public class OPMAgents extends AgentTypeDisplay
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8136199562481069547L;

	public OPMAgents()
	{
		addAgent("mgmt_highInt", Color.yellow.brighter());
		addAgent("mgmt_lowInt", Color.green.darker());
		addAgent("mgmt_medInt", Color.green.brighter());
		addAgent("no_mgmt_NOPM", Color.blue.brighter());
		addAgent("no_mgmt_unable", Color.blue.darker());
	}
}
