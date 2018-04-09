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

import de.tarent.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.io.*;
import java.util.*;
import java.util.jar.Attributes;
import de.tarent.octopus.server.OctopusContext;
import java.util.Collections;



/**                                            
 * This is again an Implementation of a generic tool for collecting version information.
 * The goal of this implementation is to support different environments like 'installed client application' or 'war/ear archive',
 * as well as different version info ressource like. Simple properties file, Manifest file.
 * 
 * @author Sebastian Mancke (s.mancke@tarent.de) tarent GmbH Bonn
 */
public class VersionTool {
    
    private static final Log log = LogFactory.getLog(VersionTool.class);

    public static final String PREFIX_PACKAGE = "package:";
    public static final String PREFIX_RESOURCE = "resource:";
    public static final String PREFIX_JAR = "jar:";

    public static final String VERSION_NAME = "version_name";
    public static final String VERSION_DESCRIPTION = "version_description";
    public static final String VERSION_COPYRIGHT = "version_copyright";
    public static final String VERSION_VERSION = "version_version";
    public static final String VERSION_BUILD_INFO = "version_buildInfo";
    public static final String VERSION_VENDOR = "version_vendor";
    public static final String BuildID = "Build-ID";

    List versionInfos = new ArrayList();

    VersionInfo mainVersionInfo;

    /**
     * Search the searchPath for Version infos.
     * Depending on the type the search entries may have a describing prefix. Valid entries in the searchPath are: 
     * <ul>
     *    <li>Directory (prefix: none). The directoy will be searched for a META-INF/MANIFEST.MF, as well as for jar-files, which are included with there manifest.</li>
     *    <li>Package Names (prefix: 'package:')</li>
     *    <li>Properties resource file (prefix: 'resource:')</li>
     *    <li>Jar files (prefix: 'jar:')</li>
     * <ul>
     * Depending on the type, the version info is collected.
     */
    public void search(String[] searchPath) {
        for (int i = 0; i < searchPath.length; i++) {
            VersionInfo info = null;
            if (searchPath[i].startsWith(PREFIX_PACKAGE)) {
                info = getInfoFromPackage(searchPath[i].substring(PREFIX_PACKAGE.length()));
            } 
            else if (searchPath[i].startsWith(PREFIX_RESOURCE)) {
                info = getInfoFromResourceFile(searchPath[i].substring(PREFIX_RESOURCE.length()));
            } 
            else if (searchPath[i].startsWith(PREFIX_JAR)) {
                info = getInfoFromJar(searchPath[i].substring(PREFIX_JAR.length()));
            }
            else {
                info = getInfoFromDirectory(searchPath[i]);
                searchInDirectory(searchPath[i]);
            }
            if (info != null)
                versionInfos.add(info);
        }

        if (mainVersionInfo == null && versionInfos.size() > 0)
            mainVersionInfo = (VersionInfo)versionInfos.get(0);

        Collections.sort(versionInfos, new Comparator() {
                public int compare(Object o1, Object o2) {
                    VersionInfo v1 = (VersionInfo)o1;
                    VersionInfo v2 = (VersionInfo)o2;

                    if (v1.getVendor() == null && v2.getVendor() == null)
                        return 0;

                    if (v1.getVendor() != null && v1.getVendor().startsWith("tarent"))
                        return -1;
                    if (v2.getVendor() != null && v2.getVendor().startsWith("tarent"))
                        return 1;
                    
                    if (v1.getVendor() != null && v2.getVendor() == null)
                        return -1;

                    if (v2.getVendor() != null && v1.getVendor() == null)
                        return 1;
                    
                    return v1.getVendor().compareTo(v2.getVendor());
                }
                
                public boolean equals(Object obj) {
                    return false;
                }
            });
    }

    public static String[] INPUT_getVersions = {"prependingSearchPath", "appendingSearchPath"};
    public static boolean[] MANDATORY_getVersions = {false, false};
    public static String OUTPUT_getVersions = "versionInfos";
    
    /**
     * Octopus action, which searches the module path and the supplied path for version information.
     * @params cntx the octopus context
     * @param prependingSearchPath Path string with ':' as delimiter, prepended for the default path, may be null
     * @param appendingSearchPath Path string with ':' as delimiter, appended for the default path, may be null
     */
    public static VersionTool getVersions(OctopusContext cntx, String prependingSearchPath, String appendingSearchPath) {
        VersionTool vt = new VersionTool();
        if (prependingSearchPath != null)
            vt.search(prependingSearchPath.split(":"));
        vt.search(new String[]{
                      cntx.moduleRootPath().getParentFile().getAbsolutePath(),
                      new File(new File(cntx.moduleRootPath().getParentFile(), "WEB-INF"),"lib").getAbsolutePath(),
                      new File(new File(cntx.moduleRootPath().getParentFile(), "OCTOPUS"),"lib").getAbsolutePath()
                  });
        if (appendingSearchPath != null)
            vt.search(appendingSearchPath.split(":"));
        return vt;
    }

    public static void main(String args[]) {
        VersionTool vt = new VersionTool();
        vt.search(args);
        for (Iterator iter = vt.getVersionInfos().iterator(); iter.hasNext();) {
            VersionInfo vi = (VersionInfo)iter.next();
            System.out.println(vi);            
        }        
    }

    /**
     * Returns the main VersionInfo (first in the path);
     * @returns list of VersionInfo objects from the search path.
     */
    public VersionInfo getMainVersionInfo() {
        return mainVersionInfo;
    }

    /**
     * Returns the list off all VersionInfos
     * @returns list of VersionInfo objects from the search path.
     */
    public List getVersionInfos() {
        return versionInfos;
    }

