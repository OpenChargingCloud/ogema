
package org.ogema.driver.acudc243;

import org.ogema.core.model.Resource;
import org.ogema.core.model.units.ElectricCurrentResource;
import org.ogema.core.model.units.EnergyResource;
import org.ogema.core.model.units.PowerResource;
import org.ogema.core.model.units.VoltageResource;
import org.ogema.core.resourcemanager.AccessMode;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.connections.ElectricityConnection;

/**
 * Creation pattern for a electricity connection supported by this driver.
 */
public class MeterPattern extends ResourcePattern<ElectricityConnection> {

	/**
	 * Current value measured by the meter.
	 */
	public final ElectricCurrentResource current = model.currentSensor().reading();

	/**
	 * Voltage value measured by the meter.
	 */
	public final VoltageResource voltage = model.voltageSensor().reading();

	/**
	 * Energy value measured by the meter.
	 */
	public final EnergyResource energy = model.energySensor().reading();

	/**
	 * Energy value measured by the meter.
	 */
	public final PowerResource power = model.powerSensor().reading();

	/**
	 * Default constructor required by the framework. Do not change.
	 */
	public MeterPattern(Resource res) {
		super(res);
	}

	/**
	 * Initialize the field with defaults and request exclusive write access to all fields read from device.
	 */
	public void init() {
		current.setValue(0.f);
		voltage.setValue(0);
		energy.setValue(0);

		current.requestAccessMode(AccessMode.EXCLUSIVE, AccessPriority.PRIO_HIGHEST);
		voltage.requestAccessMode(AccessMode.EXCLUSIVE, AccessPriority.PRIO_HIGHEST);
		energy.requestAccessMode(AccessMode.EXCLUSIVE, AccessPriority.PRIO_HIGHEST);
	}
}
