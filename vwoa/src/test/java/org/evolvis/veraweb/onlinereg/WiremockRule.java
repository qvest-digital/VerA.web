package org.evolvis.veraweb.onlinereg;

/*-
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
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.IOException;

/**
 * Created by mley on 28.08.14.
 * WireMock must be started outside JVM, because of dependency collisions
 */
public class WiremockRule implements TestRule {

    private Process wiremock;

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                startIfRequired();
                try {
                    base.evaluate();
                } finally {
                    stop();
                }
            }
        };
    }

    private void startIfRequired() throws IOException {
        if(wiremock != null) {
            return;
        }

        String wiremockJarPath = System.getenv("HOME")+"/.m2/repository/com/github/tomakehurst/wiremock/1.47/wiremock-1.47-standalone.jar";
        String wiremockDir = this.getClass().getResource("/wiremock").getPath();
        System.out.println("Using WireMock JAR: "+wiremockJarPath);
        System.out.println("Starting WireMock in directory: " + wiremockDir);
        wiremock = Runtime.getRuntime().exec(new String[]{"java", "-jar",wiremockJarPath, "--port", "8091"}, null, new File(wiremockDir));
/*
        new Thread() {
            public void run() {
                BufferedReader bi = new BufferedReader(new InputStreamReader(wiremock.getInputStream()));
                BufferedReader be = new BufferedReader(new InputStreamReader(wiremock.getErrorStream()));
                try {
                    String line;
                    while (wiremock != null) {
                        if ((line = bi.readLine()) != null) {
                            System.out.println(line);
                        }

                        if ((line = be.readLine()) != null) {
                            System.out.println(line);
                        }
                        Thread.yield();
                    }
                } catch (IOException e) {
                }
            }
        }.start();*/

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if(wiremock != null) {
                    wiremock.destroy();
                    try {
                        wiremock.waitFor();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    wiremock = null;
                }
            }
        });
    }


    private void stop() throws InterruptedException {
        if (wiremock != null) {
            wiremock.destroy();
            wiremock.waitFor();
            wiremock = null;
        }

    }
}
