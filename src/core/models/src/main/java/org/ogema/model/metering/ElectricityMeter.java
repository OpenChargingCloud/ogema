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
package org.ogema.model.metering;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.model.units.EnergyResource;
import org.ogema.core.model.units.PowerResource;
import org.ogema.model.connections.ElectricityConnection;
import org.ogema.model.devices.connectiondevices.ElectricityConnectionBox;
import org.ogema.model.smartgriddata.ElectricityPrice;

/**
 * Meter for electricity (electric power and possibly accumulated energy).
 */
public interface ElectricityMeter extends GenericMeter {

	/**
	 * Electricity connection
	 */
	@Override
	ElectricityConnection connection();

	/**
	 * Power readings of the meter. The history of past values should be added
	 * in the definition {@link FloatResource#historicalData() historicalData} field of the reading.
	 */
	PowerResource powerReading();

	/**
	 * Energy readings of the meter. The history of past values should be added
	 * in the definition {@link FloatResource#historicalData() historicalData} field of the reading.
	 */
	EnergyResource energyReading();

	/**
	 * Time at which the energy reading was last reset (i.e. time of reference at
	 * which {@link #energyReading()} started at zero. Past values (past resets)
	 * should be added in the {@link FloatResource#historicalData() historicalData} 
	 * subresource of this field.
	 */
	TimeResource resetTime();

	/**
	 * Type of meter:<br>
	 * 1: two-way meter without nonreturn<br>
	 * 2: consumption meter<br>
	 * 3: generation meter<br>
	 * 4: capacitive reactive power<br>
	 * 5: generation, inductive reactive power<br>
	 * greater/equal 10.000: custom values
	 */
	@Override
	IntegerResource type();

	@Override
	ElectricityPrice price();

	@Override
	ElectricityConnectionBox distributionBox();
}
