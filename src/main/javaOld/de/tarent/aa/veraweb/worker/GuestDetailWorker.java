/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.fasterxml.jackson.core.type.TypeReference;

import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.utils.FileUploadUtils;
import de.tarent.aa.veraweb.utils.VworConstants;
import de.tarent.aa.veraweb.utils.VworUtils;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.statement.Delete;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.Guest;
import de.tarent.aa.veraweb.beans.GuestDoctype;
import de.tarent.aa.veraweb.beans.GuestSearch;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonCategorie;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.beans.facade.GuestMemberFacade;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.BeanChangeLogger;
import de.tarent.octopus.server.OctopusContext;


/**
 * Dieser Octopus-Worker dient der Anzeige und Bearbeitung von Details von Gästen.
 */
public class GuestDetailWorker extends GuestListWorker {


    /**
     * Logger dieser Klasse
     */
    public static final Logger logger = Logger.getLogger(GuestDetailWorker.class);

    // Octopus-Aktionen
    /**
     * Eingabe-Parameter der Octopus-Aktion {@link #showDetail(OctopusContext, Integer, Integer)}
     */
    public static final String INPUT_showDetail[] = {"id", "offset"};
    /**
     * Eingabe-Parameterzwang der Octopus-Aktion {@link #showDetail(OctopusContext, Integer, Integer)}
     */
    public static final boolean MANDATORY_showDetail[] = {false, false};


    /**
     * Diese Octopus-Aktion lädt Details zu einem Gast, der über ID oder über Position in der Ergebnisliste zu einer
     * aktuellen Gästesuche identifiziert wird.<br>
     * Wird der Gast oder die dazugehörende Person nicht gefunden, so wird der Status "notfound" gesetzt. Ansonsten werden
     * folgende Einträge im Octopus-Content gesetzt:
     * <ul>
     * <li> "guest" mit den Gastdetails
     * <li> "person" mit den Details der zugehörigen Person
     * <li> "main" mit der Facade zur Hauptperson
     * <li> "partner" mit der Facade zur Partnerperson
     * <li> "address" mit der Facade zur Adresse zum Standard-Freitextfeld
     * </ul>
     *
     * @param octopusContext Octopus-Kontext
     * @param guestid        ID des Gasts
     * @param offset         alternativ: Offset des Gasts in der aktuellen Gästesuche
     */
    @SuppressWarnings("unchecked")
    public void showDetail(OctopusContext octopusContext, Integer guestid, Integer offset) throws IOException, BeanException {
        final Database database = getDatabase(octopusContext);
        final Guest guest = getGuest(octopusContext, guestid, offset);
        final Person person = getPerson(octopusContext, guest);
        final Integer freitextfeld = ConfigWorker.getInteger(octopusContext, "freitextfeld");
        final Doctype doctype = (Doctype) database.getBean("Doctype", freitextfeld);
        final Integer addresstype = doctype != null ? doctype.addresstype : null;
        final Integer locale = doctype != null ? doctype.locale : null;
        final Categorie category = (Categorie) database.getBean("Categorie", guest.category);

        setGeneralContentForOctopusContext(octopusContext, guest, person, addresstype, locale, category);

        // Getting persons category
        getPersonCategories(person.id, octopusContext);

        // Bug 1591 Im Kopf der Gaesteliste sollen nicht die Stammdaten, sondern die
        // Daten der Gaesteliste angezeigt werden
        try {
            setGuestContentForOctopusContext(octopusContext, database, guest, freitextfeld);
        } catch (Exception e) {
            logger.warn("zum Gast: " + guestid + " und Doctyp: " + freitextfeld +
                    " kann Bean 'GuestDoctype' nicht geladen werden", e);
            octopusContext.setContent("showGuestListData", new Boolean(false));
        }

        if (guest.image_uuid != null) {
            octopusContext.setContent("guestImage", downloadGuestImage(guest.image_uuid));
        }
        if (guest.image_uuid_p != null) {
            octopusContext.setContent("guestPartnerImage", downloadGuestImage(guest.image_uuid_p));
        }

    }

    public static final String INPUT_reservationDupCheck[] = {};

    /**
     * This method returns list of error messages in the case where duplicate reservation for the guest ("Hauptperson")
     * or its partner (or both) were found in the database table tguest. Duplicate reservation applies if an seat
     * (with empty or 0 table) or table and seat is alreadyreserved by another guest or its partner.
     *
     * @param database
     * @param guest
     * @return Returns list of error messages in case duplicate reservation were found
     * @throws BeanException
     * @throws IOException
     */
    public List<String> reservationDupCheck(final Database database, final Guest guest, final OctopusContext octopusContext)
            throws BeanException, IOException {
        final LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
        final LanguageProvider languageProvider = languageProviderHelper.enableTranslation(octopusContext);
        final List<String> duplicateErrorList = new ArrayList<String>();
        return duplicateGuestAndPartnerList(database, guest, duplicateErrorList, languageProvider);
    }

    /**
     * Eingabe-Parameter der Octopus-Aktion {@link #saveDetail(OctopusContext)}
     */
    public static final String INPUT_saveDetail[] = {};

