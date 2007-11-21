/*
 * $Id: Categorie.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * Created on 23.02.2005
 */
package de.tarent.aa.veraweb.beans;

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.tcategorie, eine
 * Kategorie, dar.
 * 
 * @author christoph
 * @author mikel
 */
public class Categorie extends AbstractBean implements OrgUnitDependent {
    /** {@link #flags}-Eintrag: normale Kategorie */
	static public final int FLAG_DEFAULT = 0;
    /** {@link #flags}-Eintrag: Ereignis */
	static public final int FLAG_EVENT = 1;
    /** {@link #flags}-Eintrag: diplomatisches Korp */
	static public final int FLAG_DIPLO_CORPS = 99;

    /** pk serial NOT NULL: Prim�rschl�ssel */
	public Integer id;
    /** fk_orgunit int4 DEFAULT 0: Mandant */
	public Integer orgunit;
    /** fk_event int4 DEFAULT 0: Veranstaltung */
	public Integer event;
    /** catname varchar(200) NOT NULL: Name der Kategorie */
	public String name;
    /** flags int4 DEFAULT 0: Flags, vergleiche <code>FLAG_*</code> */
	public Integer flags;
    /** ank int4 DEFAULT 0: Kategorienrang */
	public Integer rank;

    /**
     * �berpr�ft das Bean auf innere Vollst�ndigkeit.
     * Hier wird getestet, ob die Kategorie einen nicht-leeren Namen hat.
     * 
     * @throws BeanException bei Unvollst�ndigkeit
     */
	public void verify() throws BeanException {
		if (name == null || name.length() == 0)
			addError("Sie m&uuml;ssen eine Bezeichnung eingeben.");
	}

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Test ist, ob der Benutzer Standard-Reader ist.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    public void checkRead(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_READ_STANDARD);
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
     * werden darf.<br>
     * Test ist, ob der Benutzer Writer ist. F�r das automatische Generieren von
     * Kategorien beim Import ist dies notwendig.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    public void checkWrite(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_WRITE);
    }
}
