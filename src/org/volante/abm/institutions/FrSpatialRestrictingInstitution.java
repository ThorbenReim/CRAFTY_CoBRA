/**
 * 
 */
package org.volante.abm.institutions;


import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;
import org.volante.abm.agent.Agent;
import org.volante.abm.agent.fr.FunctionalRole;
import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.example.AgentPropertyIds;
import org.volante.abm.schedule.RunInfo;
import org.volante.abm.serialization.ABMPersister;

import com.google.common.collect.Table;


/**
 * @author Sascha Holzhauer
 *
 */
public class FrSpatialRestrictingInstitution extends FrRestrictingInstitution {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(FrRestrictingInstitution.class);

	@Element(required = true)
	protected String spatialLayer;


	/**
	 * Checks configured restriction CSV file.
	 * 
	 * @param fr
	 * @param cell
	 * @return true if the given {@link FunctionalRole} is allowed to occupy the given cell.
	 */
	@Override
	public boolean isAllowed(FunctionalRole fr, Cell cell) {

		boolean masked = (boolean) cell.getObjectProperty(AgentPropertyIds.valueOf(spatialLayer));

		if (!masked) {
			return true; // does not care as it is not covered 
		}

		String label2request =
				(cell.getOwner().getFC().getFR().getLabel().equals(Agent.NOT_MANAGED_FR_ID) ? this.labelUnmanaged
						: cell.getOwner().getFC().getFR().getLabel());
		if (!restrictedRoles.contains(label2request, fr.getLabel())) {
			// <- LOGGING
			logger.warn("Allowed Types Map does not contain an entry for " + label2request
					+ " > " + fr.getLabel() + "! Assuming 0.");
			// LOGGING ->
			return true;
		} else {
			return restrictedRoles.get(label2request, fr.getLabel()) <= 0;
		}

	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ("FR spatial restricting institution (" + spatialLayer + ")") ;
	}

}