    /**
     * Diese Methode speichert Details zu einem Gast.<br>
     * Der Gast wird aus dem Octopus-Request gelesen. Je nach Einladungsart und -status werden dann Korrekturen an den
     * laufenden Nummern ausgeführt und die Bean wird geprüft ({@link BeanException} falls sie unvollständig ist oder
     * ungültige Einträge enthält). Schließlich wird sie gespeichert und passend wird im Octopus-Content unter
     * "countInsert" oder "countUpdate" 1 eingetragen.
     * Wenn der Nutzer dies im GUI bestaetigt hat, wird der Rang der Kategorie aus den Stammdaten der Person
     * uebernommen.
     *
     * @param octopusContext Octopus-Content
     * @throws BeanException bei ungültigen oder unvollständigen Einträgen
     */
    public void saveDetail(OctopusContext octopusContext) throws Exception {
        final Database database = getDatabase(octopusContext);
        final TransactionContext transactionContext = database.getTransactionContext();
        final Map<String, Object> allRequestParams = octopusContext.getRequestObject().getRequestParameters();

        try {
            final Guest guest = getGuestEntity(database, allRequestParams);
            updateGuestAndPartnerImage(allRequestParams, guest);

            //Check for duplicate reservation (guest and partner).
            final List<String> duplicateErrorList = reservationDupCheck(database, guest, octopusContext);
            //In case duplications were found show the errors and do not proceed with saving
            if (duplicateErrorList != null && !duplicateErrorList.isEmpty()) {
                octopusContext.setContent("duplicateErrorList", duplicateErrorList);
                return;
            }

            if (guest.reserve != null && guest.reserve.booleanValue()) {
                guest.orderno_a = null;
                guest.orderno_b = null;
            }

            updatePartnerData(allRequestParams, guest);
            updateMainPersonData(allRequestParams, guest);
            guest.verify();

            /*
             * modified to support change logging
             * cklein 2008-02-12
             */
            final BeanChangeLogger clogger = new BeanChangeLogger(database, transactionContext);

            if (guest.id == null) {
                insertGuestRemoveNotehost(octopusContext, database, transactionContext, guest, clogger);
            } else {
                updateGuestRemoveNotehost(octopusContext, database, transactionContext, guest, clogger);
            }

            updateDelegationFields(transactionContext, allRequestParams, guest.id);

            transactionContext.commit();
        } catch (BeanException e) {
            // cklein
            // 2008-02-13
            // prior to the change, there was a finally here
            // which caused the transaction to be always rolled back
            transactionContext.rollBack();
        }
    }

    /**
     * Eingabe-Parameter der Octopus-Aktion {@link #showTestGuest(OctopusContext)}
     */
    public static final String INPUT_showTestGuest[] = {};

    /**
     * Diese Octopus-Aktion liefert Details zu einem Test-Gast. Dieser wird unter
     * "guest" im Octopus-Content eingetragen.
     *
     * @param octopusContext Octopus-Kontext
     */
    public void showTestGuest(OctopusContext octopusContext) throws BeanException {
        Guest guest = new Guest();
        int random = new Random().nextInt(10000);
        String suffix = " [test-" + random + "]";
        showTestGuest(guest.getMain(), suffix + " (Hauptperson)", random);
        showTestGuest(guest.getPartner(), suffix + " (Partner)", random);
        guest.verify();
        octopusContext.setContent("guest", guest);
    }


    private String downloadGuestImage(String imageUUID) throws IOException {
        final TypeReference<String> stringType = new TypeReference<String>() {};
        final VworUtils vworUtils = new VworUtils();
        final String URI = vworUtils.path(VworConstants.FILEUPLOAD, VworConstants.DOWNLOAD, imageUUID);
        try {
            return vworUtils.readResource(URI, stringType);
        } catch (UniformInterfaceException e) {
            int statusCode = e.getResponse().getStatus();
            if (statusCode == 204) {
                return null;
            }
            return null;
        }
    }


    private void setGuestContentForOctopusContext(OctopusContext octopusContext, Database database, Guest guest, Integer freitextfeld) throws IOException, BeanException {
        if (freitextfeld == null) {
            //Kopfdaten der Gaesteliste: Anzeige der Stammdaten oder Kopien fuer Gaesteliste
            octopusContext.setContent("showGuestListData", new Boolean(false));
        } else {
            final GuestDoctype guestDoctype = getGuestDoctypeFromDatabase(database, guest, freitextfeld, guestDoctype);
            octopusContext.setContent("showGuestListData", new Boolean(guestDoctype != null));
            octopusContext.setContent("guestListData", guestDoctype);

        }
    }

    private GuestDoctype getGuestDoctypeFromDatabase(
            Database database,
            Guest guest,
            Integer freitextfeld,
            GuestDoctype guestDoctype) throws BeanException, IOException {

        final Select select = database.getSelect(guestDoctype);
        guestDoctype.doctype = freitextfeld;
        guestDoctype.guest = guest.id;
        select.where(database.getWhere(guestDoctype));
        return (GuestDoctype) database.getBean("GuestDoctype", select);
    }

    private void setGeneralContentForOctopusContext(
            OctopusContext octopusContext,
            Guest guest,
            Person person,
            Integer addresstype,
            Integer locale,
            Categorie category) {

        octopusContext.setContent("guest", guest);
        octopusContext.setContent("person", person);
        octopusContext.setContent("main", person.getMemberFacade(true, locale));
        octopusContext.setContent("partner", person.getMemberFacade(false, locale));
        octopusContext.setContent("address", person.getAddressFacade(addresstype, locale));
        octopusContext.setContent("tab", octopusContext.requestAsString("tab"));

        if (category != null && category.name != null && !category.name.equals("")) {
            octopusContext.setContent("guestCategory", category.name);
        }
    }

    private Person getPerson(OctopusContext octopusContext, Guest guest) throws IOException, BeanException {
        final Person person = (Person) getDatabase(octopusContext).getBean("Person", guest.person);
        if (person == null) {
            logger.error("showDetail konnte Person #" + guest.person + " unerwartet nicht laden.");
            octopusContext.setStatus("notfound");
        }

        return person;
    }

