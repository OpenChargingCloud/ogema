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
package org.ogema.model.communication;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.communication.CommunicationInformation;

/**
 * Communication information for a Modbus driver 
 */
public interface ModbusCommunicationInformation extends CommunicationInformation {
	
	/**
	 * Modbus communication address
	 */
	ModbusAddress comAddress();
	
	/**
	 * Offset for interpretation of numerical values 
	 * (for Modbus readings: physical value = factor * received value + offset) 
	 */
	FloatResource offset();
	
	/**
	 * Factor for interpretation of numerical values 
	 * (for Modbus readings: physical value = factor * received value + offset)  
	 */
	FloatResource factor();
	
}