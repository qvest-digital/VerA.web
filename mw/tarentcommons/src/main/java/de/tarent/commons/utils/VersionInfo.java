/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.utils;

/**
 * Abstraction of version information for a software package
 */
public class VersionInfo {

    /** name of the software package */
    String name;
    /** description of the software package */
    String description;
    /** version string of the software package */
    String version;
    /** additional build info (e.g. build date and host) of the software package */
    String buildInfo;
    /** copyright info of the software package */
    String copyright;
    /** vendor information of the software package */
    String vendor;
    /** name of the resource for which the version info is */
    String resourceName;
    /** id of the build contains user, host, build-time */
    String buildID;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String newResourceName) {
        this.resourceName = newResourceName;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String newVendor) {
        this.vendor = newVendor;
    }

    public String getBuildInfo() {
        return buildInfo;
    }

    public void setBuildInfo(String newBuildInfo) {
        this.buildInfo = newBuildInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String newCopyright) {
        this.copyright = newCopyright;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String newVersion) {
        this.version = newVersion;
    }

    public String getBuildID() {
		return buildID;
	}

	public void setBuildID(String buildID) {
		this.buildID = buildID;
	}

    public String getResourceName(String appResourceName) {
        if(getResourceName() == null){
        	return appResourceName;
        }else{
        	return this.resourceName;
        }
    }

    public String getVendor(String appVendor) {
    	if(getVendor() == null){
        	return appVendor;
        }else{
        	return this.vendor;
        }
    }

    public String getBuildInfo(String appBuildInfo) {
    	if(getBuildInfo() == null){
        	return appBuildInfo;
        }else{
        	return this.buildInfo;
        }
    }

    public String getDescription(String appDescription) {
    	if(getDescription() == null){
        	return appDescription;
        }else{
        	return this.description;
        }
    }

    public String getCopyright(String appCopyrigth) {
    	if(getCopyright() == null){
        	return appCopyrigth;
        }else{
        	return this.copyright;
        }
    }

    public String getName(String appName) {
    	if(getName() == null){
        	return appName;
        }else{
        	return this.name;
        }
    }

    public String getVersion(String appVersion) {
    	if(getVersion() == null){
        	return appVersion;
        }else{
        	return this.version;
        }
    }

    public String getBuildID(String appBuildID) {
    	if(getBuildID() == null){
        	return appBuildID;
        }else{
        	return this.buildID;
        }
	}

	public String toString() {
        if (getName() == null && getVersion() == null)
            return getResourceName()+": no version info present";
        return getResourceName()+": "+getName() +", "+getVersion();
    }
}