    private Guest getGuest(OctopusContext octopusContext, Integer guestid, Integer offset)
            throws BeanException, IOException {
        final GuestSearch search = getSearch(octopusContext);
        final Guest guest = getGuest(octopusContext, search.event, guestid, offset);
        if (guest == null) {
            logger.error("showDetail konnte Gast #" + guestid + " unerwartet nicht laden.");
            octopusContext.setStatus("notfound");
        }

        return guest;
    }

    /**
     * Getting the categories for one person/guest
     *
     * @param octopusContext OctopusContext
     * @throws BeanException
     * @throws IOException
     */
    private void getPersonCategories(Integer personId, OctopusContext octopusContext)
            throws BeanException, IOException {

        final Database database = getDatabase(octopusContext);
        final List<Categorie> categories = database.getBeanList("Categorie",
                database.getSelect("Categorie").
                        joinLeftOuter("tperson_categorie", "tcategorie.pk", "tperson_categorie.fk_categorie").
                        joinLeftOuter("tperson", "tperson_categorie.fk_person", "tperson.pk").
                        whereAndEq("tperson.pk", personId).
                        orderBy(null));

        octopusContext.setContent("personCategories", categories);
    }

    private void updateGuestAndPartnerImage(Map<String, Object> allRequestParams, Guest guest)
            throws IOException, BeanException {

        uploadGuestImage(allRequestParams, guest);
        uploadPartnerImage(allRequestParams, guest);
    }

    private void updateMainPersonData(Map<String, Object> allRequestParams, Guest guest) {
        final boolean isMainPerson = true;
        setInvitationStatus(allRequestParams, guest, isMainPerson);
        setTableNumber(allRequestParams, guest, isMainPerson);
        setSeatNumber(allRequestParams, guest, isMainPerson);
        setLanguage(allRequestParams, guest, isMainPerson);
        setPartnerNationality(allRequestParams, guest, isMainPerson);
        setNoteToOrgaTeam(allRequestParams, guest, isMainPerson);
        setNoteToHost(allRequestParams, guest, isMainPerson);
        setDomesticStatus(allRequestParams, guest, isMainPerson);
        setPartnerGender(allRequestParams, guest, isMainPerson);
    }

    private void updatePartnerData(Map<String, Object> allRequestParams, Guest guest) {
        final boolean isMainPerson = false;
        setInvitationStatus(allRequestParams, guest, isMainPerson);
        setTableNumber(allRequestParams, guest, isMainPerson);
        setSeatNumber(allRequestParams, guest, isMainPerson);
        setLanguage(allRequestParams, guest, isMainPerson);
        setPartnerNationality(allRequestParams, guest, isMainPerson);
        setNoteToOrgaTeam(allRequestParams, guest, isMainPerson);
        setNoteToHost(allRequestParams, guest, isMainPerson);
        setDomesticStatus(allRequestParams, guest, isMainPerson);
        setPartnerGender(allRequestParams, guest, isMainPerson);
    }

    private void setPartnerGender(Map<String, Object> allRequestParams, Guest guest, boolean isMainPerson) {
        if (isMainPerson) {
            if (allRequestParams.get("guest-sex_a") != null && allRequestParams.get("guest-sex_a") != guest.sex_a) {
                guest.sex_a = allRequestParams.get("guest-sex_a").toString();
            }
        } else {
            if (allRequestParams.get("guest-sex_b") != null && allRequestParams.get("guest-sex_b") != guest.sex_b) {
                guest.sex_b = allRequestParams.get("guest-sex_b").toString();
            }
        }
    }

    private void setDomesticStatus(Map<String, Object> allRequestParams, Guest guest, boolean isMainPerson) {
        if (isMainPerson) {
            if (allRequestParams.get("guest-domestic_a") != null && allRequestParams.get("guest-domestic_a") != guest.domestic_a) {
                guest.domestic_a = allRequestParams.get("guest-domestic_a").toString();
            }
        } else {
            if (allRequestParams.get("guest-domestic_b") != null && allRequestParams.get("guest-domestic_b") != guest.domestic_b) {
                guest.domestic_b = allRequestParams.get("guest-domestic_b").toString();
            }
        }
    }

    private void setNoteToHost(Map<String, Object> allRequestParams, Guest guest, boolean isMainPerson) {
        if (isMainPerson) {
            if (allRequestParams.get("guest-notehost_a") != null && allRequestParams.get("guest-notehost_a") != guest.notehost_a) {
                guest.notehost_a = allRequestParams.get("guest-notehost_a").toString();
            }
        } else {
            if (allRequestParams.get("guest-notehost_b") != null && allRequestParams.get("guest-notehost_b") != guest.notehost_b) {
                guest.notehost_b = allRequestParams.get("guest-notehost_b").toString();
            }
        }
    }

    private void setNoteToOrgaTeam(Map<String, Object> allRequestParams, Guest guest, Boolean isMainPerson) {
        if (isMainPerson) {
            if (allRequestParams.get("guest-noteorga_a") != null && allRequestParams.get("guest-noteorga_a") != guest.noteorga_a) {
                guest.noteorga_a = allRequestParams.get("guest-noteorga_a").toString();
            }
        } else {
            if (allRequestParams.get("guest-noteorga_b") != null && allRequestParams.get("guest-noteorga_b") != guest.noteorga_b) {
                guest.noteorga_b = allRequestParams.get("guest-noteorga_b").toString();
            }
        }
    }

    private void setPartnerNationality(Map<String, Object> allRequestParams, Guest guest, Boolean isMainPerson) {
        if (isMainPerson) {
            if (allRequestParams.get("guest-nationality_a") != null && allRequestParams.get("guest-nationality_a") != guest.nationality_a) {
                guest.nationality_a = allRequestParams.get("guest-nationality_a").toString();
            }
        } else {
            if (allRequestParams.get("guest-nationality_b") != null && allRequestParams.get("guest-nationality_b") != guest.nationality_b) {
                guest.nationality_b = allRequestParams.get("guest-nationality_b").toString();
            }
        }
    }

