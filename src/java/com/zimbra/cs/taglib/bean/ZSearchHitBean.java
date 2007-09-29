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
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZSearchHit;

public abstract class ZSearchHitBean {
    
    public enum HitType { conversation, contact, message }
    
    private HitType mHitType;
    private ZSearchHit mHit;
    
    protected ZSearchHitBean(ZSearchHit hit, HitType hitType) {
        mHit = hit;
        mHitType = hitType;
    }
    
    public final String getId() { return mHit.getId(); }
    
    public final String getSortField() { return mHit.getSortField(); }
    
    public final float getScore() { return mHit.getScore(); }
    
    public final String getHitType() { return mHitType.name(); }

    public final boolean getIsConversation() { return mHitType == HitType.conversation; }
    
    public final boolean getIsMessage() { return mHitType == HitType.message; }
    
    public final boolean getIsContact() { return mHitType == HitType.contact; }

    public final ZConversationHitBean getConversationHit() { return getIsConversation() ? (ZConversationHitBean) this : null; }

    public final ZMessageHitBean getMessageHit() { return getIsMessage() ? (ZMessageHitBean) this : null; }

    public final ZContactHitBean getContactHit() { return getIsContact() ? (ZContactHitBean) this : null; }

}
