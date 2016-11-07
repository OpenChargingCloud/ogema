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
package org.ogema.impl.security;

import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Dictionary;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.ogema.accesscontrol.AccessManager;
import org.ogema.accesscontrol.AppPermissionFilter;
import org.ogema.accesscontrol.PermissionManager;
import org.ogema.accesscontrol.UserRightsProxy;
import org.ogema.accesscontrol.WebAccessPermission;
import org.ogema.applicationregistry.ApplicationRegistry;
import org.ogema.core.administration.AdminApplication;
import org.ogema.core.administration.CredentialStore;
import org.ogema.core.application.AppID;
import org.ogema.core.security.AppPermission;
import org.ogema.staticpolicy.StaticPolicies;
import org.ogema.staticpolicy.StaticUser;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;

/**
 * All mechanisms related to role based access management is included in this class. Users including their properties
 * (public) and credentials (private) are stored persistently by the UserAdmin service. Code accessing the user
 * credentials or properties needs UserAdminPermission. For more information please refer to OSGi Service Platform
 * Service Compendium Release 4.
 * 
 * There are two ways to check user based permissions. First users could access web resources of apps over the web
 * interface and initiate security sensitive accesses that are protected by the security manager in common osgi way.
 * Second a user could access to security sensitive resources (like OGEMA resources via REST interface) without any
 * application to be involved.
 * 
 * In the first way for each installed app a Role from type GROUP is created. Each user is added as a member of the
 * group of the app its resources should be accessible by this user. The check of a permission ends up in the check if
 * the user has the role with the same name as the app id string. This is the case if the user is a member of the GROUP
 * that represents the app.
 * 
 * In the second way the user get access to specific OGEMA resources over the rest interface. These rights are granted
 * by the administration interface of the ogema framework. The implementation is similar to the first way mentioned
 * above. For each resource defined by its location path a Role from type GROUP is created. The location path could be
 * an exact path to a resource or it could be wildcarded that means that the access is recursively granted. The user
 * that should get the access right to a resource is added to the GROUP as a member.
 * 
 */

/**
 * @author Zekeriya Mansuroglu
 *
 */
public class AccessManagerImpl implements AccessManager, BundleListener {

	private final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

	static final String ADMIN_NAME = "master";
	static final String GUEST_NAME = "guest";
	private static final String REST_NAME = "rest";

	static final String OGEMA_ROLE_NAME = "ogemaRole";

	static final String OGEMA_NATURAL_USER = "naturalUser";
	static final String OGEMA_MACHINE_USER = "machineUser";
	static final String OGEMA_APPLICATION = "applicationGroup";
	static final String OGEMA_RESOURCES = "resourcesGroup";

	UserAdmin usrAdmin;

	private BundleContext osgi;

	static final String ALLAPPS = "ALL APPS";
	static final String NOAPPS = "NO APPS";

	static final String ALLRESOURCES = "ALL RESOURCES";

	private ServiceTracker<UserRightsProxy, UserRightsProxy> urpTracker;

	final ConcurrentHashMap<String, UserRightsProxy> urpMap;
	final PermissionManager permMan;
	final ApplicationRegistry appReg;
	final CredentialStore cStore;

	static final String WEB_ACCESS_PERMISSION_CLASS_NAME = WebAccessPermission.class.getName();

