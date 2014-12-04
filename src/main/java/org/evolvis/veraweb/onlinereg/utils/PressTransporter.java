package org.evolvis.veraweb.onlinereg.utils;

public class PressTransporter {

	private String uuid;
	private String nachname;
	private String vorname;
	private String gender;
	private String email;
	private String address;
	private String plz;
	private String city;
	private String country;
	
	/**
	 * Empty constructor
	 */
	public PressTransporter() {
	}

	/**
	 * Constructor with parameters 
	 */
	public PressTransporter(String uuid, String nachname, String vorname,
			String gender, String email, String address, String plz,
			String city, String country) {
		super();
		this.uuid = uuid;
		this.nachname = nachname;
		this.vorname = vorname;
		this.gender = gender;
		this.email = email;
		this.address = address;
		this.plz = plz;
		this.city = city;
		this.country = country;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
}
