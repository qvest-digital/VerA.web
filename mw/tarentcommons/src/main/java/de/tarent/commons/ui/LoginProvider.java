package de.tarent.commons.ui;

import de.tarent.commons.config.ConnectionDefinition;

/**
 *
 * An interface for classes providing login-credentials
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */
public interface LoginProvider
{
	/**
	 * <p>Returns the username to be used for connection</p>
	 *
	 * <p>Only used, if shouldContinueSession() returns false</p>
	 *
	 * @return the username to be used for connection
	 */
	public String getUserName();

	/**
	 * <p>Returns the password to be used for connection</p>
	 *
	 * <p>Only used, if shouldContinueSession() returns false</p>
	 *
	 * @return
	 */
	public String getPassword();

	/**
	 *
	 * If the application should try to reconnect with an existing session-cookie
	 *
	 * @return if the user wants to reconnect
	 */
	public boolean shouldContinueSession();

	/**
	 *
	 * <p>Returns the {@link de.tarent.config.ConnectionDefinition} to be used for connection</p>
	 *
	 * <p>Only used, if shouldContinueSession() returns false</p>
	 *
	 * @return the {@link de.tarent.config.ConnectionDefinition} containing connection-information like server-address, port etc
	 */
	public ConnectionDefinition getConnectionDefinition();
}
