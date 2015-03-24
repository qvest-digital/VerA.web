package de.tarent.aa.veraweb.beans;

/**
 * Bean of the table <code>veraweb.link_uuid</code>
 * @author jnunez
 *
 */
public class LinkUUID  extends AbstractHistoryBean {

	/**
	 * Enum type to allow everytype which has a URL
	 * TODO (maybe) Extract to a different class to allow Delegations, Press and Freevisitors
	 */
	public static enum LinkType {
		/** code in DB: 0 */
		FREEVISITORS,
		/** code in DB: 1 */
		DELEGATION,
		/** code in DB: 2 */
		MEDIA,
		/** code in DB: 3 */
		PERSON
	}
	
	/** ID - Primary key */
	private Integer id;
	/** The UUID(hash) */
	private String uuid;
	/** Erstellt am */
	private LinkType link_type;
	/** Erstellt von */
	private Integer fk_person;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public LinkType getLink_type() {
		return link_type;
	}
	public void setLink_type(LinkType link_type) {
		this.link_type = link_type;
	}
	public Integer getFk_person() {
		return fk_person;
	}
	public void setFk_person(Integer fk_person) {
		this.fk_person = fk_person;
	}
	
	
	
	
	
}
