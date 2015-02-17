package org.evolvis.veraweb.onlinereg.utils;

import java.util.Date;

public class EventTransporter {

	private Integer pk;
    private String shortname;
    private Date datebegin;
    private Boolean isRegistered;

	public EventTransporter() {
	}
	
	public EventTransporter(Integer pk, String shortname, Date datebegin,
			Boolean isRegistered) {
		super();
		this.pk = pk;
		this.shortname = shortname;
		this.datebegin = datebegin;
		this.isRegistered = isRegistered;
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

}
