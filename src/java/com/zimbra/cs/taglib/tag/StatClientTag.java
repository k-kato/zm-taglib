/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.taglib.tag;

import com.yahoo.calendar.rocketstats.StatClient;
import javax.servlet.jsp.JspException;
import java.io.IOException;

public class StatClientTag extends ZimbraSimpleTag {

    private String mStat;
    private int mCount;

    public void setStat(String stat) { this.mStat = stat; }
    public void setCount(int count) { this.mCount = count; }
    
    public void doTag() throws JspException, IOException {
    	if (mCount == 1) {
        	StatClient.getInstance().inc(mStat);
    	}
    	else if (mCount > 1) {
    		StatClient.getInstance().inc(mStat,mCount);
    	}
    	else {
    		// shouldn't really be invoking this if you don't actually want to increment (count = 0)
    	}
    }
}