    private void setLanguage(Map<String, Object> allRequestParams, Guest guest, Boolean isMainPerson) {
        if (isMainPerson) {
            if (allRequestParams.get("guest-language_a") != null && allRequestParams.get("guest-language_a") != guest.language_a) {
                guest.language_a = allRequestParams.get("guest-language_a").toString();
            }
        } else {
            if (allRequestParams.get("guest-language_b") != null && allRequestParams.get("guest-language_b") != guest.language_b) {
                guest.language_b = allRequestParams.get("guest-language_b").toString();
            }
        }
    }

    private void setSeatNumber(Map<String, Object> allRequestParams, Guest guest, Boolean isMainPerson) {
        if (isMainPerson) {
            if (allRequestParams.get("guest-seatno_a") != null && Integer.parseInt(allRequestParams.get("guest-seatno_a").toString()) != guest.seatno_a) {
                guest.seatno_a = Integer.parseInt(allRequestParams.get("guest-seatno_a").toString());
            }
        } else {
            if (allRequestParams.get("guest-seatno_b") != null && Integer.parseInt(allRequestParams.get("guest-seatno_b").toString()) != guest.seatno_b) {
                guest.seatno_b = Integer.parseInt(allRequestParams.get("guest-seatno_b").toString());
            }
        }
    }

    private void setTableNumber(Map<String, Object> allRequestParams, Guest guest, Boolean isMainPerson) {
        if (isMainPerson) {
            if (allRequestParams.get("guest-tableno_a") != null && Integer.parseInt(allRequestParams.get("guest-tableno_a").toString()) != guest.tableno_a) {
                guest.tableno_a = Integer.parseInt(allRequestParams.get("guest-tableno_a").toString());
            }
        } else {
            if (allRequestParams.get("guest-tableno_b") != null && Integer.parseInt(allRequestParams.get("guest-tableno_b").toString()) != guest.tableno_b) {
                guest.tableno_b = Integer.parseInt(allRequestParams.get("guest-tableno_b").toString());
            }
        }
    }

    private void setInvitationStatus(Map<String, Object> allRequestParams, Guest guest, Boolean isMainPerson) {
        if (isMainPerson) {
            if (allRequestParams.get("guest-invitationstatus_a") != null && Integer.parseInt(allRequestParams.get("guest-invitationstatus_a").toString()) != guest.invitationstatus_a) {
                guest.invitationstatus_a = Integer.parseInt(allRequestParams.get("guest-invitationstatus_a").toString());
            }
        } else {
            if (allRequestParams.get("guest-invitationstatus_b") != null && Integer.parseInt(allRequestParams.get("guest-invitationstatus_b").toString()) != guest.invitationstatus_b) {
                guest.invitationstatus_b = Integer.parseInt(allRequestParams.get("guest-invitationstatus_b").toString());
            }
        }
    }

    private void uploadGuestImage(Map<String, Object> allRequestParams, Guest guest) throws IOException, BeanException {
        final String base64Image = getBase64Image(allRequestParams, "baseInfoImage");

        if (base64Image != null && !base64Image.equals("")) {
            final String extension = FileUploadUtils.getImageType(base64Image);
            final String imageData = FileUploadUtils.removeHeaderFromImage(base64Image);

            setGuestImageUUID(guest);
            sendImageToVwor(extension, imageData, guest.image_uuid);
        }
    }

    private void uploadPartnerImage(Map<String, Object> allRequestParams, Guest guest)
            throws IOException, BeanException {
        final String base64Image = getBase64Image(allRequestParams, "baseInfoImagePartner");

        if (base64Image != null && !"".equals(base64Image)) {
            final String extension = FileUploadUtils.getImageType(base64Image);
            final String imageData = FileUploadUtils.removeHeaderFromImage(base64Image);

            setPartnerImageUUID(guest);
            sendImageToVwor(extension, imageData, guest.image_uuid_p);
        }
    }

    private void setGuestImageUUID(Guest guest) {
        if (guest.image_uuid == null) {
            guest.image_uuid = FileUploadUtils.generateImageUUID();
        }
    }

    private void setPartnerImageUUID(Guest guest) {
        if (guest.image_uuid_p == null) {
            guest.image_uuid_p = FileUploadUtils.generateImageUUID();
        }
    }

    private void sendImageToVwor(String extension, String imageData, String imageUUID) throws IOException {
        final VworUtils vworUtils = new VworUtils();
        final Client client = Client.create();
        client.addFilter(vworUtils.getAuthorization());

        final WebResource resource = client.resource(vworUtils.path(VworConstants.FILEUPLOAD, VworConstants.SAVE));
        final Form postBody = new Form();

        postBody.add("imageUUID", imageUUID);
        postBody.add("imageStringData", imageData);
        postBody.add("extension", extension);

        resource.post(postBody);
    }

    private String getBase64Image(Map<String, Object> allRequestParams, String imageKey) {
        final String[] imageInfo = (String[]) allRequestParams.get(imageKey);
        if (imageInfo != null) {
            return imageInfo[0];
        }

        return null;
    }

    private Guest getGuestEntity(Database database, Map<String, Object> allRequestParams) throws BeanException, IOException {
        final Integer guestId = Integer.valueOf(allRequestParams.get("guest-id").toString());
        return (Guest) database.getBean("Guest", guestId);
    }

