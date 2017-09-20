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
import java.util.Date;

/**
 * Class used to transport data between api and angular - the check "isRegistered" is crucial inside the open events site
 * @author jnunez
 *
 */
public class EventTransporter {

	private Integer pk;
    private String shortname;
    private Date datebegin;
    private Date dateend;

	private Boolean isRegistered;
    private Integer status;
    private String message;
	private String hash;

    /** Empty constructor */
	public EventTransporter() {}

	/**
	 * Custom constructor.
	 *
	 * @param pk FIXME
	 * @param shortname FIXME
	 * @param datebegin Begin date
	 * @param dateend End date
	 * @param isRegistered FIXME
	 * @param hash FIXME
	 */
	public EventTransporter(Integer pk, String shortname, Date datebegin, Date dateend,
			Boolean isRegistered, String hash) {
		super();
		this.pk = pk;
		this.shortname = shortname;
		this.datebegin = datebegin;
		this.dateend = dateend;
		this.isRegistered = isRegistered;
		this.hash = hash;
	}

	public Integer getPk() {
		return pk;
	}

	public void setPk(Integer pk) {
		this.pk = pk;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public Date getDatebegin() {
		return datebegin;
	}

	public void setDatebegin(Date datebegin) {
		this.datebegin = datebegin;
	}

	public Boolean getIsRegistered() {
		return isRegistered;
	}

	public void setIsRegistered(Boolean isRegistered) {
		this.isRegistered = isRegistered;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getDateend() {
		return dateend;
	}

	public void setDateend(Date dateend) {
		this.dateend = dateend;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

}
