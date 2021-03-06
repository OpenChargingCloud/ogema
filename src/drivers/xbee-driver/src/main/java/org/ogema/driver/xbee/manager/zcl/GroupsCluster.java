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
package org.ogema.driver.xbee.manager.zcl;

import org.ogema.driver.xbee.Constants;
import org.ogema.driver.xbee.manager.Endpoint;

/**
 * 
 * @author puschas
 * @see ZigBee Cluster Library pages 131-140.
 */
public class GroupsCluster extends Cluster {
	public GroupsCluster(Endpoint endpoint) {
		super((short) 0x0004, endpoint);
		setName("Groups");
		clusterId = 0x0004;
		this.endpoint = endpoint;

		clusterAttributes.put((short) 0x0000, new ClusterAttribute(this, (short) 0x0000, "NameSupport",
				Constants.READ_ONLY, Constants.MANDATORY));

		clusterCommands.put((byte) 0x00, new ClusterCommand(this, (byte) 0x00, "Add group", Constants.MANDATORY));
		clusterCommands.put((byte) 0x01, new ClusterCommand(this, (byte) 0x01, "View group", Constants.MANDATORY));
		clusterCommands.put((byte) 0x02, new ClusterCommand(this, (byte) 0x02, "Get group membership",
				Constants.MANDATORY));
		clusterCommands.put((byte) 0x03, new ClusterCommand(this, (byte) 0x03, "Remove group", Constants.MANDATORY));
		clusterCommands.put((byte) 0x04,
				new ClusterCommand(this, (byte) 0x04, "Remove all groups", Constants.MANDATORY));
		clusterCommands.put((byte) 0x05, new ClusterCommand(this, (byte) 0x05, "Add group if identifying",
				Constants.MANDATORY));
		// TODO: Client responses
	}
}
