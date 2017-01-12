/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.beans;

import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.beans.facade.GuestMemberFacade;
import de.tarent.aa.veraweb.utils.AddressHelper;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

import java.sql.Timestamp;

/**
 * Dieses Bean stellt einen Eintrag der Tabelle <em>veraweb.tguest</em> dar.
 *
 * @author Christoph
 * @author Mikel
 */
public class Guest extends AbstractHistoryBean implements EventConstants {
	/** ID */
	public Integer id;
	/** Erstellt am */
	public Timestamp created;
	/** Erstellt von */
	public String createdby;
	/** Geändert am */
	public Timestamp changed;
	/** Geändert von */
	public String changedby;
	/** ID der Person */
	public Integer person;
	/** ID der Veranstaltung */
	public Integer event;
	/** ID der Kategory */
	public Integer category;
	/** Einladungsart */
	public Integer invitationtype;
	/** Ist Gastgeber */
	public Integer ishost;
	/** Akkretierungsdatum */
	public Timestamp diplodate;
	/** Rang */
	public Integer rank;
	/** Reserve */
	public Boolean reserve;
	/** Arbeitsbereich */
	public String workarea_name;
    /** Delegation */
    public String delegation;

	public String keywords;

	// Hauptperson
	public Integer invitationstatus_a;
	public Integer tableno_a;
	public Integer seatno_a;
	public Integer orderno_a;
	public String notehost_a;
	public String noteorga_a;
	public String language_a;
	public String sex_a;
	public String nationality_a;
	public String domestic_a;
	public String color_a;
	public Integer fk_color_a;
	public String image_uuid;

	// Partner
	public Integer invitationstatus_b;
	public Integer tableno_b;
	public Integer seatno_b;
	public Integer orderno_b;
	public String notehost_b;
	public String noteorga_b;
	public String language_b;
	public String sex_b;
	public String nationality_b;
	public String domestic_b;
	public String color_b;
	public Integer fk_color_b;
	public String osiam_login;
	public String image_uuid_p;

	public String getImage_uuid() {
		return image_uuid;
	}

	public void setImage_uuid(String image_uuid) {
		this.image_uuid = image_uuid;
	}

	public String getLogin_required_uuid() {
		return login_required_uuid;
	}

	public void setLogin_required_uuid(String login_required_uuid) {
		this.login_required_uuid = login_required_uuid;
	}

	// UUID to allow registration to events without login
	public String login_required_uuid;



    @Override
    public void verify() throws BeanException {
		if (ishost == null) ishost = new Integer(0);
		AddressHelper.setColor(this);
	}

	/**
	 * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
	 * darf.<br>
	 * Test ist, ob der Benutzer Standard-Reader ist.
	 *
	 * @param octopusContext Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
	 * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
	 */
	@Override
    public void checkRead(OctopusContext octopusContext) throws BeanException {
		 checkGroup(octopusContext, PersonalConfigAA.GROUP_READ_STANDARD);
	}

