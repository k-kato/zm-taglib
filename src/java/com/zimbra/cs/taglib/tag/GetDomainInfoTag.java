/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010, 2011, 2013, 2014 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.Config;

import com.zimbra.common.account.Key;
import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.net.SocketFactories;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.DateUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.httpclient.URLUtil;

public class GetDomainInfoTag extends ZimbraSimpleTag {
    private static final String CONFIG_ZIMBRA_DOMAININFO_TTL = "zimbra.domaininfo.ttl";

    static {
        SocketFactories.registerProtocols();
    }

    private String mVar;
    private Key.DomainBy mBy;
    private String mValue;
    private String mCsrfToken;
    private ZAuthToken mAuthToken;

    private static final Map<String, CachedDomain> mCache = new HashMap<String, CachedDomain>();

    public void setVar(String var) { this.mVar = var; }
    public void setBy(String by) throws ServiceException { this.mBy = Key.DomainBy.fromString(by); }
    public void setValue(String value) { this.mValue = value; }
    public void setAuthtoken(ZAuthToken authToken) { this.mAuthToken = authToken; }
    public void setCsrftoken(String csrfToken) { this.mCsrfToken = csrfToken; }

    private static final String DEFAULT_TTL_STR = "60m";
    private static final long DEFAULT_TTL = 60*60*1000;
    private static long sCacheTtl = -1;

    @Override
    public void doTag() throws JspException, IOException {
        JspContext ctxt = getJspContext();
        if (sCacheTtl == -1) {
            String ttl = (String) Config.find((PageContext) ctxt, CONFIG_ZIMBRA_DOMAININFO_TTL);
            sCacheTtl = DateUtil.getTimeInterval(ttl != null ? ttl : DEFAULT_TTL_STR, DEFAULT_TTL);
        }

        ctxt.setAttribute(mVar, checkCache(),  PageContext.REQUEST_SCOPE);
    }

    private String getCacheKey() { return mBy +"/" + mValue; }

    private Domain checkCache() {
        if (mAuthToken == null) {
            CachedDomain cd = mCache.get(getCacheKey());
            if (cd != null) {
                if (cd.expireTime > System.currentTimeMillis())
                    return cd.domain;
            }
        }
        Domain d = getInfo();
        if (mAuthToken == null) {
            synchronized(mCache) {
                mCache.put(getCacheKey(), new CachedDomain(d));
            }
        }
        return d;
    }

    private Domain getInfo() {
        SoapProvisioning sp = new SoapProvisioning();
        try {
            sp.soapSetURI(URLUtil.getAdminURL());
            ZimbraLog.misc.debug("Using admin service URL %s", sp.soapGetURI());
            sp.setAuthToken(mAuthToken);
            sp.setCsrfToken(mCsrfToken);
        } catch (ServiceException e) {
            ZimbraLog.misc.error("Error while locating admin service", e);
            return null;
        }
        try {
            return sp.getDomainInfo(mBy, mValue);
        } catch (ServiceException e) {
            ZimbraLog.misc.error("Error requesting domain info from " + sp.soapGetURI(), e);
            return null;
        }
    }

    static class CachedDomain {
        public Domain domain;
        public long expireTime;

        public CachedDomain(Domain d) { domain = d; expireTime = System.currentTimeMillis() + sCacheTtl; }
    }
}
