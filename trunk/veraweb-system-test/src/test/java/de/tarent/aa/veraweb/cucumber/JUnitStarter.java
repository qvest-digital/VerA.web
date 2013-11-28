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

