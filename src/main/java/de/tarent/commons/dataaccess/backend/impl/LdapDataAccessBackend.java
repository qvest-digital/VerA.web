/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.dataaccess.backend.impl;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import de.tarent.commons.dataaccess.DataAccessException;
import de.tarent.commons.dataaccess.QueryProcessor;
import de.tarent.commons.dataaccess.StoreProcessor;
import de.tarent.commons.dataaccess.backend.AbstractDataAccessBackend;
import de.tarent.commons.dataaccess.backend.QueryingStrategy;
import de.tarent.commons.dataaccess.backend.QueryingWithQueryBuilder;
import de.tarent.commons.dataaccess.backend.StoringAttributeSets;
import de.tarent.commons.dataaccess.backend.StoringStrategy;
import de.tarent.commons.dataaccess.data.AttributeSet;
import de.tarent.commons.dataaccess.data.AttributeSetImpl;
import de.tarent.commons.dataaccess.data.AttributeSetList;
import de.tarent.commons.dataaccess.data.AttributeSetListImpl;
import de.tarent.commons.dataaccess.query.QueryBuilder;
import de.tarent.commons.dataaccess.query.impl.LdapQueryBuilder;

public class LdapDataAccessBackend extends AbstractDataAccessBackend implements QueryingWithQueryBuilder, StoringAttributeSets {
	private InMemoryTransactionAdapter inMemoryTransactionAdapter;

	/** Javax-Naming-Directory environment */
	private Hashtable environment;
	/** LDAP base URL, like <code>ldap://host/</code> */
	private String baseUrl;
	/** LDAP base URL, like <code>ou=company,dc=location</code> */
	private String baseDn;
	/** LDAP secutity authentication (mode like simple) */
	private String secutityAuthentication;
	/** LDAP secutity principal (username) */
	private String secutityPrincipal;
	/** LDAP secutity credentials (password) */
	private String secutityCredentials;

	/** Encoding filter for the ldap url and base dn. */
	private String encodingFilterInput;
	/** Encoding filter for the ldap url and base dn. */
	private String encodingFilterOutput;

	public LdapDataAccessBackend() {
		init();
	}

