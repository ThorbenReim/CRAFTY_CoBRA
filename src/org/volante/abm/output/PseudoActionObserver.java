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
 * Created by Sascha Holzhauer on 3 Nov 2015
 */
package org.volante.abm.output;

import org.volante.abm.decision.pa.CraftyPa;


/**
 * Avoids checks for null in case there is not {@link ActionObserver} registered at the {@link Region}.
 * 
 * @author Sascha Holzhauer
 * 
 */
public class PseudoActionObserver implements ActionObserver {

	/**
	 * @see org.volante.abm.output.ActionObserver#observeAction(org.volante.abm.decision.pa.CraftyPa)
	 */
	@Override
	public void observeAction(CraftyPa<?> pa) {
		// does nothing
	}
}
