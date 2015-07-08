/**
 * This file is part of
 * 
 * CRAFTY - Competition for Resources between Agent Functional TYpes
 *
 * Copyright (C) 2015 School of GeoScience, University of Edinburgh, Edinburgh, UK
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
 * Created by Sascha Holzhauer on 25 May 2015
 */
package org.volante.abm.lara;

import org.apache.log4j.Logger;
import org.volante.abm.agent.bt.LaraBehaviouralComponent;
import org.volante.abm.decision.pa.CraftyPa;

import de.cesr.lara.components.agents.impl.LDefaultAgentComp;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.eventbus.events.LAgentDecideEvent;
import de.cesr.lara.components.eventbus.events.LAgentExecutionEvent;
import de.cesr.lara.components.eventbus.events.LAgentPreprocessEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.impl.LDcSpecificEventbus;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.parma.core.PmParameterManager;

/**
 * Prevents the agent component from subscribing to LAgent* events (since these
 * subscriptions are performed on demand).
 * 
 * @author Sascha Holzhauer
 * 
 */
public class CobraLAgentComp extends
		LDefaultAgentComp<LaraBehaviouralComponent, CraftyPa<?>> {

	/**
	 * @param agent
	 * @param env
	 */
	public CobraLAgentComp(LaraBehaviouralComponent agent, LaraEnvironment env) {
		super(agent, env);
	}

	/**
	 * @param model
	 * @param lbc
	 * @param env
	 */
	public CobraLAgentComp(LaraModel model, LaraBehaviouralComponent lbc,
			LaraEnvironment env) {
		super(model, lbc, env);
	}

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(CobraLAgentComp.class);

	@Override
	public void setLaraModel(LaraModel lmodel) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.info("Set new id object (renew eventbus and subscirbe events subsequently)");
		}
		// LOGGING ->

		this.lmodel = lmodel;
		this.eventBus = this.lmodel.getLEventbus();

	}

	/**
	 * @param dc
	 */
	public void subscribeOnce(LaraDecisionConfiguration dc) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(this.agent + "> Subscribe once for " + dc);
		}
		// LOGGING ->

		LDcSpecificEventbus eb = (LDcSpecificEventbus) this.eventBus;

		eb.subscribeOnce(this, LAgentPreprocessEvent.class, dc);
		eb.subscribeOnce(this, LAgentDecideEvent.class, dc);
		eb.subscribeOnce(this, LAgentExecutionEvent.class, dc);
	}

	public void onInternalEvent(LaraEvent event) {
		// Create eventbus instance here to account for region-specific
		// parameter manager (which is not known to the preprocessor where the
		// eventbus is usually initiated):
		LEventbus.getNewInstance(this.agent, PmParameterManager
				.getInstance(this.agent.getAgent().getRegion()));
		super.onInternalEvent(event);
	}
}
