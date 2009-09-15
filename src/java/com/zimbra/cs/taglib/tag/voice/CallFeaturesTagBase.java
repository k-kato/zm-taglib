/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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

package com.zimbra.cs.taglib.tag.voice;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;

import java.util.List;
import java.util.ArrayList;

abstract public class CallFeaturesTagBase extends ZimbraSimpleTag {
	protected String mVar;
	protected String mPhone;
	protected boolean mEmailNotificationActive;
	protected String mEmailNotificationAddress;
	
	protected String mAddEmailNotificationaddress;
	protected String mRemoveEmailNotificationaddress;
	
	protected boolean mCallForwardingActive;
	protected String mCallForwardingForwardTo;
	protected boolean mSelectiveCallForwardingActive;
	protected String mSelectiveCallForwardingForwardTo;
	protected List<String> mSelectiveCallForwardingForwardFrom;
	
	protected boolean mAnonymousCallRejectionActive;
	protected boolean mSelectiveCallRejectionActive;
	protected List<String> mSelectiveCallRejectionRejectFrom;
	
	protected long mNumberPerPage;
	protected int mNumberOfRings;
	
	protected boolean mPlayDateAndTimeInMsgEnv;
	protected boolean mPlayCallerNameInMsgEnv;
	protected boolean mAutoPlayNewMsgs;
	protected boolean mSkipPinEntry;
	protected String mPromptLevel;
	protected String mAnsweringLocale;
	protected String mUserLocale;	

	public void setVar(String var) { mVar = var; }
	public void setPhone(String phone) { mPhone = phone; }
	public void setEmailnotificationactive(String active) { mEmailNotificationActive = booleanValue(active); }
	public void setEmailnotificationaddress(String address) { mEmailNotificationAddress = address.trim(); }
	
	public void setAddEmailnotificationaddress(String address) { mAddEmailNotificationaddress = address.trim(); }
	public void setRemoveEmailnotificationaddress(String address) { mRemoveEmailNotificationaddress = address.trim(); }
	
	public void setCallforwardingactive(String active) { mCallForwardingActive = booleanValue(active); }
	public void setCallforwardingforwardto(String number) { mCallForwardingForwardTo = number.trim(); }
	public void setSelectivecallforwardingactive(String active) { mSelectiveCallForwardingActive = booleanValue(active); }
	public void setSelectivecallforwardingforwardto(String number) { mSelectiveCallForwardingForwardTo = number.trim(); }
	public void setSelectivecallforwardingforwardfrom(String[] numbers) {
		mSelectiveCallForwardingForwardFrom = new ArrayList<String>(numbers.length);
		for (String number : numbers) {
			mSelectiveCallForwardingForwardFrom.add(number.trim());
		}
	}
	public void setAnonymouscallrejectionactive(String active) { mAnonymousCallRejectionActive = booleanValue(active); }
	public void setSelectivecallrejectionactive(String active) { mSelectiveCallRejectionActive = booleanValue(active); }
	public void setSelectivecallrejectionrejectfrom(String[] numbers) {
		mSelectiveCallRejectionRejectFrom = new ArrayList<String>(numbers.length);
		for (String number : numbers) {
			mSelectiveCallRejectionRejectFrom.add(number.trim());
		}
	}
	
	public void setNumberPerPage(String number) { mNumberPerPage = Long.parseLong(number); }
	public void setNumberOfRings(String rings) { mNumberOfRings = Integer.parseInt(rings); }
	
	public void setPromptLevel(String level) { mPromptLevel = level; }
	public void setAnsweringLocale(String locale) { mAnsweringLocale = locale; }
	public void setUserLocale(String locale) { mUserLocale = locale; }
	public void setPlayDateAndTimeInMsgEnv(String play) { mPlayDateAndTimeInMsgEnv = booleanValue(play); }
	public void setPlayCallerNameInMsgEnv(String play) { mPlayCallerNameInMsgEnv = booleanValue(play); }
	public void setAutoPlayNewMsgs(String play) { mAutoPlayNewMsgs = booleanValue(play); }
	public void setSkipPinEntry(String skip) { mSkipPinEntry = booleanValue(skip); }
	public void setRequirePinEntry(String require) { mSkipPinEntry = !booleanValue(require); }

	private boolean booleanValue(String value) {
		return "TRUE".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value);
	}
}
