package org.evolvis.veraweb.common;

public class RestPaths {

    /**
     * Category-Resource
     * */
    public static final String REST_CATEGORY = "/category";
    public static final String REST_CATEGORY_GET_ID = "/{catname}/{uuid}";
    public static final String REST_CATEGORY_IDENTIFY = "/identify";
    public static final String REST_CATEGORY_GET_NAME = "/catname/{uuid}/{personId}";
    public static final String REST_CATEGORY_GET_PERSON_DATA = "person/data";

    /**
     * Delegation-Resource
     * */
    public static final String REST_DELEGATION = "/delegation";
    public static final String REST_DELEGATION_GET_OPTIONAL_FIELDS = "/fields/list/{eventId}/{guestId}";
    public static final String REST_DELEGATION_REMOVE_FIELDS = "/remove/fields";
    public static final String REST_DELEGATION_GET_LABEL = "/field/{eventId}";
    public static final String REST_DELEGATION_SAVE_FIELD = "/field/save";

    /**
     * Email-Resource
     * */
    public static final String REST_EMAIL = "/email";
    public static final String REST_EMAIL_CONFIRM = "/confirmation/send";

    /**
     * Event-Resource
     * */
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
     * Export-Resource
     * */
    public static final String REST_EXPORT = "/export";
    public static final String REST_EXPORT_GET_GUESTLIST = "/guestList/{eventId}";

    /**
     * FileUpload-Resource
     * */
    public static final String REST_FILEUPLOAD = "fileupload";
    public static final String REST_FILEUPLOAD_SAVE = "/save";
    public static final String REST_FILEUPLOAD_DOWNLOAD = "/download/{imgUUID}";

    /**
     * ForgotLogin-Resource
     * */
    public static final String REST_FORGOTLOGIN = "/forgotLogin";
    public static final String REST_FORGOTLOGIN_RESEND = "/resend/login";

    /**
     * ForgotPassword-Resource
     * */
    public static final String REST_FORGOTPASSWORD = "/forgotPassword";
    public static final String REST_FORGOTPASSWORD_RESET_LINK = "/request/reset-password-link";

    /**
     * FreeVisitors-Resource
     * */
    public static final String REST_FREEVISITORS = "/freevisitors";
    public static final String REST_FREEVISITORS_GET_GUEST = "/noLoginRequired/{noLoginRequiredUUID}";

    /**
     * Function-Resource
     * */
    public static final String REST_FUNCTION = "/function";
    public static final String REST_FUNCTION_GET_FUNCTION = "/fields/list/function";

    /**
     * Guest-Resource
     * */
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
     * Health-Resource
     * */
    public static final String REST_HEALTH_AVAILABLE = "/available";

    /**
     * Imprint-Resource
     * */
    public static final String REST_IMPRINT = "/imprint";
    public static final String REST_IMPRINT_LANGUAGE = "/{language}";

    /**
     * Info-Resource
     * */
    public static final String REST_INFO = "/info";

    /**
     * LINKUUID-Resource
     * */
    public static final String REST_LINK = "/links";
    public static final String REST_LINK_UUID = "/{uuid}";
    public static final String REST_LINK_DELETE = "/delete";
    public static final String REST_LINK_GET_BY_ID = "/byPersonId/{personId}";

    /**
     * Mailing-Resource
     * */
    public static final String REST_MAILING = "/mailing";

    /**
     * Mailtemplate-Resource
     */
    public static final String REST_MAILTEMPLATE = "/mailtemplate";

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
     * */
    public static final String REST_OPTIONALFIELDS = "/optional/fields";
    public static final String REST_OPTIONALFIELDS_GET_ALL = "/list/{eventId}";

    /**
     * OptionalFieldTypeContent-Resource
     * */
    public static final String REST_OPTIONALFIELDS_TYPECONTENT = "/typecontent";
    public static final String REST_OPTIONALFIELDS_TYPECONTENT_ID = "/{optionalFieldId}";

    /**
     * OsiamUserActivation-Resource
     */
    public static final String REST_OSIAMUSER_ACTIVATION = "/osiam/user";
    public static final String REST_OSIAMUSER_ACTIVATION_CREATE = "/create";
    public static final String REST_OSIAMUSER_ACTIVATE = "/activate";
    public static final String REST_OSIAMUSER_ACTIVATE_GET = "/get/activation/{activation_token}";
    public static final String REST_OSIAMUSER_ACTIVATE_GET_BY_NAME = "/get/activation/byusername/{username}";
    public static final String REST_OSIAMUSER_ACTIVATE_REFRESH = "/refresh/activation/data";

    /**
     * PdfTemplate-Resource
     * */
    public static final String REST_PDFTEMPLATE = "/pdftemplate";
    public static final String REST_PDFTEMPLATE_EDIT = "/edit";
    public static final String REST_PDFTEMPLATE_DELETE = "/delete";
    public static final String REST_PDFTEMPLATE_GET_ALL = "/list";
    public static final String REST_PDFTEMPLATE_EXPORT = "/export";

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
    public static final String REST_PERSON_GET_USER= "/list/{personId}";

    /**
     * SalutationAlternative-Resource
     */
    public static final String REST_SALUTATION_ALTERNATIVE = "/salutation/alternative";
    public static final String REST_SALUTATION_ALTERNATIVE_GET_ALL = "/list/{pdftemplateId}";
    public static final String REST_SALUTATION_ALTERNATIVE_UNUSED = "/unused/{pdftemplateId}";
    public static final String REST_SALUTATION_ALTERNATIVE_DELETE = "delete/{salutationId}";
    public static final String REST_SALUTATION_ALTERNATIVE_SAVE = "/save/{pdftemplateId}/";

    /**
     * SalutationResource
     */
    public static final String REST_SALUTATION = "/salutation";
    public static final String REST_SALUTATION_GET_ALL = "/getallsalutations";
}


