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
package de.tarent.aa.veraweb.cucumber;

import org.junit.runner.RunWith;

import cucumber.junit.Cucumber;

/**
 * JUnit starter for running cucumber tests.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * 
 */
@RunWith(Cucumber.class)
@Cucumber.Options(
// this code will only look into "features/" folder for features
features = { "classpath:features/" },

// this code will be run only scenarios which annotated with "@wip"
tags = { "@wip" },

// DO NOT REMOVE THIS FORMATTER!
format = { "de.tarent.aa.veraweb.cucumber.formatter.RuntimeInfoCatcher" })
public class JUnitStarter {

    /*******************************************************
     * DO NOT ADD SOME METHODS! THIS CLASS MUST BE EMPTY!!!
     *******************************************************/
}