    private void updateGuestRemoveNotehost(
            OctopusContext octopusContext,
            Database database,
            TransactionContext transactionContext,
            Guest guest,
            BeanChangeLogger clogger) throws BeanException, IOException {

        final Guest guestOld = (Guest) database.getBean("Guest", guest.id, transactionContext);
        octopusContext.setContent("countUpdate", new Integer(1));
        final Update update = database.getUpdate(guest);
        if (!((PersonalConfigAA) octopusContext.personalConfig()).getGrants().mayReadRemarkFields()) {
            update.remove("notehost_a");
            update.remove("notehost_b");
            update.remove("noteorga_a");
            update.remove("noteorga_b");
        }
        transactionContext.execute(update);
        transactionContext.commit();

        // retrieve old instance of guest for update logging
        // we will quietly ignore non existing old entities and simply omit logging
        if (guestOld != null) {
            clogger.logUpdate(octopusContext.personalConfig().getLoginname(), guestOld, guest);
        }
    }

    private void insertGuestRemoveNotehost(
            OctopusContext octopusContext,
            Database database,
            TransactionContext transactionContext,
            Guest guest,
            BeanChangeLogger clogger) throws BeanException, IOException {
        octopusContext.setContent("countInsert", new Integer(1));
        database.getNextPk(guest, transactionContext);
        final Insert insert = database.getInsert(guest);
        insert.insert("pk", guest.id);
        if (!((PersonalConfigAA) octopusContext.personalConfig()).getGrants().mayReadRemarkFields()) {
            insert.remove("notehost_a");
            insert.remove("notehost_b");
            insert.remove("noteorga_a");
            insert.remove("noteorga_b");
        }
        transactionContext.execute(insert);
        transactionContext.commit();

        clogger.logInsert(octopusContext.personalConfig().getLoginname(), guest);
    }

    private void setGuestOrderno(Guest guest) {
        if (guest.invitationtype.intValue() == EventConstants.TYPE_MITPARTNER) {
            if (guest.invitationstatus_a != null && guest.invitationstatus_a.intValue() == 2) {
                guest.orderno_a = null;
            }
            if (guest.invitationstatus_b != null && guest.invitationstatus_b.intValue() == 2) {
                guest.orderno_b = null;
            }
        } else if (guest.invitationtype.intValue() == EventConstants.TYPE_OHNEPARTNER) {
            if (guest.invitationstatus_a != null && guest.invitationstatus_a.intValue() == 2)
                guest.orderno_a = null;
            guest.orderno_b = null;
        } else if (guest.invitationtype.intValue() == EventConstants.TYPE_NURPARTNER) {
            guest.orderno_a = null;
            if (guest.invitationstatus_b != null && guest.invitationstatus_b.intValue() == 2) {
                guest.orderno_b = null;
            }
        }
    }

    private void setGuestRankType(OctopusContext octopusContext, Database database, Guest guest) {
        try {
            // Der Rang der Kategorie wird aus den Stammdaten der Person gezogen,
            // wenn Nutzer dies will und wenn kein Rang vorbelegt ist.
            if (octopusContext.requestAsBoolean("fetchRankFromMasterData").booleanValue() && guest.rank == null) {
                if (guest.person != null && guest.category != null) {
                    final Select select = database.getSelect("PersonCategorie").where(
                            Where.and(Expr.equal("fk_person", guest.person),
                                    Expr.equal("fk_categorie", guest.category)));
                    select.orderBy(null); //im Bean.property steht ein Verweis auf andere Tabelle!

                    final PersonCategorie perCat = (PersonCategorie) database.getBean("PersonCategorie", select);
                    if (perCat != null) {
                        guest.rank = perCat.rank;
                    }
                }
            }
        } catch (Exception ex) {
            logger.warn("Kann den Rang der Gast-Kategorie nicht aus dem Personenstamm laden", ex);
        }
    }

    private void updateDelegationFields(TransactionContext transactionContext, Map<String, Object> allRequestParams,
                                        Integer guestId) throws BeanException {
        // TODO Implement update the right way!!!
        // delete contents by guestId id from toptional_field_delegation_content
        deleteExistingDelegationFieldContent(transactionContext, guestId);

        for (Map.Entry<String, Object> entry : allRequestParams.entrySet()) {
            if (entry.getKey().startsWith("optional-field-input-") ||
                    entry.getKey().startsWith("optional-field-dropdown-")) {
                handleSingleValueEntry(transactionContext, allRequestParams, guestId, entry);
            } else if (entry.getKey().startsWith("optional-field-multipledropdown-")) {
                try {
                    handleMultipleDropdown(transactionContext, guestId, entry);
                } catch (ClassCastException e) {
                    // TODO Implement better (without Exceptions)
                    // Throwing ClassCastException means that we have a String value into the entry.
                    // Otherwise, we cast the value to a List<String>
                    handleSingleValueEntry(transactionContext, allRequestParams, guestId, entry);
                }
            }
        }
    }

    private void handleMultipleDropdown(TransactionContext transactionContext, Integer guestId, Map.Entry<String,
            Object> entry) throws BeanException {
        final String[] keyParts = entry.getKey().split("-");
        final Integer fieldId = new Integer(keyParts[3]);
        final String[] fieldContents = (String[]) entry.getValue();

        for (String fieldContent : fieldContents) {
            insertNewDelegationContent(transactionContext, guestId, fieldId, fieldContent);
        }
    }

    /**
     * Handle single value entry. That means, we handle input fields, simple dropdown or multiple dropdown with only
     * one selected option.
     *
     * @param transactionContext The {@link TransactionContext}
     * @param allRequestParams   All request parameters submittet by the form
     * @param guestId            The guest id for the current guest
     * @param entry              Current entry
     * @throws BeanException TODO
     */
    private void handleSingleValueEntry(final TransactionContext transactionContext,
                                        final Map<String, Object> allRequestParams,
                                        final Integer guestId,
                                        final Map.Entry<String, Object> entry) throws BeanException {
        final String[] keyParts = entry.getKey().split("-");
        final Integer fieldId = new Integer(keyParts[3]);
        final String fieldContent = (String) allRequestParams.get(entry.getKey());

        insertNewDelegationContent(transactionContext, guestId, fieldId, fieldContent);
    }

