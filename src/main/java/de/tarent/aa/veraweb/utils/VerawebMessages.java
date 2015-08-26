/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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
package de.tarent.aa.veraweb.utils;

import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.octopus.server.OctopusContext;

/**
 * Created by Jon Nuñez, tarent solutions GmbH on 23.06.15.
 */
public class VerawebMessages {
    final LanguageProviderHelper languageProviderHelper;
    final LanguageProvider languageProvider;
    private String messageGuestdoctypeCompanynameMaxLength;
    private String messageGuestdoctypeCountrynameMaxLength;
    private String messageGuestdoctypeFaxMaxLength;

    public VerawebMessages(OctopusContext octopusContext) {
        languageProviderHelper = new LanguageProviderHelper();
        languageProvider= languageProviderHelper.enableTranslation(octopusContext);
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

    public String getMessageColorMissingName() {
        return languageProvider.getProperty("MESSAGE_COLOR_MISSING_NAME");
    }

    public String getMessageEventcategoryWrong() {
        return languageProvider.getProperty("MESSAGE_EVENTCATEGORY_ASSIGNMENT_WRONG");
    }

    public String getMessageEventdoctypeWrong() {
        return languageProvider.getProperty("MESSAGE_EVENTDOCTYPE_ASSIGNMENT_WRONG");
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

    public String getMessageNoNameLastName() {
        return languageProvider.getProperty("MESSAGE_PERSON_NO_NAME_LAST_NAME");
    }

    public String getMessageNoName() {
        return languageProvider.getProperty("MESSAGE_PERSON_NO_NAME");
    }

    public String getMessageNoLastname() {
        return languageProvider.getProperty("MESSAGE_PERSON_NO_LASTNAME");
    }

    public String getMessagePersonCategoryNoConnectionPersonCategory() {
        return languageProvider.getProperty("MESSAGE_PERSON_CATEGORY_NO_CONNECTION_PERSON_CATEGORY");
    }

    public String getMessageDocTypeMaxConnectorReached() {
        return languageProvider.getProperty("MESSAGE_DOCTYPE_MAX_CONNECTOR_REACHED");
    }

    public String getMessageProxyNoRepresentative() {
        return languageProvider.getProperty("MESSAGE_PROXY_NO_REPRESENTATIVE");
    }

    public String getMessageProxyNoRole() {
        return languageProvider.getProperty("MESSAGE_PROXY_NO_ROLE");
    }

    public String getMessageProxyRepresentativeBeginBeforeEnd() {
        return languageProvider.getProperty("MESSAGE_PROXY_REPRESENTATIVE_BEGIN_BEFORE_END");
	}

    public String getMessageEventfunctionWrong() {
        return languageProvider.getProperty("MESSAGE_EVENTFUNCTION_ASSIGNMENT_WRONG");
    }

    public String getMessageFunctionMissingName() {
        return languageProvider.getProperty("MESSAGE_FUNCTION_MISSING_NAME");
    }

    public String getMessageGuestdoctypeCitynameMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_CITYNAME_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeCompanynameMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_COMPANYNAME_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeCountrynameMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_COUNTRYNAME_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeFaxMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_FAX_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeFirstnameMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_FIRSTNAME_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeLastnameMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_LASTNAME_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeFirstnamePartnerMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_FIRSTNAME_PARTNER_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeLastnamePartnerMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_LASTNAME_PARTNER_MAX_LENGTH");
    }

    public String getMessageGuestdoctypePhoneMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_PHONE_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeFunctionMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_FUNCTION_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeEmailMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_EMAIL_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeMobilePhoneMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_MOBILE_MAX_LENGTH");
    }

    public String getMessageGuestdoctypePOBoxMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_POBOX_MAX_LENGTH");
    }

    public String getMessageGuestdoctypePOBoxZipMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_POBOX_ZIP_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeSalutationMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_SALUTATION_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeSalutationPartnerMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_SALUTATION_PARTNER_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeStreetMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_STREET_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeSuffix1MaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_SUFFIX1_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeSuffix2MaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_SUFFIX2_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeTextjoinMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_TEXTJOIN_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeTitleMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_TITLE_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeTitlePartnerMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_TITLEPARTNER_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeUrlMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_URL_MAX_LENGTH");
    }

    public String getMessageGuestdoctypeZipMaxLength() {
        return languageProvider.getProperty("MESSAGE_GUEST_DOCTYPE_ZIP_MAX_LENGTH");
    }

    public String getMessageUserMissingRole() {
        return languageProvider.getProperty("MESSAGE_USER_MISSING_ROLE");
    }
}
