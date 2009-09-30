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
import com.zimbra.cs.taglib.bean.ZVoiceMailItemHitBean;
import com.zimbra.cs.taglib.bean.ZSelectiveCallRejectionBean;
import com.zimbra.cs.taglib.bean.ZVoiceMailPrefsBean;
import com.zimbra.cs.zclient.ZCallFeatures;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZPhoneAccount;
import com.zimbra.cs.zclient.ZPhone;
import com.zimbra.cs.account.Provisioning;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.List;

public class AddToRejectFeatureTag extends CallFeaturesTagBase {

	private String[] mVoiceId;
	private String mVar;
	private String mError;
	private int mMax = 12;
	
	public void setVoiceId(String[] voiceId) {
		mVoiceId = voiceId;
	}
	
	public void setVar(String var) {
		mVar = var;
	}
	
	public void setError(String error) {
		mError = error;
	}

	public void setMax(String max) {
		try {
			mMax = Integer.parseInt(max);
		} catch (NumberFormatException ex) {
			mMax = 12;
		}
	}

	public void doTag() throws JspException, IOException {
	
		try {
			ZMailbox mailbox = getMailbox();
			ZPhoneAccount account = mailbox.getPhoneAccount(mPhone);
			ZCallFeaturesBean oldFeatures = new ZCallFeaturesBean(account.getCallFeatures(), false);
			ZCallFeaturesBean newFeatures = new ZCallFeaturesBean(new ZCallFeatures(mailbox, account.getPhone()), true);
		
			ZSelectiveCallRejectionBean selectiveCallRejection = oldFeatures.getSelectiveCallRejection();
			ZSelectiveCallRejectionBean newSelectiveCallRejection = newFeatures.getSelectiveCallRejection();
		
			List<String> rejectFrom = selectiveCallRejection.getRejectFrom();
			String firstError=null;
			int added = 0;

			if (rejectFrom.size() >= mMax) {
				firstError = ZCallFeatures.SELECTIVE_CALL_REJECT_LIST_FULL;
			} else if (newSelectiveCallRejection.getIsSubscribed()) {
				for (String id : mVoiceId) {
					ZVoiceMailItemHitBean hit = ZVoiceMailItemHitBean.deserialize(id, account.getPhone().getDisplay());
					ZPhone caller = hit.getCaller();
		
					if (account.getPhone().getName().equals(caller.getName())) {
						firstError = ZPhone.INVALID_PHNUM_OWN_PHONE_NUMBER;
					} else {
						String validity = caller.getValidity();
						if (validity.equals(ZPhone.VALID)) {
							String displayName = caller.getDisplay();
							if (!rejectFrom.contains(displayName) && rejectFrom.size() < mMax) {
								rejectFrom.add(displayName);
								added++;
							}
						} else if (firstError==null) {
							firstError = validity;
						}
					}
				}
				if (added > 0) {
					newSelectiveCallRejection.setIsActive(true);
					newSelectiveCallRejection.setRejectFrom(rejectFrom);
				}
			}
			boolean update = false;
			if (!newFeatures.isEmpty() && added > 0) {
				mailbox.saveCallFeatures(newFeatures.getCallFeatures());
				update = true;
			}
			if (mVar!=null) getJspContext().setAttribute(mVar, update ? added : 0, PageContext.PAGE_SCOPE);
			if (mError!=null) getJspContext().setAttribute(mError, firstError, PageContext.PAGE_SCOPE);
		
		} catch (ServiceException e) {
			throw new JspTagException(e);
		}
	}
}
