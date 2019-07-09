package de.tarent.octopus.cronjobs;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.Calendar;
import java.util.Map;

/**
 * This implements a CronJob that is either started on a exactly specified date
 * or repeatedly on defined dates like "every sunday" or "every 15th of September"
 *
 * @author Nils Neumaier (n.neumaier@tarent.de)
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */

public class ExactCronJob extends CronJob {
    public static final int JANUARY = Calendar.JANUARY;
    public static final int FEBRUARY = Calendar.FEBRUARY;
    public static final int MARCH = Calendar.MARCH;
    public static final int APRIL = Calendar.APRIL;
    public static final int MAY = Calendar.MAY;
    public static final int JUNE = Calendar.JUNE;
    public static final int JULY = Calendar.JULY;
    public static final int AUGUST = Calendar.AUGUST;
    public static final int SEPTEMBER = Calendar.SEPTEMBER;
    public static final int OCTOBER = Calendar.OCTOBER;
    public static final int NOVEMBER = Calendar.NOVEMBER;
    public static final int DECEMBER = Calendar.DECEMBER;

    public static final int SUNDAY = Calendar.SUNDAY;
    public static final int MONDAY = Calendar.MONDAY;
    public static final int TUESDAY = Calendar.TUESDAY;
    public static final int WEDNESDAY = Calendar.WEDNESDAY;
    public static final int THURSDAY = Calendar.THURSDAY;
    public static final int FRIDAY = Calendar.FRIDAY;
    public static final int SATURDAY = Calendar.SATURDAY;

    public static final String PROPERTIESMAP_KEY_HOUR = "hour";
    public static final String PROPERTIESMAP_KEY_MINUTE = "minute";
    public static final String PROPERTIESMAP_KEY_MONTH = "month";
    public static final String PROPERTIESMAP_KEY_DAYOFWEEK = "dayofweek";
    public static final String PROPERTIESMAP_KEY_DAYOFMONTH = "dayofmonth";

    private int hour = -1;
    private int minute = -1;
    private int month = -1;
    private int dayOfMonth = -1;
    private int dayOfWeek = -1;

    /**
     * Creates a new ExactCronJob. This job will be run at an exact time either once or
     * multiple times on recurring time events. It closely resembles the unix cron system but
     * without the possibility of giving multiple values for a field.
     * <p>
     * To use a wildcard for a field (equivalent to "*" in unix cron), use the value -1 for
     * the fields.
     *
     * @param hour       The hour, the job should be run (24h clock).
     * @param minute     The minute, the job should be run.
     * @param month      The month, the job should be run.
     * @param dayOfMonth The day of month, the job should be run.
     * @param dayOfWeek  The day of week, the job should be run.
     */
    public ExactCronJob(Cron cron, int hour, int minute, int month, int dayOfMonth, int dayOfWeek) {
        super(cron);

        setHour(hour);
        setMinute(minute);
        setMonth(month);
        setDayOfMonth(dayOfMonth);
        setDayOfWeek(dayOfWeek);
    }

    /**
     * Creates a new ExactCronJob that will be run every time the clock
     * equals the given hour and minute.
     *
     * @param hour   The hour, the job should be run (24h clock).
     * @param minute The minute, the job should be run (24h clock).
     */
    public ExactCronJob(Cron cron, int hour, int minute) {
        this(cron, hour, minute, -1, -1, -1);
    }

    /**
     * Returns the type of this CronJob.
     */
    public int getType() {
        return Cron.EXACT_CRONJOB;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * Extends the method getCronJobMap() of the abstract superclass CronJob
     * The cronjobmap generated by the superclass is extended by property values
     * specific for exact-cronjobs
     */

    public Map getCronJobMap() {
        Map cronJobMap = super.getCronJobMap();
        Map properties = (Map) cronJobMap.get(Cron.CRONJOBMAP_KEY_PROPERTIES);

        properties.put(ExactCronJob.PROPERTIESMAP_KEY_HOUR, new Integer(getHour()));
        properties.put(ExactCronJob.PROPERTIESMAP_KEY_MINUTE, new Integer(getMinute()));
        properties.put(ExactCronJob.PROPERTIESMAP_KEY_MONTH, new Integer(getMonth()));
        properties.put(ExactCronJob.PROPERTIESMAP_KEY_DAYOFWEEK, new Integer(getDayOfWeek()));
        properties.put(ExactCronJob.PROPERTIESMAP_KEY_DAYOFMONTH, new Integer(getDayOfMonth()));

        cronJobMap.put(Cron.CRONJOBMAP_KEY_PROPERTIES, properties);

        return cronJobMap;
    }
}
