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

/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 28.02.2006
 */

package de.tarent.octopus.cronjobs;
import java.util.Calendar;
import java.util.Map;

/**
 * This implements a CronJob that is either started on a exactly specified date
 * or repeatedly on defined dates like "every sunday" or "every 15th of September"
 *
 * @author Nils Neumaier (n.neumaier@tarent.de)
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 *
 */

public class ExactCronJob extends CronJob
{
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

    public static final String PROPERTIESMAP_KEY_HOUR               = "hour";
    public static final String PROPERTIESMAP_KEY_MINUTE             = "minute";
    public static final String PROPERTIESMAP_KEY_MONTH              = "month";
    public static final String PROPERTIESMAP_KEY_DAYOFWEEK          = "dayofweek";
    public static final String PROPERTIESMAP_KEY_DAYOFMONTH         = "dayofmonth";

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
     * @param hour The hour, the job should be run (24h clock).
     * @param minute The minute, the job should be run.
     * @param month The month, the job should be run.
     * @param dayOfMonth The day of month, the job should be run.
     * @param dayOfWeek The day of week, the job should be run.
     */
    public ExactCronJob(Cron cron, int hour, int minute, int month, int dayOfMonth, int dayOfWeek)
    {
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
     * @param hour The hour, the job should be run (24h clock).
     * @param minute The minute, the job should be run (24h clock).
     */
    public ExactCronJob(Cron cron, int hour, int minute)
    {
        this(cron, hour, minute, -1, -1, -1);
    }

    /**
     * Returns the type of this CronJob.
     */
    public int getType()
    {
        return Cron.EXACT_CRONJOB;
    }

    public int getDayOfMonth()
    {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth)
    {
        this.dayOfMonth = dayOfMonth;
    }

    public int getDayOfWeek()
    {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek)
    {
        this.dayOfWeek = dayOfWeek;
    }

    public int getHour()
    {
        return hour;
    }

    public void setHour(int hour)
    {
        this.hour = hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public void setMinute(int minute)
    {
        this.minute = minute;
    }

    public int getMonth()
    {
        return month;
    }

    public void setMonth(int month)
    {
        this.month = month;
    }

    /**
     * Extends the method getCronJobMap() of the abstract superclass CronJob
     * The cronjobmap generated by the superclass is extended by property values
     * specific for exact-cronjobs
     */

    public Map getCronJobMap(){
        Map cronJobMap = super.getCronJobMap();
        Map properties = (Map)cronJobMap.get(Cron.CRONJOBMAP_KEY_PROPERTIES);

        properties.put(ExactCronJob.PROPERTIESMAP_KEY_HOUR, new Integer(getHour()));
        properties.put(ExactCronJob.PROPERTIESMAP_KEY_MINUTE, new Integer(getMinute()));
        properties.put(ExactCronJob.PROPERTIESMAP_KEY_MONTH, new Integer(getMonth()));
        properties.put(ExactCronJob.PROPERTIESMAP_KEY_DAYOFWEEK, new Integer(getDayOfWeek()));
        properties.put(ExactCronJob.PROPERTIESMAP_KEY_DAYOFMONTH, new Integer(getDayOfMonth()));

        cronJobMap.put(Cron.CRONJOBMAP_KEY_PROPERTIES, properties);

        return cronJobMap;
    }
}