	/**
	 * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
	 * werden darf.<br>
	 * Test ist, ob der Benutzer Writer ist.
	 *
	 * @param octopusContext Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
	 * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
	 */
	@Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
		 checkGroup(octopusContext, PersonalConfigAA.GROUP_WRITE);
	}

    /**
     * Diese Methode leert beschränkte Felder.<br>
     * Hier sind es die Bemerkungsfelder, wenn der Benutzer nicht in der Gruppe
     * {@link PersonalConfigAA#GROUP_READ_REMARKS} der hierzu freigeschalteten ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException bei Problemen mit der Bean
     * @see de.tarent.aa.veraweb.beans.AbstractBean#clearRestrictedFields(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void clearRestrictedFields(OctopusContext octopusContext) throws BeanException {
        PersonalConfig personalConfig = octopusContext != null ? octopusContext.personalConfig() : null;
        if (personalConfig == null || !personalConfig.isUserInGroup(PersonalConfigAA.GROUP_READ_REMARKS)) {
            notehost_a = null;
            notehost_b = null;
            noteorga_a = null;
            noteorga_b = null;
        }
        super.clearRestrictedFields(octopusContext);
    }

	/**
	 * Diese Methode gibt an, ob ein Partner für diesen Gast mit auf der Gästeliste steht.
	 * @return boolean true falls ein existierender Partner mit eingeladen wurde
	 */
	// added as per change request for version 1.2.0
	public boolean getIsPartnerInvited()
	{
		GuestMemberFacade g = this.getMain();
		int invitationType = g.getInvitationType();
		return ( ( invitationType == TYPE_MITPARTNER ) || ( invitationType == TYPE_NURPARTNER ) );
	}

	/**
	 * Diese Methode liefert eine Facade für die Hauptperson dieses Gastes zurück.
	 * @return GuestMemberFacade
	 */
	public GuestMemberFacade getMain() {
		return new Main();
	}

	/**
	 * Diese Methode liefert eine Facade für den Partner dieses Gastes zurück.
	 * @return GuestMemberFacade
	 */
	public GuestMemberFacade getPartner() {
		return new Partner();
	}

	/**
	 * Diese Klasse stellt eine Facade für die Hauptperson dieses Gastes dar.
	 */
	private class Main implements GuestMemberFacade {
        public Integer getInvitationType() {
			return invitationtype;
		}

		public Integer getInvitationStatus() {
			return invitationstatus_a;
		}

		public Integer getTableNo() {
			return tableno_a;
		}

		public Integer getSeatNo() {
			return seatno_a;
		}

		public Integer getOrderNo() {
			return orderno_a;
		}

		public String getNoteOrga() {
			return noteorga_a;
		}

		public String getNoteHost() {
			return notehost_a;
		}

		public String getLanguages() {
			return language_a;
		}

		public String getSex() {
			return sex_a;
		}

		public String getNationality() {
			return nationality_a;
		}

		public String getDomestic() {
			return domestic_a;
		}

		public String getColor() {
			return color_a;
		}

		public Integer getColorFK() {
			return fk_color_a;
		}

		public String getImageUuid() { return image_uuid; }

		public void setInvitationType(Integer value) {
			invitationtype = value;
		}

		public void setInvitationStatus(Integer value) {
			invitationstatus_a = value;
		}

		public void setTableNo(Integer value) {
			tableno_a = value;
		}

		public void setSeatNo(Integer value) {
			seatno_a = value;
		}

		public void setOrderNo(Integer value) {
			orderno_a = value;
		}

		public void setNoteOrga(String value) {
			noteorga_a = value;
		}

		public void setNoteHost(String value) {
			notehost_a = value;
		}

		public void setLanguages(String value) {
			language_a = value;
		}

		public void setSex(String value) {
			sex_a = value;
		}

		public void setNationality(String value) {
			nationality_a = value;
		}

		public void setDomestic(String value) {
			domestic_a = value;
		}

		public void setColor(String value) {
			color_a = value;
		}

		public void setColorFK(Integer value) {
			fk_color_a = value;
		}

		public void setImageUuid(String value) { image_uuid = value; }
	}

	/**
	 * Diese Klasse stellt eine Facade für den Partner dieses Gastes dar.
	 */
	private class Partner implements GuestMemberFacade {
        public Integer getInvitationType() {
			return invitationtype;
		}

		public Integer getInvitationStatus() {
			return invitationstatus_b;
		}

		public Integer getTableNo() {
			return tableno_b;
		}

		public Integer getSeatNo() {
			return seatno_b;
		}

		public Integer getOrderNo() {
			return orderno_b;
		}

		public String getNoteOrga() {
			return noteorga_b;
		}

		public String getNoteHost() {
			return notehost_b;
		}

		public String getLanguages() {
			return language_b;
		}

		public String getSex() {
			return sex_b;
		}

		public String getNationality() {
			return nationality_b;
		}

		public String getDomestic() {
			return domestic_b;
		}

		public String getColor() {
			return color_b;
		}

		public Integer getColorFK() {
			return fk_color_b;
		}

		public void setInvitationType(Integer value) {
			invitationtype = value;
		}

		public void setInvitationStatus(Integer value) {
			invitationstatus_b = value;
		}

		public void setTableNo(Integer value) {
			tableno_b = value;
		}

		public void setSeatNo(Integer value) {
			seatno_b = value;
		}

		public void setOrderNo(Integer value) {
			orderno_b = value;
		}

		public void setNoteOrga(String value) {
			noteorga_b = value;
		}

		public void setNoteHost(String value) {
			notehost_b = value;
		}

		public void setLanguages(String value) {
			language_b = value;
		}

		public void setSex(String value) {
			sex_b = value;
		}

		public void setNationality(String value) {
			nationality_b = value;
		}

		public void setDomestic(String value) {
			domestic_b = value;
		}

		public void setColor(String value) {
			color_b = value;
		}

		public void setColorFK(Integer value) {
			fk_color_b = value;
		}

		public String getImageUuid() { return image_uuid_p; }

		public void setImageUuid(String value) { image_uuid_p = value; }
	}
}
