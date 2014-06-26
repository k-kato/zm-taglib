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
package com.zimbra.cs.taglib.bean;

import com.zimbra.client.ZContactHit;
import com.zimbra.client.ZEmailAddress;

public class ZContactHitBean extends ZSearchHitBean {
    
    private ZContactHit mHit;
    
    public ZContactHitBean(ZContactHit hit) {
        super(hit, HitType.contact);
        mHit = hit;
    }

    public String getFlags() { return mHit.getFlags(); }

    public boolean getHasFlags() { return mHit.hasFlags(); }
    
    public boolean getIsFlagged() { return mHit.isFlagged(); }

    public boolean getHasTags() { return mHit.hasTags(); }

    public boolean getIsGroup() { return mHit.isGroup(); }
    
    public String getTagIds() { return mHit.getTagIds(); }    

    public String getFolderId() { return mHit.getFolderId(); }

    public String getRevision() { return mHit.getRevision(); }
    
    public String getFileAsStr() { return mHit.getFileAsStr(); } 

    public String getEmail() { return mHit.getEmail(); }

    public String getEmail2() { return mHit.getEmail2(); }

    public String getEmail3() { return mHit.getEmail3(); }

    public String getWorkEmail1() { return mHit.getWorkEmail1(); }

    public String getWorkEmail2() { return mHit.getWorkEmail2(); }

    public String getWorkEmail3() { return mHit.getWorkEmail3(); }

    public String getType() { return mHit.getType(); }

    public String getDlist() { return mHit.getDlist(); }

    public String toString() { return mHit.toString(); }

    public String getFullName() { return mHit.getFullName(); }
    public String getFileAs() { return mHit.getFileAs(); }
    public String getNickname() { return mHit.getNickname(); }
    public String getNamePrefix() { return mHit.getNamePrefix(); }
    public String getFirstName() { return mHit.getFirstName(); }
    public String getPhoneticFirstName() { return mHit.getPhoneticFirstName(); }
    public String getMiddleName() { return mHit.getMiddleName(); }
    public String getMaidenName() { return mHit.getMaidenName(); }
    public String getLastName() { return mHit.getLastName(); }
    public String getPhoneticLastName() { return mHit.getPhoneticLastName(); }
    public String getNameSuffix() { return mHit.getNameSuffix(); }
    public String getCompany() { return mHit.getCompany(); }
    public String getPhoneticCompany() { return mHit.getPhoneticCompany(); }
    
    /**
     * @return time in msecs
     */
    public long getMetaDataChangedDate() { return mHit.getMetaDataChangedDate(); }
    
    /**
     * @return first email from email/2/3 that is set, or an empty string
     */
    public String getDisplayEmail() {
        if (getIsGroup())
            return getDlist();
        else if (getEmail() != null && getEmail().length() > 0) 
            return getEmail();
        else if (getEmail2() != null && getEmail2().length() > 0) 
            return getEmail2();
        else if (getEmail3() != null && getEmail3().length() > 0) 
            return getEmail3();
        else
            return "";
    }
    
    /**
     *
     * @return the "full" email address suitable for inserting into a To/Cc/Bcc header
     */
    public String getFullAddress() {
        if (getIsGroup()) {
            return getDlist();
        } else {
            return new ZEmailAddress(getDisplayEmail(), null, getFileAsStr(), ZEmailAddress.EMAIL_TYPE_TO).getFullAddress();
        }
    }

    public String getImage() {
        if (getIsGroup())
            return "contacts/ImgGroup.png";
        else
            return "contacts/ImgContact.png";
    }

    public String getImageAltKey() {
        if (getIsGroup())
            return "ALT_CONTACT_GROUP";
        else
            return "ALT_CONTACT_CONTACT";
    }
}
