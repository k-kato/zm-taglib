/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013, 2014 Zimbra, Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.ngxlookup;

import com.zimbra.common.consul.ConsulClient;
import com.zimbra.common.consul.ConsulServiceLocator;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.servicelocator.ServiceLocator;
import com.zimbra.common.util.ngxlookup.ZimbraNginxLookUpClient;

public class NginxRouteLookUpConnector {
    private static ServiceLocator sServiceLocator = new ConsulServiceLocator(new ConsulClient());
    private static ZimbraNginxLookUpClient sTheClient = new ZimbraNginxLookUpClient(sServiceLocator);

    /**
     * Returns the one and only Nginx Lookup client object.
     * Nginx LookUp Handler Client makes a new connection to a random upstream handler.
     * Handler Client doesn't maintain persistent connections unlike Memcached Client
     */
    public static ZimbraNginxLookUpClient getClient() {
        return sTheClient;
    }

    public static void startup() throws ServiceException {
    }

    public static void shutdown() throws ServiceException {
        sTheClient = null;
    }
}