    public void searchInDirectory(String dirname) {
        File f = new File(dirname);
        if (!f.exists() || !f.isDirectory()) {
            log.warn("directory '"+dirname+"' for version info not found");
        }
        File[] files = f.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith(".jar")) {
                    VersionInfo info = getInfoFromJar(files[i].getAbsolutePath());
                    if (info != null)
                        versionInfos.add(info);
                }
            }
        }
    }
    
    public static VersionInfo getInfoFromClass(Class klass)
    {
//      return getInfoFromPackage(klass.getPackage());
    	return getInfoFromJar(klass.getProtectionDomain().getCodeSource().getLocation().getFile());
    }
    
    public static VersionInfo getInfoFromPackage(String packageName) {
      return getInfoFromPackage(Package.getPackage(packageName));
    }
      
    public static VersionInfo getInfoFromPackage(Package packageDescriptor) {
        String packageName = packageDescriptor.getName();
        
        if (packageDescriptor == null) {
            log.warn("package '"+packageName+"' for version info not found");
            return null;
        }
        VersionInfo info = new VersionInfo();
        info.setResourceName("Package: "+packageName);
        info.setName(specImplConcat(packageDescriptor.getSpecificationTitle(), packageDescriptor.getImplementationTitle()));
        info.setVersion(specImplConcat(packageDescriptor.getSpecificationVersion(), packageDescriptor.getImplementationVersion()));
        info.setVendor(specImplConcat(packageDescriptor.getSpecificationVendor(), packageDescriptor.getImplementationVendor()));
        return info;
    }

    public static VersionInfo getInfoFromResourceFile(String resName) {
        InputStream is = VersionTool.class.getResourceAsStream(resName);
        if (is == null) {
            log.warn("resource '"+resName+"' for version info not found in classpath");
            return null;
        }
        try {
            Properties props = new Properties();
            props.load(is);
            is.close();
            VersionInfo info = new VersionInfo();
            info.setResourceName("Resource file: "+resName);
            info.setName(props.getProperty(VERSION_NAME));
            info.setDescription(props.getProperty(VERSION_DESCRIPTION));
            info.setCopyright(props.getProperty(VERSION_COPYRIGHT));
            info.setVersion(props.getProperty(VERSION_VERSION));
            info.setBuildInfo(props.getProperty(VERSION_BUILD_INFO));
            info.setVendor(props.getProperty(VERSION_VENDOR));
            info.setBuildID(props.getProperty(BuildID));
            return info;
        } catch (IOException ioe) {
            log.error("cant read '"+resName+"'", ioe);
            return null;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException ioe) {}
        }
    }

    public static VersionInfo getInfoFromJar(String jarPathName) {
        File f = new File(jarPathName);
        if (!f.exists()) {
            log.warn("file '"+jarPathName+"' for version info not found");
//            return null;
            // return new VersionInfo instead of null
            return new VersionInfo();
        }
        JarFile jar = null;
        try {
            jar = new JarFile(f);
            VersionInfo info = getInfoFromManifest(jar.getManifest());
            info.setResourceName("jar: "+jarPathName);
            if (info.getName() == null || info.getName().trim().length() == 0)
                info.setName(f.getName());
            return info;
        } catch (IOException ioe) {
            log.info("cant read '"+jarPathName+"'. probably not running a jar-build");
//          return new VersionInfo instead of null
            return new VersionInfo();
        } finally {
            try {
                if (jar != null)
                    jar.close();
            } catch (IOException ioe) {}
        }
    }
    
    public static VersionInfo getInfoFromDirectory(String dirname) {
        File f = new File(new File(dirname, "META-INF"), "MANIFEST.MF");
        if (!f.exists()) {
            log.info("file '"+f.getAbsolutePath()+"' for version info not found");
            return null;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            VersionInfo info = getInfoFromManifest(new Manifest(fis));
            info.setResourceName("Directory: "+dirname);
            return info;
        } catch (IOException ioe) {
            log.error("cant read '"+f.getAbsolutePath()+"'", ioe);
            return null;
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ioe) {}
        }
    }

    public static VersionInfo getInfoFromManifest(Manifest manifest) {
        Attributes att = manifest.getMainAttributes();
        VersionInfo info = new VersionInfo();

        // use standard attributes as defaiult
        info.setName(specImplConcat(att.getValue("Specification-Title"), att.getValue("Implementation-Title")));
        info.setVersion(specImplConcat(att.getValue("Specification-Version"), att.getValue("Implementation-Version")));
        info.setVendor(specImplConcat(att.getValue("Specification-Vendor"), att.getValue("Implementation-Vendor")));
        info.setBuildInfo(att.getValue("Implementation-Build"));

        if (att.getValue(VERSION_NAME) != null)
            info.setName(att.getValue(VERSION_NAME));
        if (att.getValue(VERSION_DESCRIPTION) != null)
            info.setDescription(att.getValue(VERSION_DESCRIPTION));
        if (att.getValue(VERSION_COPYRIGHT) != null)
            info.setCopyright(att.getValue(VERSION_COPYRIGHT));
        if (att.getValue(VERSION_VERSION) != null)
            info.setVersion(att.getValue(VERSION_VERSION));
        if (att.getValue(VERSION_BUILD_INFO) != null)
            info.setBuildInfo(att.getValue(VERSION_BUILD_INFO));
        if (att.getValue(VERSION_VENDOR) != null)
            info.setVendor(att.getValue(VERSION_VENDOR));
        if (att.getValue(BuildID) != null)
        	info.setBuildID(att.getValue(BuildID));
        return info;
    }
    
    static String specImplConcat(String specText, String implText) {
        if ( (implText == null || implText.trim().length() == 0)
             && specText != null)
            return "Specification: "+specText;
        return implText;
    }
}
