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
package org.ogema.channels;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.ogema.core.application.AppID;
import org.ogema.core.application.Application;
import org.ogema.core.channelmanager.ChannelConfiguration;
import org.ogema.core.channelmanager.ChannelConfiguration.Direction;
import org.ogema.core.channelmanager.driverspi.ChannelLocator;
import org.ogema.core.channelmanager.driverspi.DeviceLocator;
import org.ogema.core.channelmanager.driverspi.SampledValueContainer;
import org.ogema.core.channelmanager.driverspi.ValueContainer;
import org.ogema.core.channelmanager.measurements.IntegerValue;
import org.osgi.framework.Bundle;

public class UnconfiguredChannelsTest {

	private ChannelManagerImpl channelManager;

	private ChannelDriverImpl driver;

	private ApplicationRegistryImpl appReg;
	
	@Before
	public void setup() {
		
		channelManager = new ChannelManagerImpl();
		
		appReg = new ApplicationRegistryImpl();
		appReg.appId = new AppIdImpl("foo");
		
		channelManager.appreg = appReg;
		channelManager.permMan = new PermissionManagerImpl();
		
		driver = new ChannelDriverImpl("driver1", "firstDriver");
		channelManager.addDriver(driver);
	}
	
	@Test
	public void testReadUnconfiguredChannels() throws Exception {
		DeviceLocator deviceLocator = new DeviceLocator(driver.getDriverId(), "", "", null);
		ChannelLocator channelLocator = new ChannelLocator("1", deviceLocator); 
		
		driver.channels.put(channelLocator, new AtomicInteger());

		ChannelLocator channelLocator2 = new ChannelLocator("2", deviceLocator); 
		
		driver.channels.put(channelLocator2, new AtomicInteger());

		ChannelConfiguration configuration = channelManager.addChannel(channelLocator, Direction.DIRECTION_INPUT, ChannelConfiguration.NO_READ_NO_LISTEN);
		
		assertEquals(1, driver.channelAddedCount);
		assertEquals(0, driver.channelRemovedCount);
		
		channelManager.getChannelValue(configuration);
		
		List<SampledValueContainer> list = new ArrayList<SampledValueContainer>();
		list.add(new SampledValueContainer(channelLocator));
		list.add(new SampledValueContainer(channelLocator2));
		
		channelManager.readUnconfiguredChannels(list);
		
		assertEquals(2, driver.channelAddedCount);
		assertEquals(1, driver.channelRemovedCount);
		
		assertEquals(1, driver.addedChannels.size());
		assertTrue(driver.addedChannels.contains(channelLocator));
		assertFalse(driver.addedChannels.contains(channelLocator2));
		
		assertEquals(2, list.size());
		assertEquals(1, list.get(0).getSampledValue().getValue().getIntegerValue());
		assertEquals(0, list.get(1).getSampledValue().getValue().getIntegerValue());
	}
	
	@Test
	public void testWriteUnconfiguredChannels() throws Exception {
		DeviceLocator deviceLocator = new DeviceLocator(driver.getDriverId(), "", "", null);
		ChannelLocator channelLocator = new ChannelLocator("1", deviceLocator); 
		
		driver.channels.put(channelLocator, new AtomicInteger());

		ChannelLocator channelLocator2 = new ChannelLocator("2", deviceLocator); 
		
		driver.channels.put(channelLocator2, new AtomicInteger());

		@SuppressWarnings("unused")
		ChannelConfiguration configuration = channelManager.addChannel(channelLocator, Direction.DIRECTION_INPUT, ChannelConfiguration.NO_READ_NO_LISTEN);
		
		assertEquals(1, driver.channelAddedCount);
		assertEquals(0, driver.channelRemovedCount);
		
		List<ValueContainer> list = new ArrayList<ValueContainer>();
		list.add(new ValueContainer(channelLocator, new IntegerValue(42)));
		list.add(new ValueContainer(channelLocator2, new IntegerValue(43)));
		
		channelManager.writeUnconfiguredChannels(list);
		
		assertEquals(2, driver.channelAddedCount);
		assertEquals(1, driver.channelRemovedCount);
		
		assertEquals(1, driver.addedChannels.size());
		assertTrue(driver.addedChannels.contains(channelLocator));
		assertFalse(driver.addedChannels.contains(channelLocator2));
		
		assertEquals(42, driver.channels.get(channelLocator).get());
		assertEquals(43, driver.channels.get(channelLocator2).get());
	}

	private class AppIdImpl implements AppID {

		String id;
		
		AppIdImpl(String id) {
			this.id = id; 
		}
		
		@Override
		public String getIDString() {
			return id;
		}

		@Override
		public String getLocation() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Bundle getBundle() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Application getApplication() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getOwnerUser() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getOwnerGroup() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getVersion() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
