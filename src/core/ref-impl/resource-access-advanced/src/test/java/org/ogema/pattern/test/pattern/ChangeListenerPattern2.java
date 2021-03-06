/**
 * This file is part of OGEMA.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ogema.pattern.test.pattern;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.sensors.TemperatureSensor;

public class ChangeListenerPattern2 extends ResourcePattern<PhysicalElement> {

	public ChangeListenerPattern2(Resource match) {
		super(match);
	}
	
	public final TemperatureSensor tempSens = model.getSubResource("temperatureSensor",TemperatureSensor.class);
	
	public final TemperatureResource reading = tempSens.reading();

	@ChangeListener
	@Existence(required=CreateMode.OPTIONAL)
	public final IntegerResource type = tempSens.readingType();
	
	
}
