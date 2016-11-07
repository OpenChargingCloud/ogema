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
package org.ogema.core.rads.creation;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.Resource;
import org.ogema.core.rads.tools.ContainerTool;
import org.ogema.core.rads.tools.ResourceFieldInfo;
import org.ogema.core.rads.tools.RadFactory;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.ResourceAlreadyExistsException;
import org.ogema.core.resourcemanager.ResourceManagement;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePattern.CreateMode;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.slf4j.LoggerFactory;

/**
 * Creates an object according to a RAD and returns an instance of the completed
 * RAD.
 *
 * @author Timo Fischer, Fraunhofer IWES
 * @param <T> type of demanded resource
 * @param <P> type of pattern
 */
public class RadCreator<T extends Resource, P extends ResourcePattern<T>> {

	final ApplicationManager m_appMan;
	final OgemaLogger m_logger;
	final ResourceManagement m_resMan;
	final RadFactory<T, P> m_factory;
	private final Object container;
	private final Class<P> type;
	P m_result;

	public RadCreator(ApplicationManager appMan, Class<P> type, PatternFactory<P> factory, Object container) {
		m_appMan = appMan;
		m_logger = appMan.getLogger();
		m_resMan = appMan.getResourceManagement();
		m_factory = new RadFactory<>(type, AccessPriority.PRIO_LOWEST, factory);
		this.container = container;
		this.type = type;
	}

	/**
	 * Creates the pattern by first creating a top-level resource with a given name which serves as the root resource and then subsequently adding the other fields in the pattern.
	 */
	public void create(String name) {
		final Class<T> model = m_factory.getDemandedModel();
		T seed;
		try {
			try {
				seed = m_resMan.createResource(name, model);
			} catch (ResourceAlreadyExistsException e) {
				seed = m_appMan.getResourceAccess().getResource(name);
			}
		} catch (Exception e) {
			m_logger.error("Error creating ResourceAccessDeclaration: Could not create demanded model with name "
					+ name + " and type " + model.getCanonicalName() + "\n\t Reason: " + e.getMessage());
			return;
		}
		createSubresources(seed);
		setContainer();
	}

	public void addDecorator(Resource parent, String name) {
		final Class<T> model = m_factory.getDemandedModel();
		T seed;
		try {
			try {
				seed = parent.addDecorator(name, model);
			} catch (ResourceAlreadyExistsException e) {
				seed = parent.getSubResource(name);
			}
		} catch (Exception e) {
			m_logger.error("Error creating ResourceAccessDeclaration: Could not create demanded model with name "
					+ name + " and type " + model.getCanonicalName() + "\n\t Reason: " + e.getMessage());
			return;
		}
		createSubresources(seed);
		setContainer();
	}

	/**
	 * Creates the pattern with a given resource object being the root resource.
	 */
	public void create(T seed) {
		if (!seed.exists())
			seed.create();
		createSubresources(seed);
		setContainer();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setContainer() {
		if (container != null && ContextSensitivePattern.class.isAssignableFrom(type) && m_result != null) {
			ContextSensitivePattern pattern = (ContextSensitivePattern) m_result;
//			pattern.setContainer(container);
			try {
				ContainerTool.setContainer(pattern, container);
			} catch (NoSuchFieldException | IllegalAccessException | RuntimeException e) {
				LoggerFactory.getLogger(getClass()).error("Internal error: could not set pattern container: " + e);
			}
			pattern.init();
		}
	}

	private void createSubresources(T seed) {
		m_result = m_factory.create(seed);
		assert m_result != null;
		for (ResourceFieldInfo info : m_factory.getResourceFieldInfos()) {
			if (info.getCreateMode() == CreateMode.OPTIONAL) {
				m_logger.debug("RAD field " + info.getField().getName() + " is optional. Will not be created.");
				continue;
			}

			final Resource resource = RadFactory.getResource(info.getField(), m_result);
			if (resource == null) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Field with name " + info.getField().getName()
							+ " was not initialized by user: Ignoring it.");
				continue; // do not care about fields that were not initialized by user.
			}
			if (resource.exists()) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Resource at " + resource.getPath() + " already exists. Skipping it.");
				continue;
			}

			final boolean created;
			try {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Creating resource at " + resource.getPath());
				created = resource.create().exists();
			} catch (Exception e) {
				throw new RuntimeException("Could not create resource " + resource + " at location "
						+ resource.getLocation(), e);
			}
			if (!created) {
				throw new RuntimeException("Error during creation of a RAD-object: Could not create subresource at "
						+ resource.getLocation());
			}
		}
	}

	public P getRad() {
		return m_result;
	}
}
