package org.evolvis.veraweb.onlinereg.event;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import lombok.Getter;
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Delegation;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContent;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContentFacade;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.entities.PersonCategory;
import org.evolvis.veraweb.onlinereg.user.LoginResource;
import org.evolvis.veraweb.onlinereg.utils.ResourceReader;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/delegation")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class DelegationResource {

    /**
     * Base path of all resources.
     */
    private static final String BASE_RESOURCE = "/rest";

    /**
     * Invitation type:
     * 1 - main person and partner are invited
     * 2 - only main person is invited
     * 3 - only partner is invited
     */
    private static final String INVITATION_TYPE = "2";

    /* RETURN TYPES */
    private static final TypeReference<Guest> GUEST = new TypeReference<Guest>() {
    };
    private static final TypeReference<Person> PERSON = new TypeReference<Person>() {
    };
    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {
    };
    private static final TypeReference<List<Person>> GUEST_LIST = new TypeReference<List<Person>>() {
    };
    private static final TypeReference<List<OptionalFieldValue>> FIELDS_LIST = new TypeReference<List<OptionalFieldValue>>() {
    };
    private static final TypeReference<List<String>> FUNCTION_LIST = new TypeReference<List<String>>() {
    };
    private static final TypeReference<List<OptionalFieldTypeContent>> TYPE_CONTENT_LIST =
            new TypeReference<List<OptionalFieldTypeContent>>() {
            };

    /**
     * Jackson Object Mapper
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Configuration
     */
    private final Config config;

    /**
     * Jersey client
     */
    private final Client client;
    private final ResourceReader resourceReader;

    /**
     * Servlet context
     */
    @javax.ws.rs.core.Context
    @Getter
    private HttpServletRequest request;

    private void checkAuthorization(String uuid) {
        try {
            if (!isAccessAuthorized(uuid)) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
        } catch (IOException e) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);

        }
    }

    private boolean isAccessAuthorized(String uuid) throws IOException {
        final Guest guest = getEventIdFromDelegationUuid(uuid);
        if (guest == null) {
            return false;
        }
        final String associatedAccount = guest.getOsiam_login();
        if (associatedAccount == null) {
            return false;
        }
        final String authenticatedAccount = (String) request.getAttribute(LoginResource.USERNAME);
        if (authenticatedAccount == null) {
            return false;
        }
        return authenticatedAccount.equals(associatedAccount);
    }

    /**
     * Constructor
     *
     * @param config Config
     * @param client Client
     */
    public DelegationResource(Config config, Client client) {
        this.config = config;
        this.client = client;
        this.resourceReader = new ResourceReader(client, mapper, config);
    }

    /**
     * Get delegates of company/institution.
     *
     * @param uuid The delegation UUID
     * @return List with delegates
     * @throws IOException TODO
     */
    @GET
    @Path("/{uuid}")
    public List<Person> getDelegates(@PathParam("uuid") String uuid) throws IOException {
        checkAuthorization(uuid);

        return readResource(path("person", uuid), GUEST_LIST);
    }

    /**
     * Get function names of event
     *
     * @param uuid TODO
     * @return List with functions
     * @throws IOException FIXME
     */
    @GET
    @Path("/fields/list/function/{uuid}")
    public List<String> getFunctions(@PathParam("uuid") String uuid) throws IOException {
        checkAuthorization(uuid);

        return readResource(path("function", "fields", "list", "function"), FUNCTION_LIST);
    }

    /**
     * Get optional fields.
     *
     * @param uuid The delegation UUID
     * @return List with optional fields for delegates
     * @throws IOException TODO
     */
    @GET
    @Path("/{uuid}/data")
    public List<OptionalFieldValue> getExtraDataFields(@PathParam("uuid") String uuid) throws IOException {
        checkAuthorization(uuid);

        return getEventLabels(uuid);
    }

    /**
     * Get optional fields.
     *
     * @param uuid     The delegation UUID
     * @param personId The person id
     * @return List with optional fields for delegates
     * @throws IOException TODO
     */
    @GET
    @Path("/load/fields/{uuid}/{personId}")
    public List<OptionalFieldValue> getExtraDataFieldsPerson(@PathParam("uuid") String uuid,
            @PathParam("personId") Integer personId)
            throws IOException {
        checkAuthorization(uuid);
        return getEventLabelsPerson(uuid, personId);
    }

    /**
     * Loading basic data of one delegate
     *
     * @param uuid     Delegation UUID
     * @param personId person ID
     * @return {@link Person}
     * @throws IOException the exception
     */
    @GET
    @Path("/load/{uuid}/{personId}")
    public Person loadBasicData(@PathParam("uuid") String uuid, @PathParam("personId") String personId) throws IOException {
        checkAuthorization(uuid);

        return readResource(path("person", "list", personId), PERSON);
    }

    @GET
    @Path("/load/category/{uuid}/{personId}")
    public String loadDelegateCategory(@PathParam("uuid") String uuid, @PathParam("personId") String personId)
            throws IOException {
        // REST Method to get the category literal
        // Category category = readResource(path("category","catname", uuid,
        // personId), CATEGORY_OBJECT);
        checkAuthorization(uuid);

        WebResource resource;

        resource = client.resource(path("category", "catname", uuid, personId));
        return getDelegateCatnameStatus(resource);
    }

    /**
     * Register delegate for event.
     *
     * @param uuid         The delegation UUID of the company.
     * @param lastname     The last name of the delegare
     * @param firstname    The first name of the delegare
     * @param gender       The gender of the delegare
     * @param category     Category
     * @param function     Function
     * @param personId     Person id
     * @param fields       FIXME
     * @param hasTempImage FIXME
     * @return Status message
     * @throws IOException TODO
     */
    @POST
    @Path("/{uuid}/register")
    public String registerDelegateForEvent(@PathParam("uuid") String uuid, @FormParam("lastname") String lastname,
            @FormParam("firstname") String firstname, @FormParam("gender") String gender, @FormParam("category") String category,
            @FormParam("functionDescription") String function, @FormParam("fields") String fields,
            @FormParam("personId") Integer personId,
            @FormParam("hasTempImage") Boolean hasTempImage) throws IOException {
        checkAuthorization(uuid);

        final Boolean delegationIsFound = checkForExistingDelegation(uuid);

        if (delegationIsFound) {
            if (personId == null) {
                // Save new delegate
                // TODO too much parameters...
                final String returnedValue = handleDelegationFound(uuid, lastname, firstname, gender, function, category, fields,
                        hasTempImage);
                return StatusConverter.convertStatus(returnedValue);
            } else {
                // Update delegate main data
                // TODO too much parameters...
                final String returnedValue = updateDelegateMainData(personId, lastname, firstname, gender, function, fields, uuid,
                        hasTempImage);
                return StatusConverter.convertStatus(returnedValue);
            }
        } else {
            return StatusConverter.convertStatus("WRONG_DELEGATION");
        }
    }

    /**
     * Save optional fields.
     *
     * @param uuid     The delegation UUID for the company.
     * @param fields   The optional fields
     * @param personId The delegate id
     * @throws IOException TODO
     */
    @POST
    @Path("/{uuid}/fields/save")
    public void saveOptionalFields(@PathParam("uuid") String uuid, @FormParam("fields") String fields,
            @FormParam("personId") Integer personId)
            throws IOException {
        checkAuthorization(uuid);

        if (fields != null && !"".equals(fields)) {
            handleSaveOptionalFields(uuid, fields, personId);
        }
    }

    private void handleSaveOptionalFields(String uuid, String fields, Integer personId) throws IOException {
        final Map<String, Object> fieldMap = mapper.readValue(fields, new TypeReference<Map<String, Object>>() {
        });
        final Guest guest = getEventIdFromUuid(uuid, personId);

        for (Entry<String, Object> entry : fieldMap.entrySet()) {
            final int fieldId = Integer.parseInt(entry.getKey());
            deleteExistingDelegationContent(guest.getPk(), fieldId);
            try {
                saveMultipleChoiceEntry(guest, entry, fieldId);
            } catch (ClassCastException e) {
                // TODO Implement better (without Exceptions)
                // Throwing ClassCastException means that we have a String value
                // into the entry.
                // Otherwise, we cast the value to a List<String>
                final String fieldValue = (String) entry.getValue();
                saveOptionalField(guest.getPk(), fieldId, fieldValue);
            }
        }
    }

    private void saveMultipleChoiceEntry(final Guest guest, Entry<String, Object> entry, final int fieldId) {
        final List<String> fieldContents = (List<String>) entry.getValue();
        for (String value : fieldContents) {
            saveOptionalField(guest.getPk(), fieldId, value);
        }
    }

    private void deleteExistingDelegationContent(final Integer guestId, final Integer fieldId) {
        final WebResource deleteFieldsResource = client.resource(config.getVerawebEndpoint() + "/rest/delegation/remove/fields");
        final Form postBody = new Form();
        postBody.add("guestId", guestId);
        postBody.add("fieldId", fieldId);
        deleteFieldsResource.post(postBody);
    }

    private String getDelegateCatnameStatus(WebResource resource) {
        String catname;
        try {
            catname = resource.get(String.class);
        } catch (UniformInterfaceException e) {
            int statusCode = e.getResponse().getStatus();
            if (statusCode == 204) {
                return null;
            }

            return null;
        }

        return StatusConverter.convertStatus(catname);
    }

    private List<OptionalFieldValue> getEventLabels(String uuid) throws IOException {
        try {
            final Guest guest = getEventIdFromDelegationUuid(uuid);
            final List<OptionalFieldValue> fields =
                    readResource(path("delegation", "fields", "list", guest.getFk_event(), guest.getPk()),
                            FIELDS_LIST);

            final List<OptionalFieldValue> fieldLabelsAndContent = new ArrayList<>();
            for (OptionalFieldValue field : fields) {
                if (field.getLabel() != null && !field.getLabel().equals("")) {
                    // TODO Refactor later
                    final List<OptionalFieldTypeContentFacade> typeContentsFacade = createOptionalFieldFacade(field.getPk());
                    field.setOptionalFieldTypeContentsFacade(typeContentsFacade);
                    fieldLabelsAndContent.add(field);
                }
            }
            return fieldLabelsAndContent;
        } catch (UniformInterfaceException uie) {
            return null;
        }
    }

    private List<OptionalFieldTypeContentFacade> createOptionalFieldFacade(Integer fieldId) throws IOException {
        // Bring the type Contents of the field
        List<OptionalFieldTypeContent> typeContents = readResource(path("typecontent", fieldId), TYPE_CONTENT_LIST);
        List<OptionalFieldTypeContentFacade> typeContentsFacade = new ArrayList<>();

        for (OptionalFieldTypeContent optionalFieldTypeContent : typeContents) {
            OptionalFieldTypeContentFacade optionalFieldFacade = new OptionalFieldTypeContentFacade(optionalFieldTypeContent);
            typeContentsFacade.add(optionalFieldFacade);
        }
        return typeContentsFacade;
    }

    private List<OptionalFieldValue> getEventLabelsPerson(String uuid, Integer personId) throws IOException {
        try {
            final Guest guest = getEventIdFromUuid(uuid, personId);
            final List<OptionalFieldValue> fields =
                    readResource(path("delegation", "fields", "list", guest.getFk_event(), guest.getPk()),
                            FIELDS_LIST);

            final List<OptionalFieldValue> filteredList = new ArrayList<>();
            for (OptionalFieldValue field : fields) {
                if (field.getLabel() != null && !field.getLabel().equals("")) {
                    filteredList.add(field);
                }
            }
            return filteredList;
        } catch (UniformInterfaceException uie) {
            return null;
        }
    }

    private Boolean checkForExistingDelegation(String uuid) throws IOException {
        return readResource(path("guest", "exist", uuid), BOOLEAN);
    }

    private String handleDelegationFound(String uuid, String nachname, String vorname, String gender, String function,
            String category,
            String fields,
            Boolean hasTempImage) throws IOException {

        final Integer eventId = getEventId(uuid);
        if (eventId == null) {
            return "NO_EVENT_DATA";
        }

        final Person company = getCompanyFromUuid(uuid);

        String username = usernameGenerator();

        // Store in tperson
        final Integer personId = createPerson(company.getCompany_a_e1(), eventId, nachname, vorname, gender, username, function);

        Guest guest = addGuestToEvent(uuid, String.valueOf(eventId), personId, gender, nachname, category);

        String imgUUID = null;
        if (hasTempImage) {
            imgUUID = updateImageUUID(guest);
        }

        if (guest != null) {
            updateOptionalFields(uuid, fields, guest.getFk_person());

        }
        if (imgUUID != null) {
            return imgUUID;
        }
        return "OK";
    }

    private String updateImageUUID(Guest guest) {
        String imgUUID = generateImageUUID();
        updateGuestEntity(guest.getPk(), imgUUID);
        return imgUUID;
    }

    private void updateGuestEntity(Integer guestId, String imgUUID) {
        final WebResource guestUpdateResource = client.resource(config.getVerawebEndpoint() + "/rest/guest/update/entity");
        final Form postBody = new Form();
        postBody.add("guestId", guestId);
        postBody.add("imgUUID", imgUUID);
        guestUpdateResource.post(postBody);
    }

    private void createPersonCategory(final Integer personId, final Integer categoryId) {
        final WebResource personCategoryResource = client.resource(config.getVerawebEndpoint() + "/rest/personcategory/add");
        final Form postBody = createPersonCategoryPostBodyContent(personId, categoryId);
        personCategoryResource.post(PersonCategory.class, postBody);
    }

    private void updateOptionalFields(String uuid, String fields, Integer personId) throws IOException {
        // Save optional fields
        if (fields != null && !"".equals(fields)) {
            handleSaveOptionalFields(uuid, fields, personId);
        }
    }

    // TODO We can use Person entity...
    private String updateDelegateMainData(Integer personId, String lastname, String firstname, String gender, String function,
            String fields,
            String delegationUUID, Boolean hasTempImage) throws IOException {
        updatePerson(personId, firstname, lastname, gender, function);
        updateOptionalFields(delegationUUID, fields, personId);

        final Guest guest = readResource(path("guest", "delegation", delegationUUID, personId), GUEST);

        if (hasTempImage && guest.getImage_uuid() == null) {
            return updateImageUUID(guest);
        } else if (guest.getImage_uuid() != null) {
            return guest.getImage_uuid();
        }

        return "OK";
    }

    private Integer getEventId(String uuid) throws IOException {
        final Guest guest = readResource(path("guest", uuid), GUEST);

        return guest.getFk_event();
    }

    private Guest getEventIdFromUuid(String uuid, Integer personId) throws IOException {
        return readResource(path("guest", "delegation", uuid, personId), GUEST);
    }

    private Guest getEventIdFromDelegationUuid(String uuid) throws IOException {
        return readResource(path("guest", "delegation", uuid), GUEST);
    }

    private Person getCompanyFromUuid(String uuid) throws IOException {
        return readResource(path("person", "company", uuid), PERSON);
    }

    /**
     * Includes a new person in the database - Table "tperson"
     *
     * @param companyName Company name
     * @param lastname    Last name
     * @param firstname   First name
     * @param gender      Gender of the person
     */
    private Integer createPerson(String companyName, Integer eventId, String lastname, String firstname, String gender,
            String username,
            String function) {
        final WebResource personResource = client.resource(config.getVerawebEndpoint() + "/rest/person/delegate");
        final Form postBody =
                createDelegatePostBodyContent(companyName, eventId, firstname, lastname, gender, username, function);
        final Person person = personResource.post(Person.class, postBody);

        return person.getPk();
    }

    private void updatePerson(Integer personId, String firstname, String lastname, String gender, String function) {
        final WebResource personResource = client.resource(config.getVerawebEndpoint() + "/rest/person/delegate/update");
        final Form postBody = updatePersonPostBodyContent(personId, firstname, lastname, gender, function);

        personResource.post(Person.class, postBody);
    }

    private Integer getCategoryIdByValue(String categoryName, Integer personId) {
        WebResource categoryResource =
                client.resource(config.getVerawebEndpoint() + "/rest/category/person/data").queryParam("catname", categoryName)
                        .queryParam("personId", personId.toString());

        return categoryResource.get(Integer.class);
    }

    private Guest addGuestToEvent(String uuid, String eventId, Integer personId, String gender, String username,
            String category) {
        final Integer categoryId = persistPersonCategory(personId, category);

        return persistGuest(uuid, eventId, personId, gender, username, categoryId);
    }

    private Integer persistPersonCategory(Integer personId, String category) {
        Integer categoryId = null;

        if (category != null && !category.equals("")) {
            categoryId = getCategoryIdByValue(category, personId);
            createPersonCategory(personId, categoryId);
        }

        return categoryId;
    }

    private Guest persistGuest(String uuid, String eventId, Integer personId, String gender, String username,
            Integer categoryId) {
        final WebResource resource = client.resource(path("guest", uuid, "register"));
        final Form postBody = createGuestPostBodyContent(eventId, personId, gender, username, categoryId);

        return resource.post(Guest.class, postBody);
    }

    private void saveOptionalField(Integer guestId, Integer fieldId, String fieldContent) {
        final WebResource resource = client.resource(path("delegation", "field", "save"));
        final Form postBody = updateOptionalFieldPostBodyContent(guestId, fieldId, fieldContent);

        resource.post(Delegation.class, postBody);
    }

    private Form createDelegatePostBodyContent(String companyName, Integer eventId, String firstname, String lastname,
            String gender,
            String username,
            String function) {
        Form postBody = new Form();

        postBody.add("company", companyName);
        postBody.add("eventId", String.valueOf(eventId));
        postBody.add("username", username);
        postBody.add("firstname", firstname);
        postBody.add("lastname", lastname);
        postBody.add("gender", gender);
        postBody.add("function", function);

        return postBody;
    }

    private Form createGuestPostBodyContent(String eventId, Integer personId, String gender, String username,
            Integer categoryId) {
        final Form postBody = new Form();

        postBody.add("userId", personId);
        postBody.add("eventId", eventId);
        postBody.add("invitationstatus", "0");
        postBody.add("invitationtype", INVITATION_TYPE);
        postBody.add("gender", gender);
        postBody.add("category", categoryId);
        postBody.add("username", username);

        return postBody;
    }

    // private Form updateOptionalFieldPostBodyContent(Integer guestId, Integer
    // fieldId, String fieldContent, Integer fieldContentId) {
    private Form updateOptionalFieldPostBodyContent(Integer guestId, Integer fieldId, String fieldContent) {
        final Form postBody = new Form();

        postBody.add("guestId", guestId.toString());
        postBody.add("fieldId", fieldId.toString());
        postBody.add("fieldContent", fieldContent);
        // postBody.add("pk", fieldContentId);

        return postBody;
    }

    private Form updatePersonPostBodyContent(Integer personId, String firstname, String lastname, String gender,
            String function) {
        final Form postBody = new Form();

        postBody.add("firstname", firstname);
        postBody.add("lastname", lastname);
        postBody.add("gender", gender);
        postBody.add("function", function);
        postBody.add("personId", personId);

        return postBody;
    }

    private Form createPersonCategoryPostBodyContent(Integer personId, Integer categoryId) {
        final Form postBody = new Form();

        postBody.add("personId", personId);
        postBody.add("categoryId", categoryId);

        return postBody;
    }

    /**
     * Reads the resource at given path and returns the entity.
     *
     * @param path path
     * @param type TypeReference of requested entity
     * @param <T>  Type of requested entity
     * @return requested resource
     * @throws IOException
     */
    private <T> T readResource(String path, TypeReference<T> type) throws IOException {
        return resourceReader.readStringResource(path, type);
    }

    /**
     * Constructs a path from VerA.web endpint, BASE_RESOURCE and given path
     * fragmensts.
     *
     * @param path path fragments
     * @return complete path as string
     */
    private String path(Object... path) {
        String r = config.getVerawebEndpoint() + BASE_RESOURCE;

        for (Object p : path) {
            r += "/" + p;
        }

        return r;
    }

    private String usernameGenerator() {
        final Date currentDate = new Date();

        return "deleg" + currentDate.getTime();
    }

    private String generateImageUUID() {
        UUID imageUUID = UUID.randomUUID();
        return imageUUID.toString();
    }

    @GET
    @Path("/image/{delegationUUID}/{personId}")
    public String getImageUUIDByUser(@PathParam("delegationUUID") String delegationUUID,
            @PathParam("personId") Integer personId) {
        checkAuthorization(delegationUUID);

        WebResource resource = client.resource(path("guest", "image", delegationUUID, personId));
        String imageUUID;

        try {
            imageUUID = resource.get(String.class);
        } catch (UniformInterfaceException e) {
            int statusCode = e.getResponse().getStatus();
            if (statusCode == 204) {
                return null;
            }

            return null;
        }

        return StatusConverter.convertStatus(imageUUID);
    }
}
