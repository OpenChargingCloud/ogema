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
package org.ogema.accesscontrol;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.ogema.core.application.AppID;
import org.osgi.service.useradmin.Authorization;
import org.slf4j.Logger;

public class SessionAuth implements HttpSessionBindingListener {

	public static final String AUTH_ATTRIBUTE_NAME = "ogemaAuth";
	public static final String USER_CREDENTIAL = "usrCred";

	Authorization auth;
	HttpSession ses;

	ConcurrentHashMap<String, String> otpList;

	public ConcurrentHashMap<String, String> getOtpList() {
		return otpList;
	}

	private final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());
	// User usr;
	private AccessManager accMngr;

	//public User getUsr() {
	//	return usr;
	//}

	static Random rnd;

	public SessionAuth(Authorization auth, AccessManager accessManager, HttpSession ses) {
		this.auth = auth;
		this.ses = ses;
		// this.usr = user;
		this.otpList = new ConcurrentHashMap<>();
		this.accMngr = accessManager;
	}

	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
//		String id = ses.getId();
//		logger.info("Session binding event: bound " + id);
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		String id = ses.getId();
		logger.info("Session binding event: unbound " + id);
		accMngr.logout(auth.getName());
	}

	public String registerAppOtp(AppID app) {
		/*
		 * Register each app and one time password. This is to be verified in RestAccess servlet.
		 */

		String otp = otpList.get(app);

		if (otp == null) {
			otp = Long.toHexString(rnd.nextLong());
			otpList.put(app.getIDString(), otp);
		}
		return otp;
	}

	static {
		rnd = new Random(System.currentTimeMillis());
	}

	public String getName() {
		return auth.getName();
	}
}
