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
import de.tarent.octopus.beans.Database;
import org.apache.commons.lang.RandomStringUtils;
import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryBuilder;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class OsiamLoginCreator {
    /*
     * At least one digit
     * At leas one upper case letter
     * At least one special character
     */
    public static final String CONDITIONS = ".*(?=.*\\d)(?=.*[A-Z])(?=.*[-_$!#<>@&()+=}]).*";
    private static final String CHARS_FOR_PASSWORD_GENERATION =
            "abzdefghijklmnopqrstuvwxyzABZDEFGHIJKLMNOPQRSTUVWXYZ1234567890!$-_#<>@&()+=}|";

    private Database database;

    private QueryBuilder queryBuilder = new QueryBuilder();

    /**
     * Default constructor
     */
    public OsiamLoginCreator() {
    }

    /**
     * Custom constructor.
     *
     * @param database The {@link de.tarent.octopus.beans.Database}
     */
    public OsiamLoginCreator(Database database) {
        this.database = database;
    }

    /**
     * Generate username by given first and lastname.
     *
     * @param firstname The firstname of the person
     * @param lastname The lastname of the person
     * @param connector The {@link org.osiam.client.OsiamConnector}
     *
     * @return String username
     */
    public String generatePersonUsername(final String firstname,
                                         final String lastname,
                                         final OsiamConnector connector) {

        final String username = generateShortPersonUsername(firstname, lastname);
        return getResultList(username, connector);
    }

    /**
     * Generate username by given company name.
     *
     * @param companyname The companyName of the person
     * @param connector The {@link org.osiam.client.OsiamConnector}
     *
     * @return String username
     */
    public String generateCompanyUsername(final String companyname,
                                          final OsiamConnector connector) {

        final String username = generateShortCompanyUsername(companyname);
        return getResultList(username, connector);
    }

    /**
     * Generate random password for "Online-Anmeldung".
     *
     * @return The password
     */
    public String generatePassword() {

        String random = null;

        do {
            random = RandomStringUtils.random(8, CHARS_FOR_PASSWORD_GENERATION.toCharArray());
        } while (!random.matches(CONDITIONS));

        return random;
    }

    /**
     * Create osiam user.
     *
     * @param accessToken The {@link org.osiam.client.oauth.AccessToken}
     * @param username The username
     * @param password The password
     * @param connector The {@link org.osiam.client.OsiamConnector} client
     */
    public void createOsiamUser(AccessToken accessToken, String username, String password, OsiamConnector connector) {
        final User delegationUser = new User.Builder(username).setActive(true).setPassword(password).build();

        // create User in osiam
        connector.createUser(delegationUser, accessToken);
    }

    /**
     * Generates the shorttext of the username
     * Example: Karin Schneider -> kschne
     *
     * @param firstname String
     * @param lastname String
     *
     * @return String
     */
    private String generateShortPersonUsername(final String firstname, final String lastname) {
        final CharacterPropertiesReader characterPropertiesReader = new CharacterPropertiesReader();

        final String convertedFirstname = characterPropertiesReader.convertUmlauts(firstname).replaceAll("\\s+", "");
        final String convertedLastname = characterPropertiesReader.convertUmlauts(lastname).replaceAll("\\s+", "");

        String username = null;
        if (convertedLastname.length() >= 5) {
            username = handleLasnameLongerThenFiveCharacters(convertedFirstname, convertedLastname);
        } else {
            username = handleLastnameNotLongerThanFiveCharacters(convertedFirstname, convertedLastname);
        }

        return username;
    }

    private String handleLastnameNotLongerThanFiveCharacters(String convertedFirstname, String convertedLastname) {
        return convertedFirstname.substring(0, 1).toLowerCase() + convertedLastname.toLowerCase();
    }

    private String handleLasnameLongerThenFiveCharacters(String convertedFirstname, String convertedLastname) {
        return handleLastnameNotLongerThanFiveCharacters(convertedFirstname, convertedLastname.substring(0, 5));
    }

    /**
     * Generates the shorttext of the companyname
     * Example: tarent solutions GmbH -> tarentsolu
     *
     * @param companyname String
     *
     * @return String
     */
    private String generateShortCompanyUsername(final String companyname) {
        final CharacterPropertiesReader characterPropertiesReader = new CharacterPropertiesReader();

        final String convertedCompanyname = characterPropertiesReader.convertUmlauts(companyname).replaceAll("\\s+", "");

        String username = null;
        if (convertedCompanyname.length() >= 10) {
            username = handleCompanynameLongerThenTenCharacters(convertedCompanyname);
        } else {
            username = handleCompanynameNotLongerThanTenCharacters(convertedCompanyname);
        }

        return username;
    }

    private String handleCompanynameNotLongerThanTenCharacters(String convertedCompanyname) {
        return convertedCompanyname.toLowerCase();
    }

    private String handleCompanynameLongerThenTenCharacters(String convertedCompanyname) {
        return handleCompanynameNotLongerThanTenCharacters(convertedCompanyname.substring(0, 10));
    }

    private String getResultList(final String username, final OsiamConnector connector) {
        final AccessToken accessToken = connector.retrieveAccessToken(Scope.ALL);
        SCIMSearchResult<User> result = searchOsiamUser(connector, accessToken, username);
        Integer usernameSuffix = 1;
        String finalUsername = "";

        while (result.getTotalResults() > 0) {
            finalUsername = username.toLowerCase() + usernameSuffix;
            result = searchOsiamUser(connector, accessToken, finalUsername);
            usernameSuffix++;
        }
        if (finalUsername.equals("")) {
            return username;
        }
        return finalUsername;
    }

    private SCIMSearchResult<User> searchOsiamUser(OsiamConnector connector, AccessToken accessToken, String finalUsername) {
        final Query query = buildQuery(finalUsername);
        return connector.searchUsers(query, accessToken);
    }

    private Query buildQuery(String username) {
        return queryBuilder.filter("userName eq \"" + username + "\"").attributes("id").build();
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }
}
