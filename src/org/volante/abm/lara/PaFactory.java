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
 * Created by Sascha Holzhauer on 5 Jun 2015
 */
package org.volante.abm.lara;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.volante.abm.agent.bt.LaraBehaviouralComponent;
import org.volante.abm.decision.pa.CraftyPa;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.model.impl.LModel;

/**
 * Applied to deserialise {@link CraftyPa}s.
 * 
 * @author Sascha Holzhauer
 * 
 */
public class PaFactory {

	@Element(required = true)
	String classname = null;

	@ElementList(required = false, entry = "effectiveness")
	Map<String, Double> effectivenessValues = new HashMap<String, Double>();

	@Attribute(required = true, name = "key")
	String key = null;

	public PaFactory() {
	}

	/**
	 * @param lbc
	 * @return potential option
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	public CraftyPa<?> assemblePo(LaraBehaviouralComponent lbc)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException {

		Map<LaraPreference, Double> preferenceMap = new HashMap<LaraPreference, Double>();
		for (Entry<String, Double> entry : effectivenessValues.entrySet()) {
			preferenceMap.put(LModel.getModel(lbc.getAgent().getRegion())
					.getPrefRegistry().get(key), entry.getValue());
		}

		return (CraftyPa<?>) Class
				.forName(classname)
				.getConstructor(String.class, LaraBehaviouralComponent.class,
						Map.class).newInstance(this.key, lbc, preferenceMap);
	}
}
