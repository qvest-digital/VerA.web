package de.tarent.aa.veraweb.utils;

/*-
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
