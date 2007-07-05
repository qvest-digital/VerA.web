/*
 * tarent-octopus bean extension,
 * an opensource webservice and webapplication framework (bean extension)
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
 * interest in the program 'tarent-octopus bean extension'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: Request.java,v 1.4 2007/06/11 13:24:36 christoph Exp $
 * 
 * Created on 08.02.2005
 */
package de.tarent.octopus.beans;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.tarent.octopus.server.OctopusContext;

/**
 * Konkrete {@link BeanFactory}, die Beans aus den Request-Parametern ausliest. 
 * 
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public abstract class Request extends BeanFactory {
    //
    // Konstruktor
    //
    /**
     * Der Konstruktor merkt sich den übergebenen {@link OctopusContext} und
     * initialisiert die {@link BeanFactory} mit dem übergebenen Bean-Package.
     * 
     * @param cntx {@link OctopusContext}, aus dessen Request-Parameter Beans
     *  ausgelesen werden.
     * @param beanPackage Package der zu nutzenden Bean-Klassen.
     */
	public Request(OctopusContext cntx, String beanPackage) {
        super(beanPackage);
		this.cntx = cntx;
	}

    //
    // öffentliche Methoden
    //
	/**
     * Diese Methode liefert eine Bean vom übergebenen Typ aus den passenden
     * Request-Parametern mit leerem Präfix.
     *  
     * @param beanname Klasse der zu holenden Bean
     * @return die ausgelesene Bean oder <code>null</code>
	 */
    public Bean getBean(String beanname) throws BeanException {
        return getBean(beanname, null);
    }

    /**
     * Diese Methode liefert eine Bean vom übergebenen Typ aus den passenden
     * Request-Parametern mit dem übergebenen Präfix.
     * 
     * @param beanname Klasse der zu holenden Bean
     * @param prefix Präfix der zu benutzenden Request-Parameter; falls
     *  <code>null</code>, so wird kein Präfix voran gestellt.
     * @return die ausgelesene Bean oder <code>null</code>
     * @throws BeanException
     */
    public Bean getBean(String beanname, String prefix) throws BeanException {
        setPrefixes(Collections.singletonList(prefix));
        Bean bean = createBean(beanname);
        return hasNext() ? fillBean(bean) : null;
    }

    /**
     * Diese Methode holt eine Liste mit Bean-Präfixen aus dem Request und
     * generiert daraus eine Liste mit Bean-Instanzen vom übergebenen Typ.
     * 
     * @param beanname Klasse der zu holenden Beans
     * @param listname Name des Request-Parameters, dessen Inhalt als Liste
     *  von Bean-Präfixrn interpretiert wird
     * @return eine Liste ausgelesener Beans.
     * @throws BeanException
     */
    public List getBeanList(String beanname, String listname) throws BeanException {
        setPrefixes((List)BeanFactory.transform(cntx.requestAsObject(listname), List.class));
        return fillBeanList(beanname);
    }

    //
    // Basisklassen BeanFactory
    //
    /**
     * Diese Methode setzt basierend auf dem Inhalt des Request-Parameters
     * <code>[{@link #prefix PRÄFIX}-]modified</code> das Bean-Feld
     * {@link Bean#isModified() Modified}. Sollten keine Informationen
     * verfügbar sein, bleibt das Feld unangetastet, es sollte also vor dem
     * Aufruf dieser Methode sinnvoll vorbelegt sein.
     * 
     * @param bean Bohne, deren {@link Bean#isModified() Modified}-Feld aktualisiert werden soll.
     * @throws BeanException
     * @see BeanFactory#checkModified(Bean)
     */
	protected void checkModified(Bean bean) throws BeanException{
		Object o = getField("modified");
		if (o != null && o instanceof String){
			bean.setModified(Boolean.valueOf((String)o).booleanValue());
		}
	}

    /**
     * Liefert ein Objekt, das in ein bestimmtes Feld der "aktuellen" Bean
     * gesetzt werden soll.
     * 
     * @param key Feld-Schlüssel
     * @return Feldinhalt oder null
     * @throws BeanException bei Datenzugriffsfehlern.
     * @see BeanFactory#getField(String)
     */
	public Object getField(String key) throws BeanException {
		return cntx.requestAsObject(prefix != null ? prefix + '-' + key : key);
	}

    /**
     * Wird verwendet, um bei Bean-Listen zur nächsten Bohne zu springen,
     * wird vor dem Einlesen einer Bean aufgerufen.
     * 
     * @return <code>true</code>, wenn weitere Beans vorhanden sind, ansonsten
     *  <code>false</code>.
     * @throws BeanException bei Datenzugriffsfehlern.
     * @see BeanFactory#hasNext()
     */
	public boolean hasNext() throws BeanException {
		if (it.hasNext()) {
			prefix = (String)it.next(); // FIXME: hasNext should not move the cursor
			return true;
		} else {
			return false;
		}
	}

    //
    // geschätzte Hilfsmethoden
    //
    /**
     * Diese Methode löscht das aktuelle Präfix und trägt als Präfix-Iterator
     * einen {@link Iterator} über die übergebene {@link Collection} ein.
     * 
     * @param prefixes Sammlung von Präfixen.
     */
	void setPrefixes(Collection prefixes) {
		prefix = null;
		it = prefixes.iterator();
	}
    
    //
    // geschätzte Membervariablen
    //
    /** Octopus-Kontext, in dessen Request-Parametern gearbeitet wird */
    final OctopusContext cntx;
    
    /** Präfix der aktuellen Bean */
    String prefix = null;
    
    /** Präfix-Iterator der aktuell noch abzuholenden Beans */
    Iterator it = null;
}
