package de.tarent.aa.veraweb.beans.facade;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
/**
 * Definiert eine Address-Facade, inkl. Strasse, PLZ, Ort, etc.
 *
 * @author Michael Klink, Christoph Jerolimov
 */
public interface PersonAddressFacade {
    String getFunction();

    String getCompany();

    String getStreet();

    String getZipCode();

    String getState();

    String getCity();

    String getCountry();

    String getPOBox();

    String getPOBoxZipCode();

    String getSuffix1();

    String getSuffix2();

    String getPhone();

    String getFax();

    String getMobile();

    String getEMail();

    String getUrl();

    void setFunction(String value);

    void setCompany(String value);

    void setStreet(String value);

    void setZipCode(String value);

    void setState(String value);

    void setCity(String value);

    void setCountry(String value);

    void setPOBox(String value);

    void setPOBoxZipCode(String value);

    void setSuffix1(String value);

    void setSuffix2(String value);

    void setPhone(String value);

    void setFax(String value);

    void setMobile(String value);

    void setEMail(String value);

    void setUrl(String value);
}
