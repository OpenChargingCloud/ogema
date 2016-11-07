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
package org.ogema.resourcemanager.impl.model.simple;

import org.ogema.core.model.schedule.AbsoluteSchedule;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.resourcemanager.impl.ApplicationResourceManager;
import org.ogema.resourcemanager.impl.model.schedule.HistoricalSchedule;
import org.ogema.resourcemanager.virtual.VirtualTreeElement;

/**
 * 
 * @author jlapp
 */
public class DefaultIntegerResource extends SingleValueResourceBase implements IntegerResource {

	public DefaultIntegerResource(VirtualTreeElement el, String path, ApplicationResourceManager resMan) {
		super(el, path, resMan);
	}

	@Override
	public int getValue() {
		checkReadPermission();
		return getEl().getData().getInt();
	}

	@Override
	public boolean setValue(int value) {
		if (!exists() || !hasWriteAccess()) {
			return false;
		}
		checkWritePermission();
		boolean changed = value != getTreeElement().getData().getInt();
		getTreeElement().getData().setInt(value);
		handleResourceUpdate(changed);
		return true;
	}

	@Override
	public RecordedData getHistoricalData() {
		checkReadPermission();
		return getResourceDB().getRecordedData(getEl());
	}

	@Override
	public AbsoluteSchedule forecast() {
		return getSubResource("forecast", AbsoluteSchedule.class);
	}

	@Override
	public AbsoluteSchedule program() {
		return getSubResource("program", AbsoluteSchedule.class);
	}

	@Override
	public AbsoluteSchedule historicalData() {
		return getSubResource(HistoricalSchedule.PATH_IDENTIFIER, AbsoluteSchedule.class);
	}

}
