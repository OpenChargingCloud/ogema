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

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.ogema.core.channelmanager.ChannelConfiguration;
import org.ogema.core.channelmanager.ChannelConfiguration.Direction;
import org.ogema.core.channelmanager.driverspi.ChannelLocator;
import org.ogema.core.channelmanager.driverspi.ChannelScanListener;
import org.ogema.core.channelmanager.driverspi.DeviceListener;
import org.ogema.core.channelmanager.driverspi.DeviceLocator;
import org.ogema.core.channelmanager.driverspi.DeviceScanListener;

public class RemoveAppTest {

	private ChannelManagerImpl channelManager;

	private ChannelDriverImpl driver;

	private ApplicationRegistryImpl appReg;
	
	private AppIdImpl app1;
	private AppIdImpl app2;
	
	@Rule
	public TestRule watcher = new TestWatcher() {
		
		@Override
		protected void starting(Description description) {
	      System.out.println("Starting test: " + description.getMethodName());
	   }
	};
	
	@Before
	public void setup() {
		
		channelManager = new ChannelManagerImpl();
		
		app1 = new AppIdImpl("1");
		app2 = new AppIdImpl("2");
		
		appReg = new ApplicationRegistryImpl();
		appReg.appId = app1;
		
		channelManager.appreg = appReg;
		channelManager.permMan = new PermissionManagerImpl();
		
		driver = new ChannelDriverImpl("driver1", "firstDriver");
		channelManager.addDriver(driver);
		
		channelManager.activate(null);
		
		assertNotNull(appReg.listener);
	}
	
	private DeviceLocator createDeviceLocator(String driverName, String interfaceName, String deviceAddress, String parameters) {
		return new DeviceLocator(driverName, interfaceName, deviceAddress, parameters);
	}
	
	private ChannelLocator createChannelLocator(String channelAddress, DeviceLocator deviceLocator) {
		return new ChannelLocator(channelAddress, deviceLocator);
	}
	
	@Test
	public void testRemoveApp1() throws Exception {
		
		// open all kinds of long running operations
		// device scans
		// channel scans
		// ondemand channels
		// polled channels
		// listen channels
		// device listener
		
		DeviceLocator deviceLocator = createDeviceLocator(driver.getDriverId(), "foo", "bar", "");
		
		ChannelLocator ondemand = createChannelLocator("1", deviceLocator); 
		ChannelLocator polled = createChannelLocator("2", deviceLocator);
		ChannelLocator listen = createChannelLocator("3", deviceLocator);
		
		driver.channels.put(ondemand, new AtomicInteger());
		driver.channels.put(polled, new AtomicInteger());
		driver.channels.put(listen, new AtomicInteger());
		
		driver.async = true;
		
		Holder holder1 = new Holder(deviceLocator, "1", ondemand, polled, listen);
		
		appReg.appId = app2;
		
		Holder holder2 = new Holder(deviceLocator, "2", ondemand, polled, listen);
		
		assertEquals(3, driver.channelAddedCount);
		assertEquals(0, driver.channelRemovedCount);
		
		appReg.listener.appRemoved(app2);
		
		assertEquals(3, driver.channelAddedCount);
		assertEquals(0, driver.channelRemovedCount);
	}
	
	
	private class Holder {
	
		private DeviceLocator deviceLocator;
		
		private ChannelLocator channelOndemand;
		private ChannelLocator channelPolled;
		private ChannelLocator channelListen;
		
		private ChannelConfiguration configurationOndemand;
		private ChannelConfiguration configurationPolled;
		private ChannelConfiguration configurationListen;

		private Holder(DeviceLocator deviceLocator, String iface, ChannelLocator ondemand, ChannelLocator polled, ChannelLocator listen) throws Exception {
			this.deviceLocator = deviceLocator;
			channelOndemand = ondemand;
			channelPolled = polled;
			channelListen = listen;
			
			configurationOndemand = channelManager.addChannel(channelOndemand, Direction.DIRECTION_INPUT, ChannelConfiguration.NO_READ_NO_LISTEN);
			configurationPolled = channelManager.addChannel(channelPolled, Direction.DIRECTION_OUTPUT, 100);
			configurationListen = channelManager.addChannel(channelListen, Direction.DIRECTION_INPUT, ChannelConfiguration.LISTEN_FOR_UPDATE);
			
			channelManager.discoverDevices(driver.getDriverId(), iface, "", new DeviceScanListener() {
				
				@Override
				public void progress(float ratio) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void finished(boolean success, Exception e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void deviceFound(DeviceLocator device) {
					// TODO Auto-generated method stub
					
				}
			});
			
			channelManager.discoverChannels(deviceLocator, new ChannelScanListener() {
				
				@Override
				public void progress(float ratio) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void finished(boolean success) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void channelFound(ChannelLocator channel) {
					// TODO Auto-generated method stub
					
				}
			});
			
			channelManager.addDeviceListener(driver.getDriverId(), new DeviceListener() {
				
				@Override
				public void deviceRemoved(DeviceLocator device) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void deviceAdded(DeviceLocator device) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}
}
