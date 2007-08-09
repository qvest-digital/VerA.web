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

package de.tarent.commons.dataaccess.config;

import de.tarent.commons.dataaccess.backend.impl.SqlDataAccessBackend;
import de.tarent.commons.dataaccess.config.ConfigParser;

import junit.framework.TestCase;

public class ConfigParserTest extends TestCase {
	private String EXPECTED_BACKEND_CONFIG_XML =
		"<config>\n" +
		"  <dataaccess name=\"hsqldb-memory01\">\n" +
		"    <class>de.tarent.commons.dataaccess.backend.impl.SqlDataAccessBackend</class>\n" +
		"    <parameters>\n" +
		"      <jdbcURL>jdbc:hsqldb:mem:memory01</jdbcURL>\n" +
		"      <username>sa</username>\n" +
		"    </parameters>\n" +
		"  </dataaccess>\n" +
		"  <dataaccess name=\"hsqldb-memory02\">\n" +
		"    <class>de.tarent.commons.dataaccess.backend.impl.SqlDataAccessBackend</class>\n" +
		"    <parameters>\n" +
		"      <jdbcURL>jdbc:hsqldb:mem:memory02</jdbcURL>\n" +
		"      <username>sa</username>\n" +
		"    </parameters>\n" +
		"  </dataaccess>\n" +
		"</config>";

	private String EXPECTED_MAPPING_CONFIG_XML =
		"<config>\n" +
		"  <mapping for=\"hsqldb-memory01\">\n" +
		"    <class>de.tarent.commons.dataaccess.test.Person</class>\n" +
		"    <rules>\n" +
		"      <rename result=\"uid\" backend=\"x-ldap-name\" direction=\"both\" drop=\"true\"/>\n" +
		"      <rename result=\"firstname\" backend=\"givenName\" direction=\"both\" drop=\"true\"/>\n" +
		"      <rename result=\"lastname\" backend=\"sn\" direction=\"both\" drop=\"true\"/>\n" +
		"      <ignore result=\"*\" backend=\"*\" direction=\"both\" drop=\"true\"/>\n" +
		"    </rules>\n" +
		"  </mapping>\n" +
		"  <mapping for=\"hsqldb-memory01\">\n" +
		"    <class>de.tarent.commons.dataaccess.test.Address</class>\n" +
		"    <rules/>\n" +
		"  </mapping>\n" +
		"</config>";

	public void testBackendConfigFormatter() {
		BackendConfig backendConfig = new BackendConfig();
		
		SqlDataAccessBackend sqlDataAccessBackend = new SqlDataAccessBackend();
		sqlDataAccessBackend.setJdbcURL("jdbc:hsqldb:mem:memory01");
		sqlDataAccessBackend.setUsername("sa");
		sqlDataAccessBackend.setPassword(null);
		
		backendConfig.putDataAccessBackend("hsqldb-memory01", sqlDataAccessBackend);
		
		SqlDataAccessBackend sqlDataAccessBackend2 = new SqlDataAccessBackend();
		sqlDataAccessBackend2.setJdbcURL("jdbc:hsqldb:mem:memory02");
		sqlDataAccessBackend2.setUsername("sa");
		sqlDataAccessBackend.setPassword(null);
		
		backendConfig.putDataAccessBackend("hsqldb-memory02", sqlDataAccessBackend2);
		
		String xml = new ConfigParser().formatBackends(backendConfig);
		
		assertEquals(EXPECTED_BACKEND_CONFIG_XML, xml);
	}

	public void testMappingConfigFormatter() {
		MappingConfig mappingConfig = new MappingConfig();
		
		MappingRules rules = new MappingRules();
		rules.addRenameRule("uid", "x-ldap-name");
		rules.addRenameRule("firstname", "givenName");
		rules.addRenameRule("lastname", "sn");
		rules.addIgnoreRule("*", "*");

		mappingConfig.addMappingRules(
				"hsqldb-memory01",
				"de.tarent.commons.dataaccess.test.Person",
				rules);

		mappingConfig.addMappingRules(
				"hsqldb-memory01",
				"de.tarent.commons.dataaccess.test.Address",
				new MappingRules());
		
		String xml = new ConfigParser().formatMappings(mappingConfig);
		
		assertEquals(EXPECTED_MAPPING_CONFIG_XML, xml);
	}

	public void testBackendConfigParser() {
		BackendConfig backendConfig = new ConfigParser().parseBackends(EXPECTED_BACKEND_CONFIG_XML);
		
		assertEquals(2, backendConfig.size());
		SqlDataAccessBackend sqlDataAccessBackend = (SqlDataAccessBackend) backendConfig.getDataAccessBackend("hsqldb-memory01");
		SqlDataAccessBackend sqlDataAccessBackend2 = (SqlDataAccessBackend) backendConfig.getDataAccessBackend("hsqldb-memory02");
		
		assertNotNull(sqlDataAccessBackend);
		assertNotNull(sqlDataAccessBackend2);
		
		assertEquals("jdbc:hsqldb:mem:memory01", sqlDataAccessBackend.getJdbcURL());
		assertEquals("sa", sqlDataAccessBackend.getUsername());
		assertNull(sqlDataAccessBackend.getPassword());
		
		assertEquals("jdbc:hsqldb:mem:memory02", sqlDataAccessBackend2.getJdbcURL());
		assertEquals("sa", sqlDataAccessBackend2.getUsername());
		assertNull(sqlDataAccessBackend2.getPassword());
	}

	public void testMappingConfigParser() {
		MappingConfig mappingConfig = new ConfigParser().parseMappings(EXPECTED_MAPPING_CONFIG_XML);
		
		assertEquals(2, mappingConfig.size());
		
		MappingRules mappingRules1 = mappingConfig.getRules(
				"hsqldb-memory01", "de.tarent.commons.dataaccess.test.Person");
		MappingRules mappingRules2 = mappingConfig.getRules(
				"hsqldb-memory01", "de.tarent.commons.dataaccess.test.Address");
		
		assertNotNull(mappingRules1);
		assertNotNull(mappingRules2);
		
		assertEquals(4, mappingRules1.getRules().size());
		assertEquals(0, mappingRules2.getRules().size());
		
		MappingRules.Rule rule1 = (MappingRules.Rule) mappingRules1.getRules().get(0);
		MappingRules.Rule rule2 = (MappingRules.Rule) mappingRules1.getRules().get(1);
		MappingRules.Rule rule3 = (MappingRules.Rule) mappingRules1.getRules().get(2);
		MappingRules.Rule rule4 = (MappingRules.Rule) mappingRules1.getRules().get(3);
		
		assertEquals("uid", rule1.getResult());
		assertEquals("x-ldap-name", rule1.getBackend());
		assertEquals("both", rule1.getDirection());
		assertEquals(true, rule1.isLastRule());
		
		assertEquals("firstname", rule2.getResult());
		assertEquals("givenName", rule2.getBackend());
		assertEquals("both", rule2.getDirection());
		assertEquals(true, rule2.isLastRule());
		
		assertEquals("lastname", rule3.getResult());
		assertEquals("sn", rule3.getBackend());
		assertEquals("both", rule3.getDirection());
		assertEquals(true, rule3.isLastRule());
		
		assertEquals("*", rule4.getResult());
		assertEquals("*", rule4.getBackend());
		assertEquals("both", rule4.getDirection());
		assertEquals(true, rule4.isLastRule());
	}
}
