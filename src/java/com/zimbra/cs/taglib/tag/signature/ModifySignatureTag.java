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
package com.zimbra.cs.taglib.tag.signature;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.client.ZSignature;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class ModifySignatureTag extends ZimbraSimpleTag {

    private String mId;
    private String mName;
    private String mValue;
    private String mType = "text/plain";
    
    public void setId(String id) { mId = id; }
    public void setName(String name) { mName = name; }
    public void setValue(String value) { mValue = value; }
    public void setType(String type) { mType = type; }
    
    public void doTag() throws JspException, IOException {
        try {
            getMailbox().modifySignature(new ZSignature(mId, mName, mValue, mType));
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
