package org.evolvis.veraweb.onlinereg.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Max Marche <m.marche@tarent.de> on 30.12.14.
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tguest_doctype")
@NamedQueries(value = {
        @NamedQuery(name = "GuestDoctype.findByDoctypeIdAndGuestId", query = "SELECT gd FROM GuestDoctype gd "
        		+ "WHERE fk_guest=:fk_guest AND fk_doctype=:fk_doctype")
})

public class GuestDoctype {

	/**
	 * Constructor with all required fields
	 * @param fk_guest
	 * @param fk_doctype
	 * @param addresstype
	 * @param locale
	 */
	public GuestDoctype(int fk_guest, int fk_doctype, int addresstype, int locale) {
		this.fk_guest = fk_guest;
		this.fk_doctype = fk_doctype;
		this.addresstype = addresstype;
		this.locale = locale;
		this.lastname = "";
		this.firstname = "";
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pk;
    private int fk_guest;
    private int fk_doctype;
    private int addresstype;
    private int locale;
    
    private String textfield; //:firstname :lastname
    private String textfield_p;
    private String textjoin;
    private String salutation;
    private String function;
    private String titel;
    private String firstname; //:firstname
    private String lastname; //:lastname
    private String zipcode;
    private String state;
    private String city;
    private String street;
    private String country;
    private String suffix1;
    private String suffix2;
    private String salutation_p;
    private String titel_p;
    private String firstname_p;
    private String lastname_p;
    private String fon;
    private String fax;
    private String mail;
    private String www;
    private String mobil;
    private String company;
    private String pobox;
    private String poboxzipcode;
    
    
    public int getFk_guest() {
		return fk_guest;
	}
    public int getFk_doctype() {
		return fk_doctype;
	}
    
    /**
     * fill also the "textfield" value
     * @param firstname
     */
    public void setFirstname(String firstname) {
    	this.textfield = firstname + " " + this.lastname;
		this.firstname = firstname;
	}
    /**
     * fill also the "textfield" value
     * @param lastname
     */
    public void setLastname(String lastname) {
    	this.textfield = this.firstname + " " + lastname;
		this.lastname = lastname;
	}
}
