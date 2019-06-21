package org.evolvis.veraweb.common;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
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

public class RestPaths {
    /**
     * Health-Resource
     */
    public static final String REST_HEALTH_AVAILABLE = "/available";

    /**
     * Export-Resource
     */
    public static final String REST_EXPORT = "/export";
    public static final String REST_EXPORT_GET_GUESTLIST = "/guestList/{eventId}";

    /**
     * FileUpload-Resource
     */
    public static final String REST_FILEUPLOAD = "fileupload";
    public static final String REST_FILEUPLOAD_SAVE = "/save";
    public static final String REST_FILEUPLOAD_DOWNLOAD = "/download/{imgUUID}";

    /**
     * Info-Resource
     */
    public static final String REST_INFO = "/info";

    /**
     * Mailing-Resource
     */
    public static final String REST_MAILING = "/mailing";

    /**
     * Mailtemplate-Resource
     */
    public static final String REST_MAILTEMPLATE = "/mailtemplate";

    /**
     * PdfTemplate-Resource
     */
    public static final String REST_PDFTEMPLATE = "/pdftemplate";
    public static final String REST_PDFTEMPLATE_EDIT = "/edit";
    public static final String REST_PDFTEMPLATE_DELETE = "/delete";
    public static final String REST_PDFTEMPLATE_GET_ALL = "/list";
    public static final String REST_PDFTEMPLATE_EXPORT = "/export";

    /**
     * SalutationResource
     */
    public static final String REST_SALUTATION = "/salutation";
    public static final String REST_SALUTATION_GET_ALL = "/getallsalutations";

    /**
     * SalutationAlternative-Resource
     */
    public static final String REST_SALUTATION_ALTERNATIVE = "/salutation/alternative";
    public static final String REST_SALUTATION_ALTERNATIVE_GET_ALL = "/list/{pdftemplateId}";
    public static final String REST_SALUTATION_ALTERNATIVE_UNUSED = "/unused/{pdftemplateId}";
    public static final String REST_SALUTATION_ALTERNATIVE_DELETE = "delete/{salutationId}";
    public static final String REST_SALUTATION_ALTERNATIVE_SAVE = "/save/{pdftemplateId}/";


    /**
     * Category-Resource
     */
    public static final String REST_CATEGORY = "/category";
    public static final String REST_CATEGORY_GET_ID = "/{catname}/{uuid}";
    public static final String REST_CATEGORY_IDENTIFY = "/identify";
    public static final String REST_CATEGORY_GET_NAME = "/catname/{uuid}/{personId}";
    public static final String REST_CATEGORY_GET_PERSON_DATA = "person/data";

    /**
     * Delegation-Resource
     */
    public static final String REST_DELEGATION = "/delegation";
    public static final String REST_DELEGATION_GET_OPTIONAL_FIELDS = "/fields/list/{eventId}/{guestId}";
    public static final String REST_DELEGATION_REMOVE_FIELDS = "/remove/fields";
    public static final String REST_DELEGATION_GET_LABEL = "/field/{eventId}";
    public static final String REST_DELEGATION_SAVE_FIELD = "/field/save";

    /**
     * Event-Resource
     */
    public static final String REST_EVENT = "/event";
    public static final String REST_EVENT_GET_USER_EVENTS = "/userevents/{username}";
    public static final String REST_EVENT_CHECK_REGISTERED = "/userevents/existing/{username}";
    public static final String REST_EVENT_GET_PERSON_ID = "/userid/{username}";
    public static final String REST_EVENT_GET_EVENT = "/{eventId}";
    public static final String REST_EVENT_EXIST = "/exist/{uuid}";
    public static final String REST_EVENT_GET_EVENT_ID = "/require/{uuid}";
    public static final String REST_EVENT_CHECK_OPEN = "/isopen/{eventId}";
    public static final String REST_EVENT_GUESLIST_STATUS = "/guestlist/status/{eventId}";
    public static final String REST_EVENT_RESERVELIST_STATUS = "/reservelist/status/{eventId}";
    public static final String REST_EVENT_GET_EVENT_BY_UUID = "/uuid/{uuid}";

    /**
     * ForgotLogin-Resource
     */
    public static final String REST_FORGOTLOGIN = "/forgotLogin";
    public static final String REST_FORGOTLOGIN_RESEND = "/resend/login";

    /**
     * ForgotPassword-Resource
     */
    public static final String REST_FORGOTPASSWORD = "/forgotPassword";
    public static final String REST_FORGOTPASSWORD_RESET_LINK = "/request/reset-password-link";

