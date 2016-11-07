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
package org.ogema.pattern.test;

import org.ogema.pattern.test.pattern.InheritedOutsideRoomRad;
import org.ogema.pattern.test.pattern.OutsideRoomRad;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.PatternListener;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@ExamReactorStrategy(PerClass.class)
public class InheritanceTest extends OsgiTestBase {

	@ProbeBuilder
	public TestProbeBuilder build(TestProbeBuilder builder) {
		builder.setHeader("Bundle-SymbolicName", "test-probe");
		return builder;
	}

	OutsideRoomRad m_room = null;

	@Before
	public void initInactiveRad() {
		final String RADNAME = "testInheritedRadRoom";
		final Resource existingModel = resAcc.getResource(RADNAME);
		if (existingModel != null) {
			m_room = new InheritedOutsideRoomRad(existingModel);
			m_room.type.setValue(0);
			m_room.model.deactivate(true);
			return;
		}

		// Test the creation of a RAD with the advanced access.
		m_room = advAcc.createResource(RADNAME, InheritedOutsideRoomRad.class);
		assertNotNull(m_room);
		m_room.model.type().setValue(0);
	}

	class CountingListener implements PatternListener<InheritedOutsideRoomRad> {

		public volatile CountDownLatch foundLatch;
		public volatile CountDownLatch lostLatch;

		public volatile InheritedOutsideRoomRad lastAvailable = null;

		public CountingListener(int foundCounts, int lossCounts) {
			foundLatch = new CountDownLatch(foundCounts);
			lostLatch = new CountDownLatch(lossCounts);
		}

		@Override
		public void patternAvailable(InheritedOutsideRoomRad fulfilledDemand) {
			lastAvailable = fulfilledDemand;
			foundLatch.countDown();
            appMan.getLogger().info("pattern available: {}", fulfilledDemand.model.getPath());
		}

		@Override
		public void patternUnavailable(InheritedOutsideRoomRad object2beLost) {
			lostLatch.countDown();
            appMan.getLogger().info("pattern unavailable: {}", object2beLost.model.getPath());
		}
	}

	@Test
	//        @Ignore
	public void findLoseRefindForChildRad() throws InterruptedException {

		final CountingListener listener = new CountingListener(1, 1);
		advAcc.addPatternDemand(InheritedOutsideRoomRad.class, listener, null);
		m_room.model.activate(true);
		assertTrue("pattern should become available", listener.foundLatch.await(10, TimeUnit.SECONDS));
		assertNotNull(listener.lastAvailable);
		assertTrue(((Resource) m_room.model).equalsLocation((Resource) listener.lastAvailable.model));

		final InheritedOutsideRoomRad room = listener.lastAvailable;

		// @Equals annotation was removed
		room.type.setValue(46);
        appMan.getLogger().info("room type changed to incompatible value");
        assertTrue("pattern should become invalid", listener.lostLatch.await(10, TimeUnit.SECONDS));

		listener.foundLatch = new CountDownLatch(1);
		room.type.setValue(0);
        assertTrue("pattern should become available", listener.foundLatch.await(10, TimeUnit.SECONDS));
	}
}
