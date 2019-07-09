package de.tarent.aa.veraweb.beans;
import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

public class Location extends AbstractBean implements OrgUnitDependent, Comparable<Location> {
    /**
     * PK.
     */
    public Integer id;

    /**
     * Organisation unit.
     */
    public Integer orgunit;

    /**
     * Name of location.
     */
    public String name;

    /**
     * Contact person.
     */
    public String contactperson;

    /**
     * address.
     */
    public String address;

    /**
     * zip code.
     */
    public String zip;

    /**
     * location.
     */
    public String location;

    /**
     * Call number.
     */
    public String callnumber;

    /**
     * Fax number.
     */
    public String faxnumber;

    /**
     * E-Mail address.
     */
    public String email;

    /**
     * Comment.
     */
    public String comment;

    /**
     * URL.
     */
    public String url;

    /**
     * GPS data.
     */
    public String gpsdata;

    /**
     * Room number.
     */
    public String roomnumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrgunit() {
        return orgunit;
    }

    public void setOrgunit(Integer orgunit) {
        this.orgunit = orgunit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactperson() {
        return contactperson;
    }

    public void setContactperson(String contactperson) {
        this.contactperson = contactperson;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCallnumber() {
        return callnumber;
    }

    public void setCallnumber(String callnumber) {
        this.callnumber = callnumber;
    }

    public String getFaxnumber() {
        return faxnumber;
    }

    public void setFaxnumber(String faxnumber) {
        this.faxnumber = faxnumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGpsdata() {
        return gpsdata;
    }

    public void setGpsdata(String gpsdata) {
        this.gpsdata = gpsdata;
    }

    public String getRoomnumber() {
        return roomnumber;
    }

    public void setRoomnumber(String roomnumber) {
        this.roomnumber = roomnumber;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void verify(final OctopusContext octopusContext) throws BeanException {
        final VerawebMessages messages = new VerawebMessages(octopusContext);

        if (name == null || name.trim().length() == 0) {
            addError(messages.getMessageLocationTitleEmpty());
        }

        if (comment != null && comment.length() > 1000) {
            addError(messages.getMessageLocationMaxRemarkReached());
        }
    }

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
     * Test ist, ob der Benutzer Teil-Administrator ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     */
    @Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfigAA.GROUP_PARTIAL_ADMIN);
    }

    public int compareTo(Location location) {
        int result = 0;

        Collator collator = Collator.getInstance(java.util.Locale.GERMAN);
        collator.setStrength(Collator.SECONDARY);

        List<Integer> comparedList = new ArrayList<Integer>();

        comparedList.add(collator.compare(this.address == null ? "" : this.address,
          location.address == null ? "" : location.address));
        comparedList.add(collator.compare(this.callnumber == null ? "" : this.callnumber,
          location.callnumber == null ? "" : location.callnumber));
        comparedList.add(collator.compare(this.comment == null ? "" : this.comment,
          location.comment == null ? "" : location.comment));
        comparedList.add(collator.compare(this.contactperson == null ? "" : this.contactperson,
          location.contactperson == null ? "" : location.contactperson));
        comparedList.add(collator.compare(this.email == null ? "" : this.email,
          location.email == null ? "" : location.email));
        comparedList.add(collator.compare(this.faxnumber == null ? "" : this.faxnumber,
          location.faxnumber == null ? "" : location.faxnumber));
        comparedList.add(collator.compare(this.gpsdata == null ? "" : this.gpsdata,
          location.gpsdata == null ? "" : location.gpsdata));
        comparedList.add(collator.compare(this.location == null ? "" : this.location,
          location.location == null ? "" : location.location));
        comparedList.add(collator.compare(this.name == null ? "" : this.name,
          location.name == null ? "" : location.name));
        comparedList.add(collator.compare(this.roomnumber == null ? "" : this.roomnumber,
          location.roomnumber == null ? "" : location.roomnumber));
        //comparedList.add(collator.compare(this.title, location.title));
        comparedList.add(collator.compare(this.url == null ? "" : this.url,
          location.url == null ? "" : location.url));
        comparedList.add(collator.compare(this.zip == null ? "" : this.zip,
          location.zip == null ? "" : location.zip));

        if (comparedList.contains(1) || comparedList.contains(-1)) { //It contains 1 or -1 so it is not equal
            return 1; //For this implementation it does not matter what value returns. Can be changed for future reasons.
        }

        return result;
    }
}