	public void init() {
		inMemoryTransactionAdapter = new InMemoryTransactionAdapter(this);
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public String getSecutityAuthentication() {
		return secutityAuthentication;
	}

	public void setSecutityAuthentication(String secutityAuthentication) {
		this.secutityAuthentication = secutityAuthentication;
	}

	public String getSecutityPrincipal() {
		return secutityPrincipal;
	}

	public void setSecutityPrincipal(String secutityPrincipal) {
		this.secutityPrincipal = secutityPrincipal;
	}

	public String getSecutityCredentials() {
		return secutityCredentials;
	}

	public void setSecutityCredentials(String secutityCredentials) {
		this.secutityCredentials = secutityCredentials;
	}

	public String getEncodingFilterInput() {
		return encodingFilterInput;
	}

	public void setEncodingFilterInput(String encodingFilterInput) {
		this.encodingFilterInput = encodingFilterInput;
	}

	public String getEncodingFilterOutput() {
		return encodingFilterOutput;
	}

	public void setEncodingFilterOutput(String encodingFilterOutput) {
		this.encodingFilterOutput = encodingFilterOutput;
	}

	public void internalStartup() {
		try {
			getEnvironment();
		} catch (NamingException e) {
			throw new DataAccessException(e);
		}
	}

	protected InitialDirContext getInitialDirContext() throws NamingException {
		return new InitialDirContext(getEnvironment());
	}

	protected Hashtable getEnvironment() throws NamingException {
		if (environment == null)
			environment = new Hashtable();
		
		if (!environment.containsKey(Context.INITIAL_CONTEXT_FACTORY))
			environment.put(Context.INITIAL_CONTEXT_FACTORY, getClassnameIfExist(
					new String[] {
							getExtraInitialContextFactoryClassname(),
							"com.sun.jndi.ldap.LdapCtxFactory" }));
		
		if (!environment.containsKey(Context.PROVIDER_URL))
			if (baseUrl != null && baseUrl.length() != 0)
				if (baseDn != null && baseDn.length() != 0)
					environment.put(Context.PROVIDER_URL, baseUrl + baseDn);
		
		if (!environment.containsKey(Context.SECURITY_AUTHENTICATION))
			if (secutityAuthentication != null && secutityCredentials.length() != 0)
				environment.put(Context.SECURITY_AUTHENTICATION, secutityAuthentication);
		
		if (!environment.containsKey(Context.SECURITY_PRINCIPAL))
			if (secutityPrincipal != null && secutityPrincipal.length() != 0)
		environment.put(Context.SECURITY_PRINCIPAL, secutityPrincipal);
		
		if (!environment.containsKey(Context.SECURITY_CREDENTIALS))
			if (secutityCredentials != null && secutityCredentials.length() != 0)
				environment.put(Context.SECURITY_CREDENTIALS, secutityCredentials);
		
		environment.put(Context.REFERRAL, "ignore");
		
		return environment;
	}

	protected String getExtraInitialContextFactoryClassname() {
		String name = getClass().getName() + "#initialContextFactory";
		String property = System.getProperty(name);
		if (property != null && property.length() != 0)
			return property;
		return "-D" + name;
	}

	protected String getClassnameIfExist(String[] classNames) throws NamingException {
		for (int i = 0; i < classNames.length; i++) {
			try {
				return Class.forName(classNames[i]).getName();
			} catch (Throwable t) {
				// nothing
			}
		}
		
		List classList = Arrays.asList(classNames);
		while (classList.remove(null)) {
			// nothing
		}
		throw new NamingException(
				"No initial context factory found for LDAP in classpath. " +
				"Searched this class list " + classList + ".");
	}

	public QueryingStrategy getQueryingStrategy() {
		return this;
	}

	public StoringStrategy getStoringStrategy() {
		return this;
	}

	public QueryBuilder getQueryBuilder() {
		return new LdapQueryBuilder();
	}

	public Object executeQuery(QueryProcessor queryProcessor, Object query) {
		String filterExpr = (String) query;
		String[] requestedAttributes = new String[] { "name", "displayName", "mail" };
		
		DirContext dirContext = null;
		
		try {
			AttributeSetList result = new AttributeSetListImpl();
			
			dirContext = getInitialDirContext();
			
			if (filterExpr == null) {
				NamingEnumeration listResultEnumeration = dirContext.list(
						getUrl(getBaseUrl(), getBaseDn()));
				fillAttributeSetList(
						result,
						true,
						0,
						dirContext,
						listResultEnumeration,
						requestedAttributes);
				listResultEnumeration.close();
			} else {
				SearchControls searchControls = new SearchControls();
				searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
				
				NamingEnumeration searchResultEnumeration = dirContext.search(
						getUrl(getBaseUrl(), getBaseDn()),
						filterExpr,
						requestedAttributes,
						searchControls);
				try {
					while (searchResultEnumeration.hasMore()) {
						SearchResult searchResult = (SearchResult) searchResultEnumeration.next();
						result.add(getAttributeSet(
								searchResult.getNameInNamespace(),
								searchResult.getAttributes()));
					}
				} catch (PartialResultException e) {
					// TODO log here System.err.println(e);
				}
				searchResultEnumeration.close();
			}
			
			return result;
		} catch (SizeLimitExceededException e) {
			throw new DataAccessException("Size limit!", e);
		} catch (NamingException e) {
			throw new DataAccessException(e);
		} finally {
			if (dirContext != null) {
				try {
					dirContext.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void fillAttributeSetList(
			AttributeSetList result,
			boolean recursiv,
			int level,
			DirContext dirContext,
			NamingEnumeration namingEnumeration,
			String[] requestedAttributes) throws NamingException {
		
		try {
			while (namingEnumeration.hasMore()) {
				NameClassPair nameClassPair = (NameClassPair) namingEnumeration.next();
				
				result.add(getAttributeSet(
						nameClassPair.getNameInNamespace(),
						dirContext.getAttributes(getUrl(getBaseUrl(),
								nameClassPair.getNameInNamespace()),
								requestedAttributes)));
				
				if (recursiv) {
					NamingEnumeration listResultEnumeration = dirContext.list(
							getUrl(getBaseUrl(), nameClassPair.getNameInNamespace()));
					fillAttributeSetList(
							result,
							recursiv,
							level + 1,
							dirContext,
							listResultEnumeration,
							requestedAttributes);
					listResultEnumeration.close();
				}
			}
		} catch (NamingException e) {
			throw e;
		}
	}

	public String getUrl(String url, String dn) {
		if (encodingFilterInput == null || encodingFilterOutput == null)
			return url + dn;
		try {
			return new String((url + dn).getBytes(
					encodingFilterInput),
					encodingFilterOutput);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Illegal encoding.", e);
		}
	}

	protected AttributeSet getAttributeSet(String fullLdapPath, Attributes attributes) throws NamingException {
		NamingEnumeration attributeIDs = null;
		try {
			AttributeSet attributeSet = new AttributeSetImpl();
			attributeSet.setAttribute("x-ldap-name", fullLdapPath);
			
			attributeIDs = attributes.getIDs();
			while (attributeIDs.hasMore()) {
				String attributeName = (String) attributeIDs.next();
				NamingEnumeration attributeValues = attributes.get(attributeName).getAll();
				while (attributeValues.hasMore()) {
					Object attributeValue = attributeValues.next();
					
					if (!attributeSet.containsAttribute(attributeName)) {
						attributeSet.setAttribute(attributeName, attributeValue);
					} else {
						Object oldValue = attributeSet.getAttribute(attributeName);
						if (oldValue instanceof ListOfAttributeValues) {
							((ListOfAttributeValues) oldValue).add(attributeValue);
						} else {
							attributeSet.setAttribute(attributeName, new ListOfAttributeValues(oldValue, attributeValue));
						}
					}
				}
			}
			
			return attributeSet;
		} finally {
			if (attributeIDs != null)
				attributeIDs.close();
			attributeIDs = null;
		}
	}

	/** {@inheritDoc} */
	public void store(StoreProcessor storeProcessor, AttributeSet attributeSet) {
		throw new DataAccessException("LdapDataAccessBackend currently do not support storing data.");
	}

	/** {@inheritDoc} */
	public void delete(StoreProcessor storeProcessor, AttributeSet o) {
		throw new DataAccessException("LdapDataAccessBackend currently do not support deleting data.");
	}

	/** {@inheritDoc} */
	public void begin() {
		inMemoryTransactionAdapter.begin();
	}

	/** {@inheritDoc} */
	public void commit() {
		inMemoryTransactionAdapter.commit();
	}

	/** {@inheritDoc} */
	public void rollback() {
		inMemoryTransactionAdapter.rollback();
	}

	public boolean isCommiting() {
		return inMemoryTransactionAdapter.isCommiting();
	}

	public int getRestrainedStoreEntries() {
		return inMemoryTransactionAdapter.getRestrainedStoreEntries();
	}

	public int getRestrainedDeleteEntries() {
		return inMemoryTransactionAdapter.getRestrainedDeleteEntries();
	}

	public int getRestrainedEntries() {
		return inMemoryTransactionAdapter.getRestrainedEntries();
	}

	public static class ListOfAttributeValues extends LinkedList {
		private static final long serialVersionUID = -2014011835872263899L;

		private ListOfAttributeValues(Object oldValue, Object attributeValue) {
			add(oldValue);
			add(attributeValue);
		}
	}
}
