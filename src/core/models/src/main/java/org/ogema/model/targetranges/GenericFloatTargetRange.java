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
package org.ogema.model.targetranges;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.ranges.GenericFloatRange;

/**
 * Target range for generic float ranges. Should only be used if no more
 * specialized target range is available. The meaning and units of the values
 * must be defined somewhere in the parent resources, in this case.
 */
public interface GenericFloatTargetRange extends TargetRange {

	@Override
	FloatResource setpoint();

	@Override
	GenericFloatRange controlLimits();

	@Override
	GenericFloatRange targetRange();

	@Override
	GenericFloatRange alarmLimits();
}
