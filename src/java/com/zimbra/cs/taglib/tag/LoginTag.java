/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.zclient.ZGetInfoResult;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class LoginTag extends ZimbraSimpleTag {
    
    private String mUsername;
    private String mPassword;
    private String mNewPassword;
    private String mAuthToken;
    private boolean mAuthTokenInUrl;
    private boolean mRememberMe;
    private String mUrl = null;
    private String mPath = null;
    private String mVarRedirectUrl = null;
    private String mVarAuthResult = null;

    public void setVarRedirectUrl(String varRedirectUrl) { this.mVarRedirectUrl = varRedirectUrl; }

    public void setVarAuthResult(String varAuthResult) { this.mVarAuthResult = varAuthResult; }

    public void setUsername(String username) { this.mUsername = username; }

    public void setPassword(String password) { this.mPassword = password; }

    public void setNewpassword(String password) { this.mNewPassword = password; }
    
    public void setRememberme(boolean rememberMe) { this.mRememberMe = rememberMe; }

    public void setAuthtoken(String authToken) { this.mAuthToken = authToken; }

    public void setAuthtokenInUrl(boolean authTokenInUrl) { this.mAuthTokenInUrl = authTokenInUrl; }
    
    public void setUrl(String url) { this.mUrl = url; }

    private String getVirtualHost(HttpServletRequest request) {
        return request.getServerName();
        /*
        String virtualHost = request.getHeader("Host");
        if (virtualHost != null) {
            int i = virtualHost.indexOf(':');
            if (i != -1) virtualHost = virtualHost.substring(0, i);
        }
        return virtualHost;
        */
    }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            PageContext pageContext = (PageContext) jctxt;
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

            String serverName = request.getServerName();

            ZMailbox.Options options = new ZMailbox.Options();

            if (mAuthToken != null) {
                options.setAuthToken(mAuthToken);
                options.setAuthAuthToken(true);
            } else {

                if (mUsername != null && mUsername.contains("@zimbra.com")) {
                    mUrl = "https://dogfood.zimbra.com/service/soap";
                } else if (mUsername != null && mUsername.contains("@roadshow.zimbra.com")) {
                    mUrl = "http://roadshow.zimbra.com/service/soap";
                }
                options.setAccount(mUsername);
                options.setPassword(mPassword);
                options.setVirtualHost(getVirtualHost(request));
                if (mNewPassword != null && mNewPassword.length() > 0)
                    options.setNewPassword(mNewPassword);
            }
            options.setUri(mUrl == null ? ZJspSession.getSoapURL(pageContext): mUrl);
            options.setClientIp(request.getRemoteAddr());

            ZMailbox mbox = ZMailbox.getMailbox(options);
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();

            String refer = mbox.getAuthResult().getRefer();
            boolean needRefer = (refer != null && !refer.equalsIgnoreCase(serverName));

            if ((mAuthToken == null || mAuthTokenInUrl) && !needRefer) {
                Cookie authTokenCookie = new Cookie(ZJspSession.COOKIE_NAME, mbox.getAuthToken());
                if (mRememberMe) {
                    ZGetInfoResult info = mbox.getAccountInfo(false);
                    long timeLeft = info.getExpiration() - System.currentTimeMillis();
                    if (timeLeft > 0) authTokenCookie.setMaxAge((int) (timeLeft/1000));
                } else {
                    authTokenCookie.setMaxAge(-1);
                }
                authTokenCookie.setPath("/");
                /* this was causing a redirect loop when in MIXED, starting a HTTPS, then later going to HTTP
                authTokenCookie.setSecure(ZJspSession.secureAuthTokenCookie(request));
                */
                response.addCookie(authTokenCookie);

            }

            if (!needRefer)
                ZJspSession.setSession((PageContext)jctxt, mbox);

            if (mVarRedirectUrl != null)
                jctxt.setAttribute(mVarRedirectUrl,
                        ZJspSession.getPostLoginRedirectUrl(pageContext, mPath, mbox.getAuthResult(), mRememberMe, needRefer),  PageContext.REQUEST_SCOPE);

            if (mVarAuthResult != null)
                jctxt.setAttribute(mVarAuthResult, mbox.getAuthResult(), PageContext.REQUEST_SCOPE);

        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
