package de.tarent.aa.veraweb.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.scim.User;

import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * @author Max Marche <m.marche@tarent.de>, tarent solutions GmbH
 */
public class OSIAMWorker {
	/**
	 * Example Property file:
	client.id=example-client
	client.secret=secret
	client.redirect_uri=http://osiam-test.lan.tarent.de:8080/addon-administration/
	
	osiam.server.resource=http://osiam-test.lan.tarent.de:8080/osiam-resource-server/
	osiam.server.auth=http://osiam-test.lan.tarent.de:8080/osiam-auth-server/
	 */
	private static final String PROPERTY_KEY_OSIAM_RESOURCE_SERVER_ENDPOINT = "osiam.server.resource";
	private static final String PROPERTY_KEY_OSIAM_AUTH_SERVER_ENDPOINT = "osiam.server.auth";
	private static final String PROPERTY_KEY_CLIENT_REDIRECT_URI = "client.redirect_uri";
	private static final String PROPERTY_KEY_CLIENT_SECRET = "client.secret";
	private static final String PROPERTY_KEY_CLIENT_ID = "client.id";
	
	private static final String OSIAM_PROPERTY_FILE = "/etc/veraweb/osiam.properties";
	private static final int OSIAM_USERNAME_LENGTH = 6;
	
	/** Eingabe-Parameter für die Octopus-Aktion {@link #createDelegationUsers(OctopusContext)} */
	public static final String INPUT_createDelegationUsers[] = {};
	
	private OsiamConnector connector;
	private Properties properties;

	public OSIAMWorker() throws IOException {
		this.loadProperties();
		
		this.connector = new OsiamConnector.Builder()
				.setClientRedirectUri(this.getProperty(PROPERTY_KEY_CLIENT_REDIRECT_URI))
				.setClientSecret(this.getProperty(PROPERTY_KEY_CLIENT_SECRET))
				.setClientId(this.getProperty(PROPERTY_KEY_CLIENT_ID))
				.setAuthServerEndpoint(this.getProperty(PROPERTY_KEY_OSIAM_AUTH_SERVER_ENDPOINT))
				.setResourceServerEndpoint(this.getProperty(PROPERTY_KEY_OSIAM_RESOURCE_SERVER_ENDPOINT))
				.build();
	}


	/**
	 * Creates an new user in OSIAM via connector4java and the configured client
	 * @param ctx
	 * @throws BeanException 
	 * @throws SQLException 
	 */
	public void createDelegationUsers(OctopusContext ctx) throws BeanException, SQLException{
		
		Database database = new DatabaseVeraWeb(ctx);
		TransactionContext context = database.getTransactionContext();

		
		AccessToken accessToken = connector.retrieveAccessToken(Scope.ALL);
		List selectdelegation = (List) ctx.sessionAsObject("addguest-selectdelegation");
		Map event = (Map)ctx.getContextField("event");
			
		for (Object object : selectdelegation) {
			WhereList where = new WhereList();		
			where.addOr(new Where("pk", Integer.parseInt(object.toString()), "="));
			
			Select select = SQL.Select( database ).
					from("veraweb.tperson")
					.where(where);
			select.select("*");
			ResultSet result = database.result(select);

			String companyName = "";
			while(result.next()) {
				 companyName = result.getString("company_a_e1");
			}
			
			StringBuilder passwordBuilder = new StringBuilder();
			String shortName = event.get("shortname").toString();
			String login = this.generateUsername();
			passwordBuilder.append(extractFirstXChars(shortName, 3));
			passwordBuilder.append(extractFirstXChars(companyName, 3));
			passwordBuilder.append(extractFirstXChars(event.get("begin").toString(), 10));
			
			User delegationUser = new User.Builder(login)
					.setActive(true)
					.setPassword(passwordBuilder.toString())
					.build();

			//create User in osiam
			this.connector.createUser(delegationUser, accessToken);
			
			saveOsiamLogin(database, login, Integer.parseInt(event.get("id").toString()), Integer.parseInt(object.toString()));
		}
	}
	
	private void saveOsiamLogin(Database db, String login, int eventId, int personId) throws BeanException, SyntaxErrorException, SQLException{
		TransactionContext context = db.getTransactionContext();
		WhereList whereA = new WhereList();
		whereA.addAnd(new Where("fk_person", personId, "="));
		whereA.addAnd(new Where("fk_event", eventId, "="));
		//Example Query:
		//	UPDATE veraweb.tguest SET osiam_login='aaa'  WHERE (fk_person=2 AND fk_event=1)
		Update update = SQL.Update(db)
				.where(whereA);
		update.table("veraweb.tguest");
		update.update("osiam_login", login);
		

		DB.insert(context, update.statementToString());
        context.commit();
	}
	
	private String extractFirstXChars(String value, int x) {
		return value.substring(0, Math.min(value.length(), x));
	}

	private String generateUsername() {
		char[] symbols = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		int length = OSIAM_USERNAME_LENGTH;
		Random random = new SecureRandom(); 
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
		    int indexRandom = random.nextInt( symbols.length );
		    sb.append( symbols[indexRandom] );
		}
		return sb.toString();
	}
	
	private String getProperty(String key) {
		return this.properties.getProperty(key);
	}
	
	private void loadProperties() throws IOException {
		Properties prop = new Properties();
		
		FileInputStream inputStream = new FileInputStream(new File(OSIAM_PROPERTY_FILE));
		try {
			inputStream = new FileInputStream(OSIAM_PROPERTY_FILE);
			prop.load(inputStream);
		} finally {
			try { inputStream.close(); } catch (Exception e) { }
		}
		
		this.properties = prop;
	}
}
