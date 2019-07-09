package de.tarent.aa.veraweb.beans.facade;
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
