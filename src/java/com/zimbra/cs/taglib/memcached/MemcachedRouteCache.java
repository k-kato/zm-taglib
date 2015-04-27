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

package com.zimbra.cs.taglib.memcached;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.memcached.MemcachedKey;
import com.zimbra.common.util.memcached.MemcachedMap;
import com.zimbra.common.util.memcached.MemcachedSerializer;
import com.zimbra.common.util.memcached.StringBasedMemcachedKey;
import com.zimbra.common.util.memcached.ZimbraMemcachedClient;

public class MemcachedRouteCache implements RouteCache {
    protected static MemcachedRouteCache sTheInstance = new MemcachedRouteCache();
    protected MemcachedMap<MemcachedKey, String> mMemcachedLookup;

    public static MemcachedRouteCache getInstance() { return sTheInstance; }


    MemcachedRouteCache() {
        ZimbraMemcachedClient memcachedClient = MemcachedConnector.getClient();
        RouteSerializer serializer = new RouteSerializer();
        mMemcachedLookup = new MemcachedMap<>(memcachedClient, serializer, false);
    }

    protected MemcachedKey key(String accountId, String scheme) {
        return new StringBasedMemcachedKey("route:", "proto=" + scheme + ";id=" + accountId);
    }

    @Override
    public String get(String accountId, String scheme) throws ServiceException {
        return mMemcachedLookup.get(key(scheme, accountId));
    }

    @Override
    public void put(String accountId, String route, String scheme) throws ServiceException {
        mMemcachedLookup.put(key(scheme, accountId), route);
    }


    private static class RouteSerializer implements MemcachedSerializer<String> {
        @Override
        public Object serialize(String value) {
            return value;
        }

        @Override
        public String deserialize(Object obj) throws ServiceException {
            return (String) obj;
        }
    }
}