    private void insertNewDelegationContent(TransactionContext transactionContext, Integer guestId, Integer fieldId,
                                            String fieldContent) throws BeanException {
        final Insert insert = SQL.Insert(transactionContext);
        insert.table("veraweb.toptional_fields_delegation_content");
        insert.
            insert("toptional_fields_delegation_content.fk_guest", guestId).
            insert("toptional_fields_delegation_content.fk_delegation_field", fieldId).
            insert("toptional_fields_delegation_content.value", fieldContent);

        transactionContext.execute(insert);
        transactionContext.commit();
    }

    private void deleteExistingDelegationFieldContent(final TransactionContext transactionContext,
                                                      final Integer guestId) throws BeanException {
        final Delete deleteStatement = SQL.Delete(transactionContext);
        deleteStatement.
            from("toptional_fields_delegation_content").
            whereAndEq("toptional_fields_delegation_content.fk_guest", guestId);
        transactionContext.execute(deleteStatement);
        transactionContext.commit();
    }

    private List<String> duplicateGuestAndPartnerList(
            Database database,
            Guest guest,
            List<String> duplicateErrorList,
            final LanguageProvider languageProvider) throws BeanException, IOException {

        //SCENARIO 1 - The seat (or table and seat) of the guest ("Hauptperson") is already reserved by another guest
        selectGuestAddDuplicateGuestList(database, guest, duplicateErrorList, languageProvider);

        //SCENARIO 2 - The seat (or table and seat) of the guest is already reserved by another partner
        selectPartnerAddDuplicateGuestList(database, guest, duplicateErrorList, languageProvider);


        if (guest.getIsPartnerInvited()) {
            //SCENARIO 3 - The seat (or table and seat) of the partner is already reserved by another guest
            selectGuestAddPartnerDuplicateList(database, guest, duplicateErrorList, languageProvider);

            //SCENARIO 4 - The seat (or table and seat) of the partner is already reserved by another partner
            if (guest.seatno_b != null && guest.seatno_b > 0) {
                selectPartnerAddPartnerDuplicateList(database, guest, duplicateErrorList, languageProvider);
            }
        }

        return duplicateErrorList;
    }

    private void selectPartnerAddPartnerDuplicateList(
            Database database,
            Guest guest,
            List<String> duplicateErrorList,
            final LanguageProvider languageProvider) throws BeanException, IOException {

        if (guest.tableno_b == null || guest.tableno_b.intValue() == 0) {
            final Select select = database.getSelect("Guest")
                    .whereOr(Expr.isNull("tableno_p"))
                    .whereOr(Expr.equal("tableno_p", 0))
                    .whereAnd(Expr.equal("seatno_p", guest.seatno_b))
                    .whereAnd(Expr.equal("fk_event", guest.event))
                    .whereAnd(Expr.notEqual("fk_person", guest.person));
            final Person duplicatePerson = checkForDuplicateSeatPerson(database, select);
            if (duplicatePerson != null) {
                duplicateErrorList.add(
                        getDuplicateSeatErrorMessage(duplicatePerson,
                                languageProvider.getProperty("GUEST_DETAIL_PARTNER"),
                                languageProvider.getProperty("GUEST_DETAIL_PARTNER_GENITIV"),
                                languageProvider)
                );
            }
        } else {
            final Select select = database.getSelect("Guest")
                    .whereAnd(Expr.equal("tableno_p", guest.tableno_b))
                    .whereAnd(Expr.equal("seatno_p", guest.seatno_b))
                    .whereAnd(Expr.equal("fk_event", guest.event))
                    .whereAnd(Expr.notEqual("fk_person", guest.person));
            final Person duplicatePerson = checkForDuplicateSeatPerson(database, select);
            if (duplicatePerson != null) {
                duplicateErrorList.add(getDuplicateSeatErrorMessage(duplicatePerson,
                        languageProvider.getProperty("GUEST_DETAIL_PARTNER"),
                        languageProvider.getProperty("GUEST_DETAIL_PARTNER_GENITIV"), languageProvider));
            }
        }
    }

    private void selectGuestAddPartnerDuplicateList(Database database, Guest guest, List<String> duplicateErrorList,
                                                    final LanguageProvider languageProvider)
            throws BeanException, IOException {
        if (guest.seatno_b != null && guest.seatno_b > 0) {
            if (guest.tableno_b == null || guest.tableno_b.intValue() == 0) {
                final Select select = database.getSelect("Guest")
                    .whereOr(Expr.isNull("tableno"))
                    .whereOr(Expr.equal("tableno", 0))
                    .whereAnd(Expr.equal("seatno", guest.seatno_b))
                    .whereAnd(Expr.equal("fk_event", guest.event))
                    .whereAnd(Expr.notEqual("fk_person", guest.person));
                final Person duplicatePerson = checkForDuplicateSeatPerson(database, select);
                if (duplicatePerson != null) {
                    duplicateErrorList.add(
                        getDuplicateSeatErrorMessage(duplicatePerson,
                            languageProvider.getProperty("GUEST_DETAIL_MAINPERSON"),
                            languageProvider.getProperty("GUEST_DETAIL_PARTNER_GENITIV"),
                            languageProvider)
                    );
                }
            } else {
                final Select select = database.getSelect("Guest")
                    .whereAnd(Expr.equal("tableno", guest.tableno_b))
                    .whereAnd(Expr.equal("seatno", guest.seatno_b))
                    .whereAnd(Expr.equal("fk_event", guest.event))
                    .whereAnd(Expr.notEqual("fk_person", guest.person)
                    );

                final Person duplicatePerson = checkForDuplicateSeatPerson(database, select);

                if (duplicatePerson != null) {
                    duplicateErrorList.add(
                        getDuplicateSeatErrorMessage(duplicatePerson,
                            languageProvider.getProperty("GUEST_DETAIL_MAINPERSON"),
                            languageProvider.getProperty("GUEST_DETAIL_PARTNER_GENITIV"),
                            languageProvider)
                    );
                }
            }
        }
    }

