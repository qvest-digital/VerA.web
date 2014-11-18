package de.tarent.aa.veraweb.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.RandomStringUtils;
import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.scim.User;

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
	private static final int OSIAM_USERNAME_LENGTH = 10;
	private static final int OSIAM_PASSWORD_LENGTH = 8;
	
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
	 */
	public void createDelegationUsers(OctopusContext ctx) {
		AccessToken accessToken = connector.retrieveAccessToken(Scope.ALL);
		List selectdelegation = (List) ctx.sessionAsObject("addguest-selectdelegation");

		for (Object object : selectdelegation) {
			User delegationUser = new User.Builder()
					.setActive(true)
					.setNickName(this.generateUsername())
					.setPassword(this.generateSecurePassword())
					.build();

		//TODO the following line is for a future task
		//this.connector.createUser(delegationUser, accessToken); //later, it will be used
		}
	}

	private String generateUsername() {
		return RandomStringUtils.random(OSIAM_USERNAME_LENGTH);
	}
	
	private String generateSecurePassword() {
		return RandomStringUtils.random(OSIAM_PASSWORD_LENGTH);
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
		

	}
}
