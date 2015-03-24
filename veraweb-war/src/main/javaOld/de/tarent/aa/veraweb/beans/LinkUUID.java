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
	
	
	
}
