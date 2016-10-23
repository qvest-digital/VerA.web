/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.beans.facade;

/**
 * Definiert eine Address-Facade, inkl. Strasse, PLZ, Ort, etc.
 *
 * @author Michael Klink, Christoph Jerolimov
 */
public interface PersonAddressFacade {
    public String getFunction();

    public String getCompany();

    public String getStreet();

    public String getZipCode();

    public String getState();

    public String getCity();

    public String getCountry();

    public String getPOBox();

    public String getPOBoxZipCode();

    public String getSuffix1();

    public String getSuffix2();

    public String getPhone();

    public String getFax();

    public String getMobile();

    public String getEMail();

    public String getUrl();

    public void setFunction(String value);

    public void setCompany(String value);

    public void setStreet(String value);

    public void setZipCode(String value);

    public void setState(String value);

    public void setCity(String value);

    public void setCountry(String value);

    public void setPOBox(String value);

    public void setPOBoxZipCode(String value);

    public void setSuffix1(String value);

    public void setSuffix2(String value);

    public void setPhone(String value);

    public void setFax(String value);

    public void setMobile(String value);

    public void setEMail(String value);

    public void setUrl(String value);
}
