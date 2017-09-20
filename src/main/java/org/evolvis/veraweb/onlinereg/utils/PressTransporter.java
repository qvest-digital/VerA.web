package org.evolvis.veraweb.onlinereg.utils;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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
	private String username;
	
	/**
	 * Empty constructor
	 */
	public PressTransporter() {
	}

	/**
	 * Custom constructor
	 * @param uuid FIXME
	 * @param nachname Last name
	 * @param vorname First name
	 * @param gender Gender
	 * @param email Email
	 * @param address Address
	 * @param plz Zip code
	 * @param city City
	 * @param country Country
	 * @param username Username
	 */
	public PressTransporter(String uuid, String nachname, String vorname,
			String gender, String email, String address, String plz,
			String city, String country, String username) {
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
		this.username = username;
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
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
}