    private void selectPartnerAddDuplicateGuestList(Database database, Guest guest, List<String> duplicateErrorList,
                                                    final LanguageProvider languageProvider)
            throws BeanException, IOException {
        if (guest.seatno_a != null && guest.seatno_a > 0) {
            if (guest.tableno_a == null || guest.tableno_a.intValue() == 0) {
                final Select select = database.getSelect("Guest")
                    .whereOr(Expr.isNull("tableno_p"))
                    .whereOr(Expr.equal("tableno_p", 0))
                    .whereAnd(Expr.equal("seatno_p", guest.seatno_a))
                    .whereAnd(Expr.equal("fk_event", guest.event))
                    .whereAnd(Expr.notEqual("fk_person", guest.person));
                final Person duplicatePerson = checkForDuplicateSeatPerson(database, select);
                if (duplicatePerson != null) {
                    duplicateErrorList.add(getDuplicateSeatErrorMessage(duplicatePerson,
                        languageProvider.getProperty("GUEST_DETAIL_PARTNER"),
                        languageProvider.getProperty("GUEST_DETAIL_MAINPERSON"),
                        languageProvider));
                }
            } else {
                final Select select = database.getSelect("Guest")
                    .whereAnd(Expr.equal("tableno_p", guest.tableno_a))
                    .whereAnd(Expr.equal("seatno_p", guest.seatno_a))
                    .whereAnd(Expr.equal("fk_event", guest.event))
                    .whereAnd(Expr.notEqual("fk_person", guest.person));
                final Person duplicatePerson = checkForDuplicateSeatPerson(database, select);
                if (duplicatePerson != null) {
                    duplicateErrorList.add(getDuplicateSeatErrorMessage(duplicatePerson,
                            languageProvider.getProperty("GUEST_DETAIL_PARTNER"),
                            languageProvider.getProperty("GUEST_DETAIL_MAINPERSON"),
                            languageProvider));
                }
            }

        }
    }

    private void selectGuestAddDuplicateGuestList(
            Database database,
            Guest guest,
            List<String> duplicateErrorList,
            final LanguageProvider languageProvider) throws BeanException, IOException {

        if (guest.seatno_a != null && guest.seatno_a > 0) {
            if (guest.tableno_a == null || guest.tableno_a.intValue() == 0) {
                final Select select = database.getSelect("Guest")
                    .whereOr(Expr.isNull("tableno"))
                    .whereOr(Expr.equal("tableno", 0))
                    .whereAnd(Expr.equal("seatno", guest.seatno_a))
                    .whereAnd(Expr.equal("fk_event", guest.event))
                    .whereAnd(Expr.notEqual("fk_person", guest.person));
                final Person duplicatePerson = checkForDuplicateSeatPerson(database, select);
                if (duplicatePerson != null) {
                    duplicateErrorList.add(getDuplicateSeatErrorMessage(duplicatePerson,
                        languageProvider.getProperty("GUEST_DETAIL_MAINPERSON"),
                        languageProvider.getProperty("GUEST_DETAIL_MAINPERSON"),
                        languageProvider));
                }
            } else {
                final Select select = database.getSelect("Guest")
                    .whereAnd(Expr.equal("tableno", guest.tableno_a))
                    .whereAnd(Expr.equal("seatno", guest.seatno_a))
                    .whereAnd(Expr.equal("fk_event", guest.event))
                    .whereAnd(Expr.notEqual("fk_person", guest.person));
                final Person duplicatePerson = checkForDuplicateSeatPerson(database, select);
                if (duplicatePerson != null) {
                    duplicateErrorList.add(getDuplicateSeatErrorMessage(duplicatePerson,
                        languageProvider.getProperty("GUEST_DETAIL_MAINPERSON"),
                        languageProvider.getProperty("GUEST_DETAIL_MAINPERSON"),
                        languageProvider));
                }
            }
        }
    }

    /**
     * @param database
     * @param select
     * @return
     * @throws BeanException
     * @throws IOException
     */
    private Person checkForDuplicateSeatPerson(Database database, Select select) throws BeanException, IOException {
        Person duplicatePersonResult = null;
        final List resultList = database.getBeanList("Guest", select);
        if (resultList != null && !resultList.isEmpty()) {
            final Guest duplicateGuest = (Guest) resultList.get(0);
            if (duplicateGuest.person != null) {
                Person person = (Person) database.getBean("Person", duplicateGuest.person);
                if (person != null) {
                    duplicatePersonResult = person;
                }
            }
        }
        return duplicatePersonResult;
    }