	@SuppressWarnings("unchecked")
	// no generics in UserAdmin
	AccessManagerImpl(UserAdmin ua, BundleContext osgi, CredentialStore cs, PermissionManager pm,
			StaticPolicies stPol) {
		this.cStore = cs;
		this.usrAdmin = ua;
		this.osgi = osgi;
		this.urpMap = new ConcurrentHashMap<>();
		this.permMan = pm;
		this.appReg = permMan.getApplicationRegistry();

		Dictionary<String, Object> dict = null;
		Role role = ua.getRole(ALLAPPS);
		if (role == null) {
			role = ua.createRole(ALLAPPS, Role.GROUP);
			dict = role.getProperties();
			dict.put(OGEMA_ROLE_NAME, OGEMA_APPLICATION);
		}

		role = ua.getRole(NOAPPS);
		if (role == null) {
			role = ua.createRole(NOAPPS, Role.GROUP);
			dict = role.getProperties();
			dict.put(OGEMA_ROLE_NAME, OGEMA_APPLICATION);
		}

		role = ua.getRole(ALLRESOURCES);
		if (role == null) {
			role = ua.createRole(ALLRESOURCES, Role.GROUP);
			dict = role.getProperties();
			dict.put(OGEMA_ROLE_NAME, OGEMA_RESOURCES);
		}

		initURPTracker();
		/*
		 * Create the statically configured users and their roles
		 */
		// StaticPolicies stPol;
		// ServiceReference<?> sRef = osgi.getServiceReference(StaticPolicies.class);
		// if (sRef != null) {
		// stPol = (StaticPolicies) osgi.getService(sRef);
		List<StaticUser> users = stPol.getUsers();
		if (users != null) // if users is null non-clean start is performed, so no new users are to be created.
			for (StaticUser user : users) {
				String name = user.getName();
				boolean natural = user.isNatural();
				createUser(name, name, natural);
			}
		// }
		/*
		 * Create standard user admin
		 */
		createUser(ADMIN_NAME, ADMIN_NAME, true);

		/*
		 * Create standard user guest
		 */
		createUser(GUEST_NAME, GUEST_NAME, true);

		/*
		 * Create standard rest user
		 */
		createUser(REST_NAME, REST_NAME, false);

		if (Configuration.DEBUG) {
			try {
				Role[] roles = ua.getRoles(null);
				for (Role r : roles) {
					int type = r.getType();
					switch (type) {
					case Role.GROUP:
						logger.debug("Registered group: " + r.getName());
						break;
					case Role.USER:
						logger.debug("Registered user: " + r.getName());
						break;
					case Role.ROLE:
						logger.debug("Registered role: " + r.getName());
						break;
					default:
						throw new IllegalStateException();

					}

				}
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	void close() {
		if (urpTracker != null)
			urpTracker.close();
		urpTracker = null;
		urpMap.clear();
	}

	private void initURPTracker() {
		ServiceTrackerCustomizer<UserRightsProxy, UserRightsProxy> cust = new ServiceTrackerCustomizer<UserRightsProxy, UserRightsProxy>() {

			@Override
			public UserRightsProxy addingService(ServiceReference<UserRightsProxy> sr) {
				UserRightsProxy urp = osgi.getService(sr);
				urpMap.put(urp.getUserName(), urp);
				return urp;
			}

			@Override
			public void modifiedService(ServiceReference<UserRightsProxy> sr, UserRightsProxy t) {
			}

			@Override
			public void removedService(ServiceReference<UserRightsProxy> sr, UserRightsProxy urp) {
				urpMap.remove(urp);
			}
		};

		urpTracker = new ServiceTracker<>(osgi, UserRightsProxy.class, cust);
		urpTracker.open(true);

	}

	@Override
	public UserRightsProxy getUrp(String usr) {
		return urpMap.get(usr);
	}

	static final String rolesFile = "ogema.roles";
	static final String rolesDir = "ogema";

	private static final String USER_URP_ID_NAME = "URPid";

	/**
	 * An user is created and and added to the user management. The user could represent an App or a natural person
	 * which has to be authenticated on web interface. This property is given by the value of {@link isNatural}.
	 * 
	 * The added user doesn't possess any app or resource permissions as default. They could be granted later on the
	 * administration interface.
	 *
	 * @param user
	 *            name of the entity which is added to the system as user.
	 * @param natural
	 *            true if the user is a natural one or false if the user is a machine
	 * @return true, if a new user was created successfully or false if the user already exist or the creation failed.
	 */
	@Override
	public boolean createUser(final String user, final String pwd, final boolean natural) {

		Bundle b = AccessController.doPrivileged(new PrivilegedAction<Bundle>() {
			public Bundle run() {
				Role role = usrAdmin.getRole(user);
				Bundle result = null;
				if (role == null) {
					role = usrAdmin.createRole(user, Role.USER);
					// add to credential store
					try {
						cStore.createUser(user, pwd, null, null);
					} catch (IOException e) {
						logger.error("Credential store reported exception during user crreation. User is not crated.",
								e);
						usrAdmin.removeRole(user);
						return null;
					}

					@SuppressWarnings("unchecked")
					Dictionary<String, Object> dict = role.getProperties();
					if (natural)
						dict.put(OGEMA_ROLE_NAME, OGEMA_NATURAL_USER);
					else
						dict.put(OGEMA_ROLE_NAME, OGEMA_MACHINE_USER);
				}
				if (role != null) {
					UserRightsProxy urp = urpMap.get(user);
					if (urp == null)
						result = installURPBundle(user);
					if (result != null) {
						@SuppressWarnings("unchecked")
						Dictionary<String, Object> dict = role.getProperties();
						dict.put(USER_URP_ID_NAME, Long.toString(result.getBundleId()));
					}
					else
						logger.info("User couldn't be created due to failure of URP bundle installation.");
				}
				return result;
			}
		});
		if (b == null)
			return false;
		else
			return true;
	}

	private Bundle installURPBundle(String userName) {
		Bundle b = null;
		try {
			b = this.osgi.installBundle("urp:" + userName);
			logger.info(String.format("User rights proxy installed: %s", b.getLocation()));
			b.start();
		} catch (BundleException e) {
			logger.debug("BundleException.TYPE: %d", e.getType());
			if (b == null) {
				logger.info("Installation of user rights proxy bundle failed.", e.getCause());
			}
			else {
				logger.error("URP bundle failed to start.", e);
			}
		}
		return b;
	}

	@Override
	public void removeUser(String userName) {
		boolean b = usrAdmin.removeRole(userName);
		if (!b && Configuration.DEBUG)
			logger.debug("User couldn't be removed: " + userName);
		cStore.logout(userName);
		cStore.removeUser(userName);
	}

	@Override
	public void addPermission(String user, AppPermissionFilter props) {
		UserRightsProxy urp = urpMap.get(user);
		if (urp == null)
			throw new RuntimeException(
					String.format("User rights proxy installation for the user %s not yet completed.", user));
		Bundle b = urp.getBundle();
		AppID appid = appReg.getAppByBundle(b);

		addPerm(appid, props);
	}

	@Override
	public void addPermission(String user, List<AppPermissionFilter> props) {
		UserRightsProxy urp = urpMap.get(user);
		Bundle b = urp.getBundle();
		AppID appid = appReg.getAppByBundle(b);

		addPermList(appid, props);
	}

	private void addPermList(AppID appid, List<AppPermissionFilter> props) {
		String args[] = new String[2];
		args[1] = null;
		AppPermission ap = permMan.getPolicies(appid);

		for (AppPermissionFilter approps : props) {
			args[0] = approps.getFilterString();
			if (args[0] == null)
				args[0] = "*";
			{
				ap.addPermission(WEB_ACCESS_PERMISSION_CLASS_NAME, args, null);
			}
		}
		// To commit the made changes the following is needed.
		permMan.installPerms(ap);

	}

	private void addPerm(AppID appid, AppPermissionFilter props) {
		String args[] = new String[2];
		args[0] = props.getFilterString();
		if (args[0] == null)
			args[0] = "*";
		args[1] = null;

		AppPermission ap = permMan.getPolicies(appid);
		{
			ap.addPermission(WEB_ACCESS_PERMISSION_CLASS_NAME, args, null);
		}
		// To commit the made changes the following is needed.
		permMan.installPerms(ap);

	}

	@Override
	public void setNewPassword(String user, String oldPwd, String newPwd) {
		cStore.setGWPassword(user, oldPwd, newPwd);
	}

	@Override
	public Role getRole(String name) {
		Role r = usrAdmin.getRole(name);
		return r;
	}

	@Override
	public List<String> getAllUsers() {
		Vector<String> v = null;
		Role[] roles = null;
		try {
			roles = usrAdmin.getRoles(null);
		} catch (InvalidSyntaxException e) {
		}
		if (roles != null)
			v = new Vector<>();
		for (Role r : roles) {
			if (r.getType() == Role.USER)
				v.add(r.getName());
		}
		return v;
	}

	@Override
	public List<AppID> getAppsPermitted(String user) {
		Vector<AppID> v = new Vector<>();
		/*
		 * Get the all groups that represent installed apps
		 */
		List<AdminApplication> laa = appReg.getAllApps();

		for (AdminApplication aa : laa) {
			AppID id = aa.getID();
			if (isPermitted(user, id))
				v.add(id);
		}
		return v;
	}

	@Override
	public boolean isAppPermitted(User user, AppID app) {
		String username = user.getName();
		return isPermitted(username, app);
	}

	@Override
	public boolean isAppPermitted(String user, AppID app) {
		return isPermitted(user, app);
	}

	private boolean isPermitted(String username, AppID app) {
		if (!permMan.isSecure())
			return true;
		Bundle b = app.getBundle();
		WebAccessPermission wap = new WebAccessPermission(b.getSymbolicName(), username, null, b.getVersion());
		UserRightsProxy urp = urpMap.get(username);
		if (urp == null) {
			logger.error("Unknown user " + username);
			return false;
		}
		AccessControlContext acc = permMan.getBundleAccessControlContext(urp.getClass());
		return permMan.handleSecurity(wap, acc);
	}

	@Override
	public boolean authenticate(String usrName, final String pwd, boolean isnatural) {
		// not compatible with user management in CredentialStore
		// if (!permMan.isSecure())
		// return true;
		boolean userProp = isNatural(usrName);
		if (permMan.isSecure() && (isnatural && !userProp) || (!isnatural && userProp))
			return false;
		return cStore.login(usrName, pwd);
	}

	@Override
	public void removePermission(String user, AppPermissionFilter props) {
		Bundle urpbundle = urpMap.get(user).getBundle();
		AppID urpapp = appReg.getAppByBundle(urpbundle);

		permMan.removePermission(urpapp.getBundle(), WEB_ACCESS_PERMISSION_CLASS_NAME, props.getFilterString(), null);
	}

	@Override
	public void setCredential(String user, String credential, String value) {
		User usr;
		Role role = usrAdmin.getRole(user);
		if (role == null) {
			throw new IllegalArgumentException();
		}
		usr = (User) role;
		// Set users credential
		@SuppressWarnings("unchecked")
		Dictionary<String, Object> dict = usr.getCredentials();
		dict.put(credential, value);
		if (Configuration.DEBUG)
			if (usr.hasCredential(user, credential))
				logger.debug("User credential is set correctly");
	}

	@Override
	public void registerApp(AppID id) {
	}

	@Override
	public void unregisterApp(AppID appid) {
	}

	@Override
	public AppPermission getPolicies(String user) {
		AppPermission ap;
		UserRightsProxy urp = urpMap.get(user);
		AppID appid = appReg.getAppByBundle(urp.getBundle());
		ap = permMan.getPolicies(appid);
		return ap;
	}

	@Override
	public void setProperty(String user, String propName, String propValue) {
		Role role = usrAdmin.getRole(user);
		if (role == null)
			return;
		@SuppressWarnings("unchecked")
		Dictionary<String, Object> dict = role.getProperties();
		dict.put(propName, propValue);
	}

	@Override
	public String getProperty(String user, String propName) {
		Role role = usrAdmin.getRole(user);
		if (role == null)
			return null;
		@SuppressWarnings("unchecked")
		Dictionary<String, Object> dict = role.getProperties();
		return (String) dict.get(propName);
	}

	@Override
	public boolean isNatural(String user) {
		Role r = getRole(user);
		if (r == null)
			throw new RuntimeException(String.format("User %s doesn't exist!", user));
		String userType = getProperty(user, OGEMA_ROLE_NAME);
		return userType.equals(OGEMA_NATURAL_USER);
	}

	/*
	 * Surrogate implementation for unregisterApp. The UserAdmin role is only removed, if the bundle really uninstalled.
	 */
	@Override
	public void bundleChanged(BundleEvent event) {
		if (event.getType() != BundleEvent.UNINSTALLED)
			return;
		Bundle b = event.getBundle();
		AppID appid = appReg.getAppByBundle(b);
		if (appid != null)
			usrAdmin.removeRole(appid.getIDString());
	}

	@Override
	public boolean isAllAppsPermitted(String user) {
		User usr = (User) usrAdmin.getRole(user);
		if (usr == null)
			return false;
		Authorization auth = usrAdmin.getAuthorization(usr);
		return auth.hasRole(ALLAPPS);
	}

	@Override
	public boolean isNoAppPermitted(String user) {
		User usr = (User) usrAdmin.getRole(user);
		if (usr == null)
			return false;
		Authorization auth = usrAdmin.getAuthorization(usr);
		return auth.hasRole(NOAPPS);
	}

	@Override
	public Group createGroup(String name) {
		Role role = usrAdmin.getRole(name);
		if (role == null) {
			role = usrAdmin.createRole(name, Role.GROUP);
		}
		return (Group) role;
	}

	@Override
	public void logout(String usrName) {
		cStore.logout(usrName);
	}
}
