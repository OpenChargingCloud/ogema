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
package org.ogema.webresourcemanager.impl.internal.appservlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tgries
 */
public class AppsJsonGet implements Serializable {

	private static final long serialVersionUID = 2201446440921362926L;

	private String name;
	private long id;
	private Map<String, String> metainfo;
	private boolean hasWebResources;
	private List<String> webResourcePaths = new ArrayList<>();

	public AppsJsonGet() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Map<String, String> getMetainfo() {
		return metainfo;
	}

	public void setMetainfo(Map<String, String> metainfo) {
		this.metainfo = metainfo;
	}

	public List<String> getWebResourcePaths() {
		return webResourcePaths;
	}

	public void setWebResourcePaths(List<String> webResourcePaths) {
		this.webResourcePaths = webResourcePaths;
	}

	public boolean isHasWebResources() {
		return hasWebResources;
	}

	public void setHasWebResources(boolean hasWebResources) {
		this.hasWebResources = hasWebResources;
	}

}
