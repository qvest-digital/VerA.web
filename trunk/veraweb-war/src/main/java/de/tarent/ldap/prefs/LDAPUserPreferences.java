/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
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
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */
package de.tarent.ldap.prefs;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import de.tarent.ldap.LDAPException;
import de.tarent.ldap.LDAPManager;

/**
 * @author kirchner
 * 
 * Preferences im LDAP
 */
public class LDAPUserPreferences extends AbstractPreferences {

	LDAPManager		ldm			= null;
	Properties		properties	= new Properties();
	//Map für key/value der Preferences
	Map				cache		= new HashMap();
	//Map für timestamp der Preferences
	Map				timestamp	= new HashMap();
	private Boolean	deleted		= Boolean.FALSE;
	private String	user;

	/**
	 * Konstruktor
	 * 
	 * @param arg0
	 *            ist dieses null, ist die Preference eine Root-Preference
	 * @param arg1
	 */
	protected LDAPUserPreferences(AbstractPreferences arg0, String arg1) {
		super(arg0, arg1);
		try {
			// Daten in den Cache laden, wenn möglich
			syncSpi();
		} catch (BackingStoreException e) {}
	}

	/**
	 * Flush-Methode, die die cache-Map ins LDAP schreibt. Vorgehensweise: - Key
	 * für Key wird als pref=key im LDAP gespeichert, value in das
	 * Value-Attribut
	 * 
	 * @see java.util.prefs.AbstractPreferences#flushSpi()
	 */
	@Override
    protected void flushSpi() throws BackingStoreException {
		initLDM();
		createPath();
		String relativeold = adjustRelative();
		//Alle Cache-Einträge schreiben
		Iterator it = cache.keySet().iterator();
		try {
			while (it.hasNext()) {
				String key = (String) it.next();
				Object valueO = cache.get(key);
				try {
					if (valueO == null) {
						ldm.deleteSystemPreferenceKey(key);
						cache.remove(key);
						timestamp.remove(key);
					} else {
						String value = (String) valueO;
						BigInteger modified = (BigInteger) timestamp.get(key);
						ldm.updateOrCreateSystemPreferenceKey(key, value, modified);
					}
				} catch (LDAPException e) {
					throw new BackingStoreException(e);
				}
			}
			if (deleted.equals(Boolean.TRUE)) {
				ldm.deleteSystemPreferenceNode();
			}
		} catch (BackingStoreException e) {
			throw (e);
		} catch (LDAPException e) {
			throw new BackingStoreException(e);
		} finally {
			ldm.setRelative(relativeold);
		}
	}

	/**
	 *  
	 */
	private String adjustRelative() throws BackingStoreException {
		initLDM();
		String relative = ldm.getRelativeUser();
		String relativeOld = ldm.getRelative();
		String dn = null;
		try {
			dn = ldm.getUserDN(user);
		} catch (LDAPException e) {
			throw new BackingStoreException(e);
		}
		relative = dn + relative;
		String path = absolutePath();
		StringTokenizer st = new StringTokenizer(path, "/");
		while (st.hasMoreTokens()) {
			relative = "PreferenceKey=" + st.nextToken() + "," + relative;
		}
		ldm.setRelative(relative);
		return relativeOld;
	}

	/**
	 * @throws BackingStoreException
	 */
	private void createPath() throws BackingStoreException {
		initLDM();
		StringTokenizer st = new StringTokenizer(absolutePath(), "/");
		String relativeOld = ldm.getRelative();
		String relative = ldm.getRelativeUser();
		String dn = null;
		try {
			dn = ldm.getUserDN(user);
		} catch (LDAPException e) {
			throw new BackingStoreException(e);
		}
		relative = dn + relative;
		ldm.setRelative(relative);
		relative = ldm.getRelative();
		try {
			while (st.hasMoreTokens()) {
				String pathElement = st.nextToken();
				try {
					String dn2 = ldm.searchSystemPreferenceNode("PreferenceKey", pathElement);
					if (dn2 == null) {
						//Preference existiert nicht
						ldm.createSystemPreferenceNode("PreferenceKey=" + pathElement);
						dn2 = ldm.searchSystemPreferenceNode("PreferenceKey", pathElement);
					}
					ldm.setRelative(dn2 + relative);
					relative = ldm.getRelative();
				} catch (LDAPException e) {
					throw new BackingStoreException(e);
				}
			}
		} catch (BackingStoreException e) {
			throw e;
		} finally {
			ldm.setRelative(relativeOld);
		}
	}