    private String getDuplicateSeatErrorMessage(
            Person duplicatePerson,
            String changeSeatFor,
            String collidesWithSeatOf,
            final LanguageProvider languageProvider) {

        return languageProvider.getProperty("GUEST_DETAIL_DUP_SEAT_ERROR_ONE") +
                changeSeatFor +
                languageProvider.getProperty("GUEST_DETAIL_DUP_SEAT_ERROR_TWO") +
                duplicatePerson.firstname_a_e1 + " " + duplicatePerson.lastname_a_e1 +
                " (" + duplicatePerson.id + ") " +
                languageProvider.getProperty("GUEST_DETAIL_DUP_SEAT_ERROR_THREE") +
                languageProvider.getProperty("GUEST_DETAIL_DUP_SEAT_ERROR_FOUR") +
                collidesWithSeatOf + languageProvider.getProperty("GUEST_DETAIL_DUP_SEAT_ERROR_FIVE");
    }

    //
    // Hilfsmethoden
    //

    /**
     * Diese Methode erzeugt Test-Gast-Daten in einer Gast-Member-Facade.
     *
     * @param facade zu füllende Gast-Member-Facade
     * @param suffix Feldinhaltanhang für Textfelder
     * @param random Wert für laufende Nummer, Sitznummer und Tischnummer
     */
    protected void showTestGuest(GuestMemberFacade facade, String suffix, int random) {
        facade.setColor("Farbe" + suffix);
        facade.setColorFK(new Integer(new Random().nextInt(4)));
        facade.setInvitationStatus(new Integer(new Random().nextInt(3) + 1));
        facade.setInvitationType(new Integer(new Random().nextInt(3)));
        facade.setLanguages("Sprachen" + suffix);
        facade.setNationality("Nationalität" + suffix);
        facade.setNoteHost("Bemerkung (Gastgeber)" + suffix);
        facade.setNoteOrga("Bemerkung (Organisation)" + suffix);
        facade.setOrderNo(new Integer(random));
        facade.setSeatNo(new Integer(random));
        facade.setTableNo(new Integer(random));
    }

    /**
     * Diese Methode liefert eine {@link Guest}-Instanz passend zu den übergebenen
     * Kriterien. Falls eine Gast- und Veranstaltungs-ID vorliegt, wird der Gast
     * anhand dieser selektiert, sonst wird er über den übergebenen Offset in der
     * Ergebnisliste zur aktuellen Gastsuche ausgewählt.<br>
     *
     * @param octopusContext Octopus-Kontext
     * @param eventid        Veranstaltungs-ID für Selektion über ID
     * @param guestid        Gast-ID für Selektion über ID
     * @param offset         Gast-Offset für Selektion über Offset in Suchergebnisliste
     * @return der selektierte Gast oder <code>null</code>
     */
    protected Guest getGuest(
            OctopusContext octopusContext,
            Integer eventid,
            Integer guestid,
            Integer offset) throws BeanException, IOException {

        final Database database = getDatabase(octopusContext);

        // Offset aus der GuestListSearch lesen
        final GuestSearch search = (GuestSearch) octopusContext.contentAsObject("search");

        // Offset in den aktuellen Suchfilter-Bereich legen.
        if (offset == null || offset.intValue() < 1) {
            offset = search.offset;
        }

        Select select = database.getCount(BEANNAME);
        extendWhere(octopusContext, select);
        final Integer count = database.getCount(select);
        offset = getOffsetNumber(offset, count);

        // Offset und Count in die GuestListSearch schreiben
        search.offset = offset;
        search.count = count;

        // Select bauen das entweder ID oder das Offset verwenden um einen Gast zu laden
        select = database.getSelect(BEANNAME);
        selectColors(select);
        extendColumns(octopusContext, select);

        if (guestid != null && guestid.intValue() != 0) {
            if (logger.isEnabledFor(Priority.DEBUG))
                logger.log(Priority.DEBUG, "GuestDetail show for id " + guestid);
            select.where(Where.and(
                    Expr.equal("tguest.pk", guestid),
                    Expr.equal("tguest.fk_event", eventid)));

            Guest guest = (Guest) database.getBean(BEANNAME, select);
            if (guest != null) {
                getGuestListPositionById(octopusContext, guestid, database, search);

                return guest;
            }
        }

        if (logger.isEnabledFor(Priority.DEBUG)) {
            logger.log(Priority.DEBUG, "GuestDetail show for offset " + offset);
        }

        final WhereList list = new WhereList();
        search.addGuestListFilter(list);
        select.where(list);
        select.Limit(new Limit(new Integer(1), new Integer(offset.intValue() - 1)));
        return (Guest) database.getBean(BEANNAME, select);
    }

    private void getGuestListPositionById(OctopusContext octopusContext, Integer guestid, Database database,
                                          GuestSearch search)
            throws BeanException, IOException {
        // Gast wurde gefunden. Durch diese Suche (per ID) konnte sich aber ggf. die
        // Position innerhalb der Liste verändert werden, daher wird diese nun neu
        // Kalkuliert.
        final Select selectForPosition = database.getSelect("Guest");
        extendColumns(octopusContext, selectForPosition);
        extendWhere(octopusContext, selectForPosition);

        int newOffset = 1;
        for (Iterator it = database.getList(selectForPosition, database).iterator(); it.hasNext(); ) {
            Integer id = (Integer) ((Map) it.next()).get("id");
            if (id.intValue() == guestid.intValue()) {
                search.offset = new Integer(newOffset);
                break;
            }
            newOffset++;
        }
    }

    private void selectColors(Select select) {
        select.joinLeftOuter("veraweb.tcolor c1", "tguest.fk_color", "c1.pk");
        select.joinLeftOuter("veraweb.tcolor c2", "tguest.fk_color_p", "c2.pk");
        select.selectAs("c1.color", "color_a");
        select.selectAs("c2.color", "color_b");
    }

    private Integer getOffsetNumber(Integer offset, Integer count) {
        if (offset == null || offset.intValue() < 1) {
            offset = new Integer(1);
        } else if (offset.intValue() > count.intValue()) {
            offset = count;
        }
        return offset;
    }
}
