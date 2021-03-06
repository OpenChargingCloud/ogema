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
package org.ogema.model.devices.generators;

import org.ogema.model.connections.ElectricityConnection;

/**
 * Base class for an energy generating device or information about a device's
 * energy generation part. Energy generating devices should extend this model.
 * This model itself should only be used if no suitable more specialized model
 * exists.
 */
public interface ElectricityGenerator extends GenericEnergyGenerator {

	/**
	 * Electricity connection that this device is connected to.
	 */
	ElectricityConnection electricityConnection();
}
