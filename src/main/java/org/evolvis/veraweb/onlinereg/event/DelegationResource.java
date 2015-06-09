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
package org.evolvis.veraweb.onlinereg.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

import lombok.extern.java.Log;

import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Category;
import org.evolvis.veraweb.onlinereg.entities.Delegation;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContent;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContentFacade;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.entities.PersonCategory;
import org.evolvis.veraweb.onlinereg.entities.PersonDoctype;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
     *  1 - main person and partner are invited
     *  2 - only main person is invited
     *  3 - only partner is invited
     */
    private static final String INVITATION_TYPE = "2";

    /* RETURN TYPES */
    private static final TypeReference<Guest> GUEST = new TypeReference<Guest>() {};
    private static final TypeReference<Person> PERSON = new TypeReference<Person>() {};
    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {};
    private static final TypeReference<Integer> CATEGORY = new TypeReference<Integer>() {};
    private static final TypeReference<Category> CATEGORY_OBJECT = new TypeReference<Category>() {};
    private static final TypeReference<List<Person>> GUEST_LIST = new TypeReference<List<Person>>() {};
    private static final TypeReference<List<OptionalFieldValue>> FIELDS_LIST =
            new TypeReference<List<OptionalFieldValue>>() {};
    private static final TypeReference<List<String>> CATEGORY_LIST = new TypeReference<List<String>>() {};
    private static final TypeReference<List<String>> FUNCTION_LIST = new TypeReference<List<String>>() {};
    private static final TypeReference<List<OptionalFieldTypeContent>> TYPE_CONTENT_LIST =
    		new TypeReference<List<OptionalFieldTypeContent>>() {};

    /**
     * Jackson Object Mapper
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Configuration
     */
    private Config config;

    /**
     * Jersey client
     */
    private Client client;

	/**
	 * Default constructor
	 */
	public DelegationResource() {
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
	}

    /**
     * Get delegates of company/institution.
     *
     * @param uuid The delegation UUID
     *
     * @return List with delegates
     * @throws IOException TODO
     */
	@GET
    @Path("/{uuid}")
    public List<Person> getDelegates(@PathParam("uuid") String uuid) throws IOException {
		return readResource(path("person", uuid), GUEST_LIST);
    }

	/**
	 * Get category names of event
	 *
	 * @param uuid
	 *
	 * @return List with categories
	 * @throws IOException
	 */
	@GET
    @Path("/fields/list/category/{uuid}")
    public List<String> getCategories(@PathParam("uuid") String uuid) throws IOException {
        WebResource resource = client.resource(path("guest", uuid));
        Integer eventId = resource.get(Guest.class).getFk_event();

		return readResource(path("category", "fields", "list", eventId), CATEGORY_LIST);
    }

	/**
	 * Get function names of event
	 *
	 * @param uuid
	 *
	 * @return List with functions
	 * @throws IOException
	 */
	@GET
    @Path("/fields/list/function/{uuid}")
    public List<String> getFunctions(@PathParam("uuid") String uuid) throws IOException {
    	WebResource resource = client.resource(path("guest", uuid));
    	Integer eventId = resource.get(Guest.class).getFk_event();

		return readResource(path("function", "fields", "list", "function", eventId), FUNCTION_LIST);
    }

    /**
     * Get optional fields.
     *
     * @param uuid The delegation UUID
     * @param personId The person id
     *
     * @return List with optional fields for delegates
     * @throws IOException TODO
     */
	@GET
    @Path("/{uuid}/data")
    public List<OptionalFieldValue> getExtraDataFields(
    		@PathParam("uuid") String uuid,
    		@PathParam("personId") Integer personId) throws IOException {

		return getEventLabels(uuid);
    }

    /**
     * Get optional fields.
     *
     * @param uuid The delegation UUID
     * @param personId The person id
     *
     * @return List with optional fields for delegates
     * @throws IOException TODO
     */
	@GET
    @Path("/load/fields/{uuid}/{personId}")
    public List<OptionalFieldValue> getExtraDataFieldsPerson(
    		@PathParam("uuid") String uuid,
    		@PathParam("personId") Integer personId) throws IOException {

		return getEventLabelsPerson(uuid, personId);
    }

	/**
	 * Loading basic data of one delegate
	 *
	 * @param uuid Delegation UUID
	 * @param personId person ID
	 * @return @Link{Person.class}
	 *
	 * @throws IOException the exception
	 */
	@GET
	@Path("/load/{uuid}/{personId}")
    public Person loadBasicData(@PathParam("uuid") String uuid, @PathParam("personId") String personId)
            throws IOException {
		Person person = readResource(path("person", "list", personId), PERSON);

		return person;
	}

	/**
	 *
	 * @param uuid
	 * @param personId
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("/load/category/{uuid}/{personId}")
    public String loadDelegateCategory(@PathParam("uuid") String uuid, @PathParam("personId") String personId)
            throws IOException {
		// REST Method to get the category literal
//		Category category = readResource(path("category","catname", uuid, personId), CATEGORY_OBJECT);

		WebResource resource;

        resource = client.resource(path("category","catname", uuid, personId));
        return getDelegateCatnameStatus(resource);
	}

    /**
     * Register delegate for event.
     *
     * @param uuid The delegation UUID of the company.
     *
     * @param lastname The last name of the delegare
     * @param firstname The first name of the delegare
     * @param gender The gender of the delegare
     *
     * @return Status message
     *
     * @throws IOException TODO
     */
    @POST
    @Path("/{uuid}/register")
    public String registerDelegateForEvent(
            @PathParam("uuid") String uuid,
            @FormParam("lastname") String lastname,
            @FormParam("firstname") String firstname,
            @FormParam("gender") String gender,
            @FormParam("category") String category,
            @FormParam("functionDescription") String function,
            @FormParam("fields") String fields,
            @FormParam("personId") Integer personId) throws IOException {

        final Boolean delegationIsFound = checkForExistingDelegation(uuid);
        final String convertedGenderValue = getGenderByLabel(gender);

        if(delegationIsFound) {
        	if (personId == null) {
        		// Save new delegate
        		final String returnedValue = handleDelegationFound(uuid, lastname, firstname, convertedGenderValue,
        		        function, category, fields);
        		return StatusConverter.convertStatus(returnedValue);
        	}
        	else {
        		// Update delegate main data
        		final String returnedValue = updateDelegateMainData(personId, lastname, firstname, convertedGenderValue,
        		        function, fields, uuid);
        		// Update delegate category
        		updateDelegateCategory(personId, category, uuid);
        		return StatusConverter.convertStatus(returnedValue);
        	}
        } else {
            return StatusConverter.convertStatus("WRONG_DELEGATION");
        }
    }

    private String updateDelegateCategory(Integer personId, String category, String uuid) {

    	final WebResource resource = client.resource(path("category", "update", "delegate", "category"));

    	final Form postBody = new Form();
    	postBody.add("personId", personId);
    	postBody.add("category", category);
    	postBody.add("uuid", uuid);

    	resource.post(postBody);

    	return "OK";

    }
    private String getGenderByLabel(String gender) {
        if (gender.equals("GENERIC_GENDER_MALE")) {
            return "m";
        }

        return "f";
    }

    /**
     * Save optional fields.
     *
     * @param uuid The delegation UUID for the company.
     *
     * @param fields The optional fields
     * @param personId The delegate id
     *
     * @throws IOException TODO
     */
    @POST
    @Path("/{uuid}/fields/save")
    public void saveOptionalFields(
    		@PathParam("uuid") String uuid,
            @FormParam("fields") String fields,
            @FormParam("personId") Integer personId) throws IOException {
		if (fields != null && !"".equals(fields)) {
            handleSaveOptionalFields(uuid, fields, personId);
		}
    }

    /**
     * Remove delegate from guest list for event.
     *
     * @param uuid The delegation UUID
     * @param userid The user id
     *
     * @return TODO
     */
    @POST
    @Path("/{uuid}/remove/{userid}")
    public List<Guest> removeDelegateFromEvent(@PathParam("uuid") String uuid, @PathParam("userid") Long userid) {
        return null;
    }

    private void handleSaveOptionalFields(String uuid, String fields, Integer personId) throws IOException {
        final Map<String,Object> fieldMap = mapper.readValue(fields, new TypeReference<Map<String,Object>>(){});
        final Guest guest = getEventIdFromUuid(uuid, personId);

        for(Entry<String, Object> entry : fieldMap.entrySet()){
            final int fieldId = Integer.parseInt(entry.getKey());
            deleteExistingDelegationContent(guest.getPk(), fieldId);
        	try {
        		saveMultipleChoiceEntry(guest, entry, fieldId);
			} catch (ClassCastException e) {
				// TODO Implement better (without Exceptions)
				// Throwing ClassCastException means that we have a String value into the entry.
				// Otherwise, we cast the value to a List<String>
				final String fieldValue = (String) entry.getValue();
				saveOptionalField(guest.getPk(), fieldId, fieldValue);
			}
        }
    }

	private void saveMultipleChoiceEntry(final Guest guest,	Entry<String, Object> entry, final int fieldId) {
		final List<String> fieldContents = (List<String>) entry.getValue();
		for (Iterator<String> iterator = fieldContents.iterator(); iterator.hasNext();) {
			String value = (String) iterator.next();
			saveOptionalField(guest.getPk(), fieldId, value);
		}
	}

    private void deleteExistingDelegationContent(final Integer guestId, final Integer fieldId) throws IOException {
        final WebResource deleteFieldsResource = client.resource(config.getVerawebEndpoint() + "/rest/delegation/remove/fields");
        final Form postBody = new Form();
        postBody.add("guestId", guestId);
        postBody.add("fieldId", fieldId);
        deleteFieldsResource.post(postBody);
    }

    private String getDelegateCatnameStatus(WebResource resource) {
        String catname = null;

        try {
            catname = resource.get(String.class);
        } catch (UniformInterfaceException e) {
           int statusCode = e.getResponse().getStatus();
           if(statusCode == 204) {
                return null;
           }

           return null;
        }

        return StatusConverter.convertStatus(catname);
    }

	private List<OptionalFieldValue> getLabels(String uuid, Integer personId) throws IOException {
		try{
			final Guest guest = getEventIdFromUuid(uuid, personId);
			final List<OptionalFieldValue> fields =
                    readResource(path("delegation", "fields", "list", guest.getFk_event(), guest.getPk()), FIELDS_LIST);

            final List<OptionalFieldValue> filteredList = new ArrayList<OptionalFieldValue>();
            for (OptionalFieldValue field : fields) {
                if (field.getLabel() != null && !field.getLabel().equals("")) {
                    filteredList.add(field);
                }
            }
            return filteredList;
		}
		catch (UniformInterfaceException uie) {
			return null;
		}
	}

	private List<OptionalFieldValue> getEventLabels(String uuid) throws IOException {
		try{
			final Guest guest = getEventIdFromDelegationUuid(uuid);
			final List<OptionalFieldValue> fields =
                    readResource(path("delegation", "fields", "list", guest.getFk_event(), guest.getPk()), FIELDS_LIST);

			final List<OptionalFieldValue> fieldLabelsAndContent = new ArrayList<OptionalFieldValue>();
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
		List<OptionalFieldTypeContentFacade> typeContentsFacade = new ArrayList<OptionalFieldTypeContentFacade>();
		
		for(OptionalFieldTypeContent optionalFieldTypeContent : typeContents) {
			OptionalFieldTypeContentFacade optionalFieldFacade = new OptionalFieldTypeContentFacade(optionalFieldTypeContent);
			typeContentsFacade.add(optionalFieldFacade);
		}
		return typeContentsFacade;
	}

	private List<OptionalFieldValue> getEventLabelsPerson(String uuid, Integer personId) throws IOException {
		try{
			final Guest guest = getEventIdFromUuid(uuid, personId);
			final List<OptionalFieldValue> fields =
                    readResource(path("delegation", "fields", "list", guest.getFk_event(), guest.getPk()), FIELDS_LIST);

            final List<OptionalFieldValue> filteredList = new ArrayList<OptionalFieldValue>();
            for (OptionalFieldValue field : fields) {
                if (field.getLabel() != null && !field.getLabel().equals("")) {
                    filteredList.add(field);
                }
            }
            return filteredList;
		}
		catch (UniformInterfaceException uie) {
			return null;
		}
	}

    private Boolean checkForExistingDelegation(String uuid) throws IOException {
    	return readResource(path("guest","exist", uuid), BOOLEAN);
    }

    private String handleDelegationFound(String uuid, String nachname, String vorname, String gender, String function,
            String category, String fields)
            throws IOException {

        final Integer eventId = getEventId(uuid);
        if (eventId == null) {
            return "NO_EVENT_DATA";
        }

        final Person company = getCompanyFromUuid(uuid);

        String username = usernameGenerator();

        // Store in tperson
        final Integer personId = createPerson(company.getCompany_a_e1(), eventId, nachname, vorname, gender,username, function);

        Guest guest = addGuestToEvent(uuid, String.valueOf(eventId), personId, gender, nachname, vorname, username, category);

        if (guest!=null) {
        	updateOptionalFields(uuid, fields, guest.getFk_person());
        }

        return "OK";
    }

    private void createPersonCategory(final Integer personId, final  Integer categoryId) {
        final WebResource personCategoryResource = client.resource(config.getVerawebEndpoint() + "/rest/personcategory/add");
        final Form postBody = createPersonCategoryPostBodyContent(personId, categoryId);
        personCategoryResource.post(PersonCategory.class, postBody);
    }

    private void updateOptionalFields(String uuid, String fields, Integer personId)
			throws IOException {
		// Save optional fields
        if (fields != null && !"".equals(fields)) {
            handleSaveOptionalFields(uuid, fields, personId);
    	}
	}

    private String updateDelegateMainData(Integer personId, String lastname, String firstname, String gender,
            String function, String fields, String uuid) throws IOException {
        updatePerson(personId, firstname, lastname, gender, function);
        updateOptionalFields(uuid, fields, personId);

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
     * @param companyName
     *
     * @param lastname Last name
     * @param firstname First name
     * @param gender Gender of the person
     */
    private Integer createPerson(String companyName, Integer eventId, String lastname, String firstname, String gender,
            String username, String function) {
        final WebResource personResource = client.resource(config.getVerawebEndpoint() + "/rest/person/delegate");
        final Form postBody = createDelegatePostBodyContent(companyName, eventId, firstname, lastname, gender,
                username, function);
        final Person person = personResource.post(Person.class, postBody);

        createPersonDoctype(person);

        return person.getPk();
    }

    private Person updatePerson(Integer personId, String firstname, String lastname, String gender, String function) {
    	final WebResource personResource = client.resource(config.getVerawebEndpoint() + "/rest/person/delegate/update");
        final Form postBody = updatePersonPostBodyContent(personId, firstname, lastname, gender, function);
        final Person person = personResource.post(Person.class, postBody);

        return person;
    }

    private Integer getCategoryIdByValue(String categoryName) {
        Integer categoryId = null;

        try {
            categoryId = readResource(path("category", categoryName), CATEGORY);
        } catch (IOException e) {
            log.warning("Fehler beim holen der Kategorie-ID");
        }

        return categoryId;
    }

    private void createPersonDoctype(Person person) {
        final WebResource personDoctypeRsource = client.resource(config.getVerawebEndpoint() + "/rest/personDoctype");
        final Form postBody = createPersonDoctypePostBodyContent(person);

        personDoctypeRsource.post(PersonDoctype.class, postBody);
    }

    private Guest addGuestToEvent(String uuid, String eventId, Integer personId, String gender, String lastName,
            String firstName, String username, String category) {
        final Integer categoryId = persistPersonCategory(personId, category);
        final Guest guest = persistGuest(uuid, eventId, personId, gender, username, categoryId);

        createGuestDoctype(guest.getPk(), firstName, lastName);

        return guest;
    }

    private Integer persistPersonCategory(Integer personId, String category) {
        Integer categoryId = null;

        if (category != null && !category.equals("")) {
            categoryId = getCategoryIdByValue(category);
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

    private void createGuestDoctype(int guestId, String firstName, String lastName) {
		final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/guestDoctype");
        final Form postBody = createGuestDoctypePostBodyContent(guestId, firstName, lastName);

        resource.post(postBody);
	}

//    private void saveOptionalField(Integer guestId, Integer fieldId, String fieldContent, Integer fieldContentId) {
    private void saveOptionalField(Integer guestId, Integer fieldId, String fieldContent) {
        final WebResource resource = client.resource(path("delegation","field", "save"));
//        final Form postBody = updateOptionalFieldPostBodyContent(guestId, fieldId, fieldContent, fieldContentId);
        final Form postBody = updateOptionalFieldPostBodyContent(guestId, fieldId, fieldContent);

        resource.post(Delegation.class, postBody);
    }

    private Form createGuestDoctypePostBodyContent(int guestId, String firstName, String lastName) {
        final Form postBody = new Form();

        postBody.add("guestId", Integer.toString(guestId));
        postBody.add("firstName", firstName);
        postBody.add("lastName", lastName);

        return postBody;
    }

    private Form createDelegatePostBodyContent(String companyName, Integer eventId, String firstname, String lastname,
            String gender, String username, String function) {
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

    private Form createPersonDoctypePostBodyContent(Person person) {
        final Form postBody = new Form();

        postBody.add("personId", Integer.toString(person.getPk()));
        postBody.add("firstName", person.getFirstname_a_e1());
        postBody.add("lastName", person.getLastname_a_e1());

        return postBody;
    }

//    private Form updateOptionalFieldPostBodyContent(Integer guestId, Integer fieldId, String fieldContent, Integer fieldContentId) {
    private Form updateOptionalFieldPostBodyContent(Integer guestId, Integer fieldId, String fieldContent) {
        final Form postBody = new Form();

        postBody.add("guestId", guestId.toString());
        postBody.add("fieldId", fieldId.toString());
        postBody.add("fieldContent", fieldContent);
//        postBody.add("pk", fieldContentId);

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
        WebResource resource;
        try {
            resource = client.resource(path);
            final String json = resource.get(String.class);
            return mapper.readValue(json, type);
        } catch (ClientHandlerException che) {
            if (che.getCause() instanceof SocketTimeoutException) {
                //FIXME some times open, pooled connections time out and generate errors
                log.warning("Retrying request to " + path + " once because of SocketTimeoutException");
                resource = client.resource(path);
                final String json = resource.get(String.class);
                return mapper.readValue(json, type);
            } else {
                throw che;
            }
        } catch (UniformInterfaceException uie) {
            log.warning(uie.getResponse().getEntity(String.class));
            throw uie;
        }
    }

    /**
     * Constructs a path from VerA.web endpint, BASE_RESOURCE and given path fragmensts.
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
}
