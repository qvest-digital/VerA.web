package org.evolvis.veraweb.onlinereg.utils;

import java.util.Date;

/**
 * Class used to transport data between api and angular 
 * -> the check "isRegistered" is crucial inside the open events site
 * @author jnunez
 *
 */
public class EventTransporter {

	private Integer pk;
    private String shortname;
    private Date datebegin;
    private Boolean isRegistered;
    private Integer status;
    private String message;

    /** Empty constructor */
	public EventTransporter() {}
	
	/**
	 * Constructor with parameters
	 * 
	 * @param pk Integer
	 * @param shortname String
	 * @param datebegin Date
	 * @param isRegistered Boolean
	 */
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

}
