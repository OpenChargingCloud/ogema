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
package org.ogema.core.model.units;

import org.ogema.core.model.simple.FloatResource;

/**
 * Resource representing a planar angle.
 */
public interface AngleResource extends PhysicalUnitResource {

	/**
	 * Returns the angle in degrees.
	 *
	 * @see FloatResource#getValue()
	 */
	@Override
	float getValue();

	/**
	 * Sets the angle to a new value. Unit is degrees.
	 *
	 * @see FloatResource#getValue()
	 */
	@Override
	boolean setValue(float value);

	/**
	 * Returns "°".
	 *
	 * @see PhysicalUnitResource#getUnit()
	 */
	@Override
	PhysicalUnit getUnit();
}
