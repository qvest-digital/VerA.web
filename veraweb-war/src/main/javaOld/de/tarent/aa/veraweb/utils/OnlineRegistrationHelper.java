package de.tarent.aa.veraweb.utils;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.ExecutionContext;
import de.tarent.octopus.server.OctopusContext;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.resources.scim.User;

import java.io.IOException;

/**
 * Helper to play with the Online-Application Configuration.
 *
 * @return true if activated, otherwise false
 *
 * @author jnunez
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class OnlineRegistrationHelper {

	/*
	 * At least one digit
	 * At leas one upper case letter
	 * At least one special character
	 */
	public static final String CONDITIONS = ".*(?=.*\\d)(?=.*[A-Z])(?=.*[-_$!#<>@&()+=}]).*";
	private static final Logger LOGGER = Logger.getLogger(OnlineRegistrationHelper.class.getCanonicalName());

	private static final String VWOR_PARAM = "online-registration.activated";
	private static final String VWOR_VALUE_TRUE = "true";
	private static final String CHARS_FOR_PASSWORD_GENERATION =
			"abzdefghijklmnopqrstuvwxyzABZDEFGHIJKLMNOPQRSTUVWXYZ1234567890!$-_#<>@&()+=}|";


	/**
	 * Check for enabled online registration module.
	 *
	 * @param cntx The context
	 * @return true if online registration is enabled, otherwise false
	 */
	public static Boolean isOnlineregActive(OctopusContext cntx) {

		final String active = cntx.moduleConfig().getParam(VWOR_PARAM);

		if (active != null && VWOR_VALUE_TRUE.equals(active)) {
			return true;
		}
		return false;
	}

	/**
	 * Generate username by given first and lastname.
	 *
	 * @param firstname The firstname of the person
	 * @param lastname The lastname of the person
	 * @param context The {@link ExecutionContext}
	 *
	 * @return String username
	 */
	public String generateUsername(final String firstname, final String lastname, final ExecutionContext context) {
			final StringBuilder stringBuilder = new StringBuilder();
			final String username = generateShortUsername(firstname, lastname);

			final Integer number = getSuffixIfUsernameAlreadyExists(context, username);

			if (number != null) {
				return stringBuilder.append(number).toString();
			}
			return stringBuilder.toString();
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
	 * @param accessToken The {@link AccessToken}
	 * @param username The username
	 * @param password The password
	 * @param connector The {@link OsiamConnector} client
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
	private String generateShortUsername(final String firstname, final String lastname) {
		final CharacterPropertiesReader characterPropertiesReader = new CharacterPropertiesReader();

		final String convertedFirstname = characterPropertiesReader.convertUmlauts(firstname);
		final String convertedLastname = characterPropertiesReader.convertUmlauts(lastname);

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
	 * Checking if the username's shorttext has duplicates
	 * 
	 * @param context ExecutionContext
	 * @param username String username
	 */
	private Integer getSuffixIfUsernameAlreadyExists(final ExecutionContext context, String username) {
		// check if a duplicate entry was found
		if (context != null) {
			final ResultList list = getResultList(context, username);

			if (!list.isEmpty()) {
				final Person person = (Person) list.get(0);
				final String auxUsername= person.username;
				
				final String[] res = auxUsername.split(username);
				
				if (res.length > 1 && isNumeric(res[1])) {
					Integer usernameNumber = Integer.valueOf(res[1]);
					return usernameNumber++;
				}
			}
		}
		return null;
	}

	private ResultList getResultList(ExecutionContext context, String username) {
		final Database database = context.getDatabase();

		final Select selectStatement = getSelectStatement(database, username);

		return getResults(context, database, selectStatement);
	}

	private ResultList getResults(ExecutionContext context, Database database, Select selectStatement) {
		ResultList list = null;
		try {
            list = database.getList(selectStatement, context);
        } catch (BeanException e) {
			LOGGER.error("Fehler bei der Abfrage", e);
        }
		return list;
	}

	private Select getSelectStatement(Database database, String username) {
		final Clause whereClause = Expr.like("username", username + "%");

		Select selectStatement = null;
		try {
            selectStatement = database.getSelect("Person").where(whereClause);
        } catch (BeanException beanException) {
			LOGGER.error("Fehler bei der Abfrage", beanException);
        } catch (IOException ioException) {
			LOGGER.error("IOFehler", ioException);
        }
		selectStatement.orderBy(Order.desc("pk"));
		selectStatement.Limit(new Limit(new Integer(1), new Integer(0)));
		return selectStatement;
	}

	/**
	 * TODO move to another class
	 * Checking if an String is numeric
	 *
	 * @param str String
	 * @return boolean
	 */
	public static boolean isNumeric(String str) {
		try {
			final double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