	/**
	 * @throws BackingStoreException
	 */
	private void initLDM() throws BackingStoreException {
		if (ldm == null) {
			Properties prop = LDAPPreferencesFactory.getLDAPProperties();
			String basedn = prop.getProperty("ldapbasedn");
			if (basedn == null) {
				throw new BackingStoreException("Property ldapbasedn nicht richtig gesetzt!");
			}
			String url = prop.getProperty("ldapurl");
			if (url == null) {
				throw new BackingStoreException("Property ldapurl nicht richtig gesetzt!");
			}
			String relative = prop.getProperty("ldaprelative");
			if (relative == null) {
				throw new BackingStoreException("Property ldaprelative nicht richtig gesetzt!");
			}
			String relativeUser = prop.getProperty("ldapuserrelative");
			if (relativeUser == null) {
				throw new BackingStoreException("Property ldapuserrelative nicht richtig gesetzt!");
			}
			user = prop.getProperty("ldapuser");
			if (user == null) {
				throw new BackingStoreException("Property ldapuser nicht richtig gesetzt!");
			}
			String pwd = prop.getProperty("ldappwd");
			if (pwd == null) {
				throw new BackingStoreException("Property ldappwd nicht richtig gesetzt!");
			}
			String auth = prop.getProperty("ldapauth");
			if (auth == null) {
				throw new BackingStoreException("Property ldapauth nicht richtig gesetzt!");
			}
			try {
                Map params = new HashMap();
                params.put(LDAPManager.KEY_BASE_DN, "dc=tarent,dc=de");
                params.put(LDAPManager.KEY_RELATIVE, "ou=Adressen");
                params.put(LDAPManager.KEY_RELATIVE_USER, "ou=People");
                ldm = LDAPManager.login(LDAPManager.class, url, params)
                    .login(user, pwd, auth);
			} catch (LDAPException e) {
				throw new BackingStoreException(e);
			}
		}
	}

	@Override
    public boolean isUserNode() {
		return true;
	}

