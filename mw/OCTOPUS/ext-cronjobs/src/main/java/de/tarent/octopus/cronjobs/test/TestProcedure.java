/*
 * tarent-octopus cronjob extension,
 * an opensource webservice and webapplication framework (cronjob extension)
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
 * interest in the program 'tarent-octopus cronjob extension'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.octopus.cronjobs.test;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TestProcedure implements Runnable {

    private Logger logger = Logger.getLogger(TestProcedure.class.getName());

    private String ausgabe;

    public void run() {
        for (int i = 0; i < 6; i++){
            if (Thread.interrupted()){
                logger.log(Level.WARNING,"THREAD INTERRUPTED");
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.log(Level.WARNING,"THREAD INTERRUPTED");
                break;
            }
            logger.log(Level.INFO,"TEST " + i + ": " + ausgabe );
            System.out.println("TEST " + i + ": " + ausgabe);

        }

    }

    public void setAusgabe(String ausgabe){
        this.ausgabe = ausgabe;
    }

}
