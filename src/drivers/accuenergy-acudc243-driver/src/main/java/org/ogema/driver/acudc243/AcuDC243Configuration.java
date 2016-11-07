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
package org.ogema.driver.acudc243;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;

/**
 * Configuration Resource Model for the AcuDC243 device driver
 * 
 * @author pau
 * 
 */
public interface AcuDC243Configuration extends Resource {

	/** Interface ID for channel driver interface */
	StringResource interfaceId();

	/** Device Address for channel driver interface */
	StringResource deviceAddress();

	/** Device Parameter for channel driver interface */
	StringResource deviceParameters();

	/** Update interval in ms */
	IntegerResource timeout();

	/** Name of the ElectricityMeter resource to be created for this configuration */
	StringResource resourceName();
}