	/**
	 * Löscht diese Preference aus dem LDAP, löscht also alle gespeicherten
	 * Preferences, die mit diesem Knoten verbunden sind, und anschließend sich
	 * selbst. Alle Operationen läufen nur im Cache ab, geschrieben wird erst
	 * beim nächsten
	 * 
	 * @see flushSpi()
	 * @see java.util.prefs.AbstractPreferences#removeNodeSpi()
	 */
	@Override
    protected void removeNodeSpi() throws BackingStoreException {
		Iterator it = cache.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			cache.remove(key);
			timestamp.remove(key);
			cache.put(key, null);
			timestamp.put(key, BigInteger.valueOf((new Date()).getTime()));
		}
		flushSpi();
		deleted = Boolean.TRUE;
		initLDM();
		String relativeOld = adjustRelative();
		try {
			ldm.deleteSystemPreferenceNode();
		} catch (LDAPException e) {
			//Tue nichts, erst ein Flush oder Sync geben Garantie
		} finally {
			ldm.setRelative(relativeOld);
		}
	}

	/**
	 * Zuerst werden alle neueren Daten geschrieben, danach der Cache komplett
	 * gefüllt
	 * 
	 * @see java.util.prefs.AbstractPreferences#syncSpi()
	 */
	@Override
    protected void syncSpi() throws BackingStoreException {
		initLDM();
		createPath();
		String relativeOld = adjustRelative();
		//erstmal alle neueren Schreiben...
		Iterator it = cache.keySet().iterator();
		try {
			while (it.hasNext()) {
				String key = (String) it.next();
				Object valueO = cache.get(key);
				try {
					if (valueO == null) {
						ldm.deleteSystemPreferenceKey(key);
						cache.remove(key);
						timestamp.remove(key);
					} else {
						String value = (String) valueO;
						BigInteger modified = (BigInteger) timestamp.get(key);
						Attributes attrs = ldm.getSystemPreferenceKey(key);
						BigInteger ldapmodified = new BigInteger((String) attrs.get("PreferenceLastModified").get(0));
						if (modified.compareTo(ldapmodified) == 1) {
							//Unser Wert ist neuer als der im LDAP
							ldm.updateOrCreateSystemPreferenceKey(key, value, modified);
						}
					}
				} catch (NamingException e) {
					throw new BackingStoreException(e);
				} catch (LDAPException e) {
					throw new BackingStoreException(e);
				}
			}
			if (deleted.equals(Boolean.TRUE)) {
				ldm.deleteSystemPreferenceNode();
			}
		} catch (BackingStoreException e) {
			throw (e);
		} catch (LDAPException e) {
			throw new BackingStoreException(e);
		} finally {
			ldm.setRelative(relativeOld);
		}
		cache = new HashMap();
		timestamp = new HashMap();
		//Dann alle lesen...
		String keys[] = keysSpi();
		adjustRelative();
		try {
			for (int i = 0; i < keys.length; i++) {
				try {
					Attributes attrs = ldm.getSystemPreferenceKey(keys[i]);
					timestamp.put(keys[i], new BigInteger((String) attrs.get("PreferenceLastModified").get(0)));
					cache.put(keys[i], attrs.get("PreferenceValue").get(0));
				} catch (NamingException e1) {
					throw new BackingStoreException(e1);
				}
			}
		} catch (BackingStoreException e1) {
			throw (e1);
		} finally {
			ldm.setRelative(relativeOld);
		}
	}

	/**
	 * Hold die Namen aller Kindknoten aus dem LDAP
	 * 
	 * @see java.util.prefs.AbstractPreferences#childrenNamesSpi()
	 */
	@Override
    protected String[] childrenNamesSpi() throws BackingStoreException {
		String children[] = null;
		initLDM();
		String relativeOld = adjustRelative();
		try {
			children = ldm.getSystemPreferenceChildren();
		} catch (LDAPException e) {
			throw new BackingStoreException(e);
		} finally {
			ldm.setRelative(relativeOld);
		}
		return children;
	}

	/**
	 * Holt eine Liste aller Keys aus dem LDAP
	 * 
	 * @see java.util.prefs.AbstractPreferences#keysSpi()
	 */
	@Override
    protected String[] keysSpi() throws BackingStoreException {
		initLDM();
		String relativeOld = adjustRelative();
		String keys[] = null;
		try {
			keys = ldm.getSystemPreferenceKeys();
		} catch (LDAPException e) {
			throw new BackingStoreException(e);
		} finally {
			ldm.setRelative(relativeOld);
		}
		return keys;
	}

	/**
	 * Löscht eine Preference im LDAP, dazu wird ihr Cache-Wert auf null gesetzt
	 * 
	 * @see java.util.prefs.AbstractPreferences#removeSpi(java.lang.String)
	 */
	@Override
    protected void removeSpi(String key) {
		BigInteger modifed = BigInteger.valueOf((new Date()).getTime());
		if (cache.containsKey(key)) {
			cache.remove(key);
			timestamp.remove(key);
			cache.put(key, null);
			timestamp.put(key, modifed);
		} else {
			cache.put(key, null);
			timestamp.put(key, modifed);
		}
		try {
			initLDM();
			String relativeOld = adjustRelative();
			try {
				ldm.deleteSystemPreferenceKey(key);
				cache.remove(key);
				timestamp.remove(key);
			} catch (LDAPException e1) {
				throw new BackingStoreException(e1);
			} finally {
				ldm.setRelative(relativeOld);
			}
		} catch (BackingStoreException e) {
			// Nichts tun, Garantie fürs schreiben gibts erst beim Sync oder
			// Flush
		}
	}

	/**
	 * Liest Settings aus dem LDAP... Zunächst wird geschaut, ob im Cache noch
	 * etwas liegt, danach wird das LDAP befragt
	 * 
	 * @param key -
	 *            Der Key nach dem gesucht wird
	 * 
	 * @see java.util.prefs.AbstractPreferences#getSpi(java.lang.String)
	 */
	@Override
    protected String getSpi(String key) {
		String result;
		result = (String) cache.get(key);
		String relativeOld = null;
		if (result == null) {
			try {
				initLDM();
				relativeOld = adjustRelative();
				Attributes attr = ldm.getSystemPreferenceKey(key);
				result = (String) attr.get("PreferenceValue").get(0);
				String lastmodified = (String) attr.get("PreferenceLastModified").get(0);
				cache.put(key, result);
				timestamp.put(key, new BigInteger(lastmodified));
			} catch (NamingException e) {
				//nichts
			} catch (BackingStoreException e) {
				// nichts
			} finally {
				ldm.setRelative(relativeOld);
			}
		}
		return result;
	}

	/**
	 * Schreibt Daten in die Preferences, werden erstmal nur in den Cache
	 * geschrieben, erst beim
	 * 
	 * @see flushSpi() werden Sie ins LDAP übertragen.
	 * @param key -
	 *            Der Key der Preference
	 * @param value -
	 *            Der Wert der Preference
	 * @see java.util.prefs.AbstractPreferences#putSpi(java.lang.String)
	 */
	@Override
    protected void putSpi(String key, String value) {
		BigInteger modifed = BigInteger.valueOf((new Date()).getTime());
		if (cache.containsKey(key)) {
			cache.remove(key);
			timestamp.remove(key);
			cache.put(key, value);
			timestamp.put(key, modifed);
		} else {
			cache.put(key, value);
			timestamp.put(key, modifed);
		}
		try {
			initLDM();
			createPath();
			String relativeOld = adjustRelative();
			try {
				ldm.updateOrCreateSystemPreferenceKey(key, value, modifed);
			} catch (LDAPException e1) {
				throw new BackingStoreException(e1);
			} finally {
				ldm.setRelative(relativeOld);
			}
		} catch (BackingStoreException e) {
			//Nichts tun, Garantie fürs schreiben gibts erst beim Sync oder
			// Flush
		}
	}

	/**
	 * Liefert das Kind mit dem angegebenen Namen zurück
	 * 
	 * @see java.util.prefs.AbstractPreferences#childSpi(java.lang.String)
	 */
	@Override
    protected AbstractPreferences childSpi(String arg0) {
		return new LDAPUserPreferences(this, arg0);
	}
}