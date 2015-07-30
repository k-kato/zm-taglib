/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2014 Zimbra, Inc.
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.SkipPageException;

import org.eclipse.jetty.io.RuntimeIOException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.zclient.ZClientException;
import com.zimbra.cs.taglib.bean.ZExceptionBean;
import com.zimbra.cs.taglib.bean.ZTagLibException;

public class GetExceptionTag extends ZimbraSimpleTag {

    private String mVar;
    private Exception mException;

    public void setVar(String var) { this.mVar = var; }

    public void setException(Exception e) { this.mException = e; }

    @Override
    public void doTag() throws JspException, IOException {
        ZExceptionBean eb = new ZExceptionBean(mException);
        Exception e = eb.getException();
        if (e != null) {
            if (
                    (!(e instanceof ServiceException)) ||
                            ((e instanceof ZTagLibException) && (!(e.getCause() instanceof SkipPageException || e.getCause() instanceof IllegalStateException || e.getCause() instanceof RuntimeIOException))) || (e instanceof ZClientException))
                ZimbraLog.webclient.warn("local exception", e);
        }
        getJspContext().setAttribute(mVar, eb,  PageContext.PAGE_SCOPE);
        getJspContext().setAttribute(mVar, eb,  PageContext.SESSION_SCOPE);
    }
}
