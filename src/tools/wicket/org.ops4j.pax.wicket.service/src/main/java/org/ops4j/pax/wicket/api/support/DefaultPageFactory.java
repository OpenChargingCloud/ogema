
/**
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author nmw
 * @version $Id: $Id
 */
package org.ops4j.pax.wicket.api.support;

import java.lang.reflect.UndeclaredThrowableException;

import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.osgi.framework.BundleContext;
public class DefaultPageFactory<T extends Page> extends AbstractPageFactory<T> {

    private Class<T> pageClass;

    /**
     * <p>Constructor for DefaultPageFactory.</p>
     *
     * @param bundleContext a {@link org.osgi.framework.BundleContext} object.
     * @param pageId a {@link java.lang.String} object.
     * @param applicationName a {@link java.lang.String} object.
     * @param pageName a {@link java.lang.String} object.
     * @param pageClass a {@link java.lang.Class} object.
     * @throws java.lang.IllegalArgumentException if any.
     */
    public DefaultPageFactory(
            BundleContext bundleContext,
            String pageId,
            String applicationName,
            String pageName,
            Class<T> pageClass)
        throws IllegalArgumentException {
        this(bundleContext, pageId, applicationName, pageName, pageClass, null);
    }

    /**
     * <p>Constructor for DefaultPageFactory.</p>
     *
     * @param bundleContext a {@link org.osgi.framework.BundleContext} object.
     * @param pageId a {@link java.lang.String} object.
     * @param applicationName a {@link java.lang.String} object.
     * @param pageName a {@link java.lang.String} object.
     * @param pageClass a {@link java.lang.Class} object.
     * @param niceUrlPath a {@link java.lang.String} object.
     * @throws java.lang.IllegalArgumentException if any.
     */
    public DefaultPageFactory(
            BundleContext bundleContext,
            String pageId,
            String applicationName,
            String pageName,
            Class<T> pageClass,
            String niceUrlPath)
        throws IllegalArgumentException {
        super(bundleContext, pageId, applicationName, pageName);
        this.pageClass = pageClass;
    }

    /** {@inheritDoc} */
    @Override
    public Class<T> getPageClass() {
        return pageClass;
    }

    /** {@inheritDoc} */
    @Override
    public T createPage(PageParameters params) {
        try {
            return pageClass.newInstance();
        } catch (InstantiationException e) {
            throw new UndeclaredThrowableException(e);
        } catch (IllegalAccessException e) {
            throw new UndeclaredThrowableException(e);
        }
    }
}
