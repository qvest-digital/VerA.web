package de.tarent.aa.veraweb.worker;

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
import de.tarent.aa.veraweb.utils.PropertiesReader;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.scim.User;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * @author Max Marche <m.marche@tarent.de>, tarent solutions GmbH
 */
public class OSIAMWorker {
	/**
	 * Example Property file: client.id=example-client client.secret=secret
	 * client
	 * .redirect_uri=http://osiam-test.lan.tarent.de:8080/addon-administration/
	 *
	 * osiam.server.resource=http://osiam-test.lan.tarent.de:8080/osiam-resource
	 * -server/
	 * osiam.server.auth=http://osiam-test.lan.tarent.de:8080/osiam-auth-server/
	 */
	private static final String OSIAM_RESOURCE_SERVER_ENDPOINT = "osiam.server.resource";
	private static final String OSIAM_AUTH_SERVER_ENDPOINT = "osiam.server.auth";
	private static final String OSIAM_CLIENT_REDIRECT_URI = "osiam.client.redirect_uri";
	private static final String OSIAM_CLIENT_SECRET = "osiam.client.secret";
	private static final String OSIAM_CLIENT_ID = "osiam.client.id";

	public static final String INPUT_createDelegationUsers[] = {};

	private static final int OSIAM_USERNAME_LENGTH = 6;

	private static final String VWOR_ACTIVE_PARAM = "online-registration.activated";

	private OsiamConnector connector;
	private Properties properties;

	public OSIAMWorker() {
		final PropertiesReader propertiesReader = new PropertiesReader();
		if (propertiesReader.propertiesAreAvailable()) {
			this.properties = propertiesReader.getProperties();
			this.connector = new OsiamConnector.Builder()
					.setClientRedirectUri(
							properties.getProperty(OSIAM_CLIENT_REDIRECT_URI))
					.setClientSecret(
							properties.getProperty(OSIAM_CLIENT_SECRET))
					.setClientId(properties.getProperty(OSIAM_CLIENT_ID))
					.setAuthServerEndpoint(
							properties.getProperty(OSIAM_AUTH_SERVER_ENDPOINT))
					.setResourceServerEndpoint(
							properties
									.getProperty(OSIAM_RESOURCE_SERVER_ENDPOINT))
					.build();
		}
	}

	private boolean checkIfOnlineRegistrationIsAvailable(OctopusContext ctx) {
		return Boolean.valueOf(ctx.getContextField(VWOR_ACTIVE_PARAM).toString());
	}

	/**
	 * Creates an new user in OSIAM via connector4java and the configured client
	 *
	 * @param octopusContext The {@link OctopusContext}
	 * @throws BeanException
	 * @throws SQLException
	 */
	public void createDelegationUsers(OctopusContext octopusContext) throws BeanException,
			SQLException {
		if(!checkIfOnlineRegistrationIsAvailable(octopusContext)) {
			return;
		}

		Database database = new DatabaseVeraWeb(octopusContext);
		AccessToken accessToken = null;
		Boolean correctOSIAMProperties = true;
		try {
			accessToken = connector.retrieveAccessToken(Scope.ALL);
		} catch (Exception e) {
			correctOSIAMProperties = false;
		}
		if (correctOSIAMProperties) {
			List selectdelegation = (List) octopusContext
					.sessionAsObject("addguest-selectdelegation");
			Map event = (Map) octopusContext.getContextField("event");

			for (Object id : selectdelegation) {
				ResultSet result = getPersons(database, id);
				String companyName = getCompanyName(result);
				String login = this.generateUsername();
				String password = generatePassword(event, companyName);
				createOsiamUser(accessToken, login, password);
				saveOsiamLogin(database, login,
						Integer.parseInt(event.get("id").toString()),
						Integer.parseInt(id.toString()));
			}
		}
	}

	private void createOsiamUser(AccessToken accessToken, String login,
			String password) {
		User delegationUser = new User.Builder(login).setActive(true)
				.setPassword(password).build();

		// create User in osiam
		this.connector.createUser(delegationUser, accessToken);
	}

	private String generatePassword(Map event, String companyName) {
		StringBuilder passwordBuilder = new StringBuilder();
		String shortName = event.get("shortname").toString();

		passwordBuilder.append(extractFirstXChars(shortName, 3));
		passwordBuilder.append(extractFirstXChars(companyName, 3));
		passwordBuilder.append(extractFirstXChars(
				event.get("begin").toString(), 10));

		return passwordBuilder.toString();
	}

	private String getCompanyName(ResultSet result) throws SQLException {
		String companyName = "";
		while (result.next()) {
			companyName = result.getString("company_a_e1");
		}
		return companyName;
	}

	private ResultSet getPersons(Database database, Object id)
			throws BeanException {
		WhereList filter = new WhereList();
		filter.add(new Where("pk", Integer.parseInt(id.toString()), "="));

		Select selectPerson = SQL.Select(database).from("veraweb.tperson")
				.where(filter);
		selectPerson.select("*");
		return database.result(selectPerson);
	}

	private void saveOsiamLogin(Database db, String login, int eventId,
			int personId) throws BeanException,
			SQLException {
		final TransactionContext context = db.getTransactionContext();
		final WhereList whereCriterias = new WhereList();
		whereCriterias.addAnd(new Where("fk_person", personId, "="));
		whereCriterias.addAnd(new Where("fk_event", eventId, "="));
		// Example Query:
		// UPDATE veraweb.tguest SET osiam_login='aaa' WHERE (fk_person=2 AND
		// fk_event=1)
		final Update update = SQL.Update(db).where(whereCriterias);
		update.table("veraweb.tguest");
		update.update("osiam_login", login);

		DB.insert(context, update.statementToString());
		context.commit();
	}

	private String extractFirstXChars(String value, int x) {
		return value.substring(0, Math.min(value.length(), x));
	}

	private String generateUsername() {
		char[] symbols = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
				.toCharArray();
		int length = OSIAM_USERNAME_LENGTH;
		Random random = new SecureRandom();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int indexRandom = random.nextInt(symbols.length);
			sb.append(symbols[indexRandom]);
		}
		return sb.toString();
	}


}
