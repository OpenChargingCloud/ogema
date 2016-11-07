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
package org.ogema.serialization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.units.PhysicalUnitResource;

import static org.ogema.serialization.JaxbResource.NS_OGEMA_REST;

/**
 * 
 * @author jlapp
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "FloatResource", namespace = NS_OGEMA_REST, propOrder = { "value", "unit" })
@XmlRootElement(name = "resource", namespace = NS_OGEMA_REST)
public class JaxbFloat extends JaxbResource {

	JaxbFloat(FloatResource r, SerializationStatus serStat) {
		super(r, serStat);
	}

	protected JaxbFloat() {
		throw new UnsupportedOperationException();
	}

	@XmlElement
	public float getValue() {
		return ((FloatResource) res).getValue();
	}

	@XmlElement
	public String getUnit() {
		if (res instanceof PhysicalUnitResource) {
			return ((PhysicalUnitResource) res).getUnit().toString();
		}
		return null;
	}

}
