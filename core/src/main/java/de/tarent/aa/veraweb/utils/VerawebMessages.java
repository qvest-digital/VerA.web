package de.tarent.aa.veraweb.utils;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.octopus.server.OctopusContext;

/**
 * Created by Jon Nuñez, tarent solutions GmbH on 23.06.15.
 */
public class VerawebMessages {
    final LanguageProviderHelper languageProviderHelper;
    final LanguageProvider languageProvider;

    public VerawebMessages(OctopusContext octopusContext) {
        languageProviderHelper = new LanguageProviderHelper();
        languageProvider = languageProviderHelper.enableTranslation(octopusContext);
    }

    public String getMessageEndAndStartDate() {
        return languageProvider.getProperty("MESSAGE_END_AND_START_DATE");
    }

    public String getMessageEventMissingShortDescription() {
        return languageProvider.getProperty("MESSAGE_EVENT_MISSING_SHORT_DESCRIPTION");
    }

    public String getMessageEventWrongDateFormat() {
        return languageProvider.getProperty("MESSAGE_EVENT_WRING_DATE_FORMAT");
    }

    public String getMessageGenericMissingDescription() {
        return languageProvider.getProperty("MESSAGE_GENERIC_MISSING_DESCRIPTION");
    }

    public String getMessageLocationTitleEmpty() {
        return languageProvider.getProperty("MESSAGE_EVENT_LOCATION_TITLE_EMPTY");
    }

    public String getMessageLocationMaxRemarkReached() {
        return languageProvider.getProperty("MESSAGE_EVENT_LOCATION_MAX_REMARK_REACHED");
    }

    public String getMessageEMailDraftNameMissing() {
        return languageProvider.getProperty("MESSAGE_EMAIL_DRAFT_NAME_MISSING");
    }

    public String getMessageEMailDraftSubjectMissing() {
        return languageProvider.getProperty("MESSAGE_EMAIL_DRAFT_SUBJECT_MISSING");
    }

    public String getMessageEMailDraftTextMissing() {
        return languageProvider.getProperty("MESSAGE_EMAIL_DRAFT_TEXT_MISSING");
    }

    public String getMessageMailingListNameEmpty() {
        return languageProvider.getProperty("MESSAGE_EMAIL_LIST_NAME_EMPTY");
    }

    public String getMessageTaskTimeWithoutDate() {
        return languageProvider.getProperty("MESSAGE_TASK_TIME_WITHOUT_DATE");
    }

    public String getMessageTaskEndDateNotFuture() {
        return languageProvider.getProperty("MESSAGE_TASK_END_DATE_NOT_FUTURE");
    }

    public String getMessageTaskEndDateWithoutBeginDate() {
        return languageProvider.getProperty("MESSAGE_TASK_END_DATE_WITHOUT_BEGIN_DATE");
    }

    public String getMessageTaskBeginDateBeforeEndDate() {
        return languageProvider.getProperty("MESSAGE_TASK_BEGIN_DATE_BEFORE_END_DATE");
    }

    public String getMessageTaskNoShortname() {
        return languageProvider.getProperty("MESSAGE_TASK_NO_SHORTNAME");
    }

    public String getMessageTaskMaxRemarkRechaed() {
        return languageProvider.getProperty("MESSAGE_TASK_MAX_REMARK_REACHED");
    }

    public String getMessageEMailWithoutSender() {
        return languageProvider.getProperty("MESSAGE_TASK_EMAIL_WITHOUT_SENDER");
    }

    public String getMessageEMailWithoutReceiver() {
        return languageProvider.getProperty("MESSAGE_TASK_EMAIL_WITHOUT_RECEIVER");
    }

    public String getMessageEMailWithoutSubject() {
        return languageProvider.getProperty("MESSAGE_TASK_EMAIL_WITHOUT_SUBJECT");
    }

    public String getMessageOrgUnitNoName() {
        return languageProvider.getProperty("MESSAGE_ORG_UNIT_NO_NAME");
    }

    public String getMessagePersonMaxCompanyReached() {
        return languageProvider.getProperty("MESSAGE_PERSON_MAX_COMPANY_REACHED");
    }

    public String getMessagePersonMaxNameReached() {
        return languageProvider.getProperty("MESSAGE_PERSON_MAX_NAME_REACHED");
    }

    public String getMessagePersonMaxLastnameReached() {
        return languageProvider.getProperty("MESSAGE_PERSON_MAX_LASTNAME_REACHED");
    }

    public String getMessagePersonNoCompanyName() {
        return languageProvider.getProperty("MESSAGE_PERSON_NO_COMPANY_NAME");
    }

    public String getMessageBothNameFieldsAreEmpty() {
        return languageProvider.getProperty("MESSAGE_PERSON_NO_NAME_LAST_NAME");
    }

    public String getMessageNameFieldEmpty() {
        return languageProvider.getProperty("MESSAGE_PERSON_NO_NAME");
    }

    public String getMessageLastnameFieldEmptry() {
        return languageProvider.getProperty("MESSAGE_PERSON_NO_LASTNAME");
    }

    public String getMessagePersonCategoryNoConnectionPersonCategory() {
        return languageProvider.getProperty("MESSAGE_PERSON_CATEGORY_NO_CONNECTION_PERSON_CATEGORY");
    }

    public String getMessageProxyNoRepresentative() {
        return languageProvider.getProperty("MESSAGE_PROXY_NO_REPRESENTATIVE");
    }

    public String getMessageProxyNoRole() {
        return languageProvider.getProperty("MESSAGE_PROXY_NO_ROLE");
    }

    public String getMessageProxyBothRolleAndProxyMissing() {
        return languageProvider.getProperty("MESSAGE_PROXY_MISSING_BOTH");
    }


    public String getMessageProxyRepresentativeBeginBeforeEnd() {
        return languageProvider.getProperty("MESSAGE_PROXY_REPRESENTATIVE_BEGIN_BEFORE_END");
    }

    public String getMessageFunctionMissingName() {
        return languageProvider.getProperty("MESSAGE_FUNCTION_MISSING_NAME");
    }

    public String getMessageUserMissingRole() {
        return languageProvider.getProperty("MESSAGE_USER_MISSING_ROLE");
    }

    public String getMessageWorkAreaMissingName() {
        return languageProvider.getProperty("WORKAREA_ERROR_MISSING_NAME");
    }

    public String getMessageNoChanges() {
        return languageProvider.getProperty("GM_NO_CHANGES");
    }

    public String getMessageOrgUnitBusy() {
        return languageProvider.getProperty("MESSAGE_ORGUNIT_BUSY");
    }

    public String getPersonMessageField(String columnName) {
        return languageProvider.getProperty(columnName);
    }

}
