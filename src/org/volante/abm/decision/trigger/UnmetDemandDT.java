/**
 * 
 */
package org.volante.abm.decision.trigger;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.volante.abm.agent.Agent;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Regions;
import org.volante.abm.data.Service;
import org.volante.abm.schedule.RunInfo;
import org.volante.abm.schedule.WorldSyncSchedule;
import org.volante.abm.serialization.GloballyInitialisable;


/**
 * @author Sascha Holzhauer
 *
 */
public class UnmetDemandDT extends AbstractDecisionTrigger implements GloballyInitialisable {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(UnmetDemandDT.class);

	@ElementList(required = true, entry = "consideredService", inline = true)
	List<String> serialConsideredServices = new ArrayList<>();

	@Element(required = false)
	protected int startTick = 0;

	protected List<Service> consideredServices = new ArrayList<>();

	@Element(required = false)
	protected double thresholdFraction = 0.2;

	protected Regions regions = null;
	protected RunInfo rInfo = null;
	protected ModelData mData = null;

	public void initialise(ModelData mData, RunInfo info, Regions regions) throws Exception {
		this.regions = regions;
		this.rInfo = info;
		this.mData = mData;

		for (String serialService : serialConsideredServices) {
			if (mData.services.forName(serialService) != null) {
				consideredServices.add(mData.services.forName(serialService));
			} else {
				logger.warn("The specified service (" + serialService
						+ ") for the subsidy is not defined in the model!");
			}
		}
	}

	/**
	 * @param agent
	 * @return true if the formal criteria for decision triggering are fulfilled.
	 */
	protected boolean checkFormal(Agent agent) {
		if (this.rInfo == null) {
			throw new IllegalStateException("UnmetDemandDT has not been initialised!");
		}
		if (this.rInfo.getSchedule() instanceof WorldSyncSchedule) {
			return (this.startTick <= this.rInfo.getSchedule().getCurrentTick());
		} else {
			logger.error("The schedule needs to be of type 'WorldSyncSchedule' to provide a 'WorldSyncModel'");
			throw new IllegalStateException(
			        "The schedule needs to be of type 'WorldSyncSchedule' to provide a 'WorldSyncModel'");
		}
	}
	/**
	 * @see org.volante.abm.decision.trigger.DecisionTrigger#check(org.volante.abm.agent.Agent)
	 */
	@Override
	public boolean check(Agent agent) {
		if (this.checkFormal(agent)) {
			for (Service service : this.consideredServices) {
				// get total demand across regions (account for distributed regions)
				double demand =
				        ((WorldSyncSchedule) this.rInfo.getSchedule()).getWorldSyncModel().getWorldDemand()
				                .get(service);
				// get total supply across regions (account for distributed regions)
				double difference =
				        demand
				                -
				        ((WorldSyncSchedule) this.rInfo.getSchedule()).getWorldSyncModel().getWorldSupply()
				                        .get(service);

				// <- LOGGING
				logger.info("> " + service + ": " + difference / demand + " (" + this.thresholdFraction + ")");
				// LOGGING ->

				if (difference > demand * this.thresholdFraction) {
					return true;
				}
			}
		}
		return false;
	}

	public String toString() {
		return "UnmetDemandTrigger (" + this.consideredServices.toString() + ")";
	}
}