    /**
     * FreeVisitors-Resource
     */
    public static final String REST_FREEVISITORS = "/freevisitors";
    public static final String REST_FREEVISITORS_GET_GUEST = "/noLoginRequired/{noLoginRequiredUUID}";

    /**
     * Function-Resource
     */
    public static final String REST_FUNCTION = "/function";
    public static final String REST_FUNCTION_GET_FUNCTION = "/fields/list/function";

    /**
     * Guest-Resource
     */
    public static final String REST_GUEST = "/guest";
    public static final String REST_GUEST_GET_GUEST = "/{eventId}/{userId}";
    public static final String REST_GUEST_GET_GUEST_ID = "/concrete/{eventId}/{userId}";
    public static final String REST_GUEST_GET_IMAGE_UUID = "/image/{delegationUUID}/{personId}";
    public static final String REST_GUEST_GET_GUEST_BY_EVENT_USER_ID = "/{eventId}/{userId}";
    public static final String REST_GUEST_UPDATE_ENTITY = "/update/entity";
    public static final String REST_GUEST_UPDATE_GUEST = "/update/{eventId}/{userId}";
    public static final String REST_GUEST_UPDATE_NOLOGIN = "/update/nologin/{noLoginRequiredUUID}";
    public static final String REST_GUEST_BY_UUID = "/{uuid}";
    public static final String REST_GUEST_FIND_BY_DELEGATION = "/delegation/{uuid}/{userId}";
    public static final String REST_GUEST_FIND_BY_DELEGATION_UUID = "/delegation/{uuid}";
    public static final String REST_GUEST_EXIST_UUID = "/exist/{uuid}";
    public static final String REST_GUEST_REGISTERED = "/registered/delegation/{username}/{delegation}";
    public static final String REST_GUEST_REGISTERED_BY_USERNAME_EVENT = "/registered/{username}/{eventId}";
    public static final String REST_GUEST_REGISTERED_ACCEPT = "/registered/accept/{username}/{eventId}";
    public static final String REST_GUEST_REGISTERED_NOLOGIN = "/registered/nologin/{noLoginRequiredUUID}/{eventId}";
    public static final String REST_GUEST_ADD_TO_EVENT = "/{uuid}/register";
    public static final String REST_GUEST_REGISTER = "/register";
    public static final String REST_GUEST_ISRESERVE = "/isreserve/{eventId}/{username}";

    /**
     * LINKUUID-Resource
     */
    public static final String REST_LINK = "/links";
    public static final String REST_LINK_UUID = "/{uuid}";
    public static final String REST_LINK_DELETE = "/delete";
    public static final String REST_LINK_GET_BY_ID = "/byPersonId/{personId}";

    /**
     * MediaRepresentativeActivation-Resource
     */
    public static final String REST_MEDIA = "/press/activation";
    public static final String REST_MEDIA_CREATE = "/create";
    public static final String REST_MEDIA_EXISTS = "/exists/{encodedAddress}/{eventId}";
    public static final String REST_MEDIA_EXISTS_TOKEN = "/exists/{mediaRepresentativeActivationToken}";
    public static final String REST_MEDIA_UPDATE = "/update";

    /**
     * OptionalField-Resource
     */
    public static final String REST_OPTIONALFIELDS = "/optional/fields";
    public static final String REST_OPTIONALFIELDS_GET_ALL = "/list/{eventId}";

    /**
     * OptionalFieldTypeContent-Resource
     */
    public static final String REST_OPTIONALFIELDS_TYPECONTENT = "/typecontent";
    public static final String REST_OPTIONALFIELDS_TYPECONTENT_ID = "/{optionalFieldId}";

    /**
     * PersonCategory-Resource
     */
    public static final String REST_PERSONCATEGORY = "/personcategory";
    public static final String REST_PERSONCATEGORY_ADD = "/add";

    /**
     * Person-Resource
     */
    public static final String REST_PERSON = "/person";
    public static final String REST_PERSON_DELEGATE = "/delegate";
    public static final String REST_PERSON_DELEGATE_UPDATE = "/delegate/update";
    public static final String REST_PERSON_PRESS = "/press";
    public static final String REST_PERSON_USERCOREDATA_UPDATE = "/usercoredata/update/";
    public static final String REST_PERSON_USERINFO = "/userinfo/{username}";
    public static final String REST_PERSON_UUID = "/{uuid}";
    public static final String REST_PERSON_COMPANY = "/company/{uuid}";
    public static final String REST_PERSON_USERDATA = "/userdata/{username}";
    public static final String REST_PERSON_USERDATA_LITE = "/userdata/lite/{username}";
    public static final String REST_PERSON_UPDATE_ORGUNIT = "/update/orgunit";
    public static final String REST_PERSON_GET_USER = "/list/{personId}";

}
