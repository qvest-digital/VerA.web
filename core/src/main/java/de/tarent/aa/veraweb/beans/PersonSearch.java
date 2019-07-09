package de.tarent.aa.veraweb.beans;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

import java.util.Date;
import java.util.List;

public class PersonSearch extends AbstractBean {
    public String firstname;
    public String lastname;
    public String function;
    public String company;
    public String street;
    public String zipcode;
    public String state;
    public String city;
    public String country;
    public Integer workarea;
    public List categoriesSelection;
    public Integer categorie2;
    public Boolean iscompany;
    public String importsource;
    public Integer validtype;
    public Date validdate;
    public Boolean onlyhosts;
    public Boolean findAll;
    public String lastlistorder;
    public String listorder;
    public Boolean sortList;
    public String sort;
    public String languages;
    public String internal_id;

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Test ist, ob der Benutzer Standard-Reader ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkRead(OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfigAA.GROUP_READ_STANDARD);
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
     * werden darf.<br>
     * Test ist, ob der Benutzer Writer ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfigAA.GROUP_WRITE);
    }
}
