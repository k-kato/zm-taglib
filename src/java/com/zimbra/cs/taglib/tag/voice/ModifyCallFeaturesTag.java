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

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZCallFeaturesBean;
import com.zimbra.cs.taglib.bean.ZCallFeatureBean;
import com.zimbra.cs.taglib.bean.ZCallForwardingBean;
import com.zimbra.cs.taglib.bean.ZSelectiveCallForwardingBean;
import com.zimbra.cs.taglib.bean.ZSelectiveCallRejectionBean;
import com.zimbra.cs.taglib.bean.ZVoiceMailPrefsBean;
import com.zimbra.cs.zclient.ZCallFeatures;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZPhoneAccount;
import com.zimbra.cs.account.Provisioning;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class ModifyCallFeaturesTag extends CallFeaturesTagBase {

    public void doTag() throws JspException, IOException {
        try {
            ZMailbox mailbox = getMailbox();
            ZPhoneAccount account = mailbox.getPhoneAccount(mPhone);
            ZCallFeaturesBean oldFeatures = new ZCallFeaturesBean(account.getCallFeatures(), false);
            ZCallFeaturesBean newFeatures = new ZCallFeaturesBean(new ZCallFeatures(mailbox, account.getPhone()), true);
	    
            ZVoiceMailPrefsBean voiceMailPrefs = oldFeatures.getVoiceMailPrefs();
	    ZCallForwardingBean callForwardingNoAnswer = oldFeatures.getCallForwardingNoAnswer();
	    
            if ((!mEmailNotificationActive && (voiceMailPrefs.getEmailNotificationAddress().length() > 0)) || !voiceMailPrefs.getEmailNotificationAddress().equalsIgnoreCase(mEmailNotificationAddress)) {
                String address = mEmailNotificationActive ? mEmailNotificationAddress : "";
                newFeatures.getVoiceMailPrefs().setEmailNotificationAddress(address);
            }
	    
	    ZCallForwardingBean callForwarding = oldFeatures.getCallForwardingAll();
            if (callForwarding.getIsActive() != mCallForwardingActive || !callForwarding.getForwardTo().equals(mCallForwardingForwardTo)) {
                ZCallForwardingBean newCallForwarding = newFeatures.getCallForwardingAll();
                newCallForwarding.setIsActive(mCallForwardingActive);
                newCallForwarding.setForwardTo(mCallForwardingForwardTo);
            }
	    
	    ZSelectiveCallForwardingBean selectiveCallForwarding = oldFeatures.getSelectiveCallForwarding();
	    if (selectiveCallForwarding.getIsActive() != mSelectiveCallForwardingActive || !selectiveCallForwarding.getForwardTo().equals(mSelectiveCallForwardingForwardTo) || !selectiveCallForwarding.getForwardFrom().equals(mSelectiveCallForwardingForwardFrom)) {
		ZSelectiveCallForwardingBean newSelectiveCallForwarding = newFeatures.getSelectiveCallForwarding();
		newSelectiveCallForwarding.setIsActive(mSelectiveCallForwardingActive);
		newSelectiveCallForwarding.setForwardTo(mSelectiveCallForwardingForwardTo);
		newSelectiveCallForwarding.setForwardFrom(mSelectiveCallForwardingForwardFrom);
	    }
	    
	    ZCallFeatureBean anonymousCallRejection = oldFeatures.getAnonymousCallRejection();
	    if (anonymousCallRejection.getIsActive() != mAnonymousCallRejectionActive) {
		newFeatures.getAnonymousCallRejection().setIsActive(mAnonymousCallRejectionActive);
	    }
	    
	    /* Uncomment when selective call rejection is implemented in the soap interface
	    
	    ZSelectiveCallRejectionBean selectiveCallRejection = oldFeatures.getSelectiveCallRejection();
	    if (selectiveCallRejection.getIsActive() != mSelectiveCallRejectionActive || !selectiveCallRejection.getRejectFrom().equals(mSelectiveCallRejectionRejectFrom)) {
		ZSelectiveCallRejectionBean newSelectiveCallRejection = newFeatures.getSelectiveCallRejection();
		newSelectiveCallRejection.setIsActive(mSelectiveCallRejectionActive);
		newSelectiveCallRejection.setRejectFrom(mSelectiveCallRejectionRejectFrom);
	    }
	    
	    */
	    
	    if (callForwardingNoAnswer.getNumberOfRings() != mNumberOfRings) {
		newFeatures.getCallForwardingNoAnswer().setNumberOfRings(mNumberOfRings);
	    }	
	    if (voiceMailPrefs.getAutoPlayNewMsgs() != mAutoPlayNewMsgs) {
		newFeatures.getVoiceMailPrefs().setAutoPlayNewMsgs(mAutoPlayNewMsgs);
	    }
	    if (voiceMailPrefs.getPlayDateAndTimeInMsgEnv() != mPlayDateAndTimeInMsgEnv) {
		newFeatures.getVoiceMailPrefs().setPlayDateAndTimeInMsgEnv(mPlayDateAndTimeInMsgEnv);
	    }
	    if (voiceMailPrefs.getSkipPinEntry() != mSkipPinEntry) {
		newFeatures.getVoiceMailPrefs().setSkipPinEntry(mSkipPinEntry);
	    }
	    if (voiceMailPrefs.getPlayCallerNameInMsgEnv() != mPlayCallerNameInMsgEnv) {
		newFeatures.getVoiceMailPrefs().setPlayCallerNameInMsgEnv(mPlayCallerNameInMsgEnv);
	    }
	    if (mAutoPlayNewMsgs && !voiceMailPrefs.getPromptLevel().equals(mPromptLevel)) {
		newFeatures.getVoiceMailPrefs().setPromptLevel(mPromptLevel);
	    }
	    if (!voiceMailPrefs.getAnsweringLocale().equals(mAnsweringLocale)) {
		newFeatures.getVoiceMailPrefs().setAnsweringLocale(mAnsweringLocale);
	    }
	    if (!voiceMailPrefs.getUserLocale().equals(mUserLocale)) {
		newFeatures.getVoiceMailPrefs().setUserLocale(mUserLocale);
	    }
	    
            
	    boolean update = false;
            if (!newFeatures.isEmpty()) {
            	mailbox.saveCallFeatures(newFeatures.getCallFeatures());
		update = true;
	    }
	    if (mailbox.getPrefs().getVoiceItemsPerPage() != mNumberPerPage) {
		Map<String, Object> attrs = new HashMap<String,Object>();
		attrs.put(Provisioning.A_zimbraPrefVoiceItemsPerPage, Long.toString(mNumberPerPage));
		mailbox.modifyPrefs(attrs);
		update = true;
		mailbox.getPrefs(true);
	    }
	    getJspContext().setAttribute(mVar, update, PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
