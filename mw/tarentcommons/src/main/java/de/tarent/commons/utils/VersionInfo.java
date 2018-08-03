package de.tarent.commons.utils;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * Abstraction of version information for a software package
 */
public class VersionInfo {

    /**
     * name of the software package
     */
    String name;
    /**
     * description of the software package
     */
    String description;
    /**
     * version string of the software package
     */
    String version;
    /**
     * additional build info (e.g. build date and host) of the software package
     */
    String buildInfo;
    /**
     * copyright info of the software package
     */
    String copyright;
    /**
     * vendor information of the software package
     */
    String vendor;
    /**
     * name of the resource for which the version info is
     */
    String resourceName;
    /**
     * id of the build contains user, host, build-time
     */
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
        if (getResourceName() == null) {
            return appResourceName;
        } else {
            return this.resourceName;
        }
    }

    public String getVendor(String appVendor) {
        if (getVendor() == null) {
            return appVendor;
        } else {
            return this.vendor;
        }
    }

    public String getBuildInfo(String appBuildInfo) {
        if (getBuildInfo() == null) {
            return appBuildInfo;
        } else {
            return this.buildInfo;
        }
    }

    public String getDescription(String appDescription) {
        if (getDescription() == null) {
            return appDescription;
        } else {
            return this.description;
        }
    }

    public String getCopyright(String appCopyrigth) {
        if (getCopyright() == null) {
            return appCopyrigth;
        } else {
            return this.copyright;
        }
    }

    public String getName(String appName) {
        if (getName() == null) {
            return appName;
        } else {
            return this.name;
        }
    }

    public String getVersion(String appVersion) {
        if (getVersion() == null) {
            return appVersion;
        } else {
            return this.version;
        }
    }

    public String getBuildID(String appBuildID) {
        if (getBuildID() == null) {
            return appBuildID;
        } else {
            return this.buildID;
        }
    }

    public String toString() {
        if (getName() == null && getVersion() == null) {
            return getResourceName() + ": no version info present";
        }
        return getResourceName() + ": " + getName() + ", " + getVersion();
    }
}
