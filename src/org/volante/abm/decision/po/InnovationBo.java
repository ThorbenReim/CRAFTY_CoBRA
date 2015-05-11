/**
 * 
 */
package org.volante.abm.decision.po;


import java.util.Map;

import org.volante.abm.agent.bt.LaraBehaviouralComponent;

import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * @author Sascha Holzhauer
 *
 */
public class InnovationBo extends
 CraftyPo<InnovationBo> {

	public InnovationBo(String key, LaraBehaviouralComponent comp) {
		super(key, comp);
	}


	@Override
	public InnovationBo getModifiedBO(LaraBehaviouralComponent comp,
			Map preferenceUtilities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map getSituationalUtilities(LaraDecisionConfiguration dBuilder) {
		// TODO Auto-generated method stub
		return null;
	}
}
