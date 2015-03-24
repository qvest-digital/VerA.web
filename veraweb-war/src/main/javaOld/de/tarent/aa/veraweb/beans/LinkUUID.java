package de.tarent.aa.veraweb.beans;

/**
 * Bean of the table <code>veraweb.link_uuid</code>
 * @author jnunez
 *
 */
public class LinkUUID  extends AbstractHistoryBean {

	private Integer id;
	private String uuid;
	private LinkType linkType;
	private Integer personid;
	
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

	public LinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(LinkType linkType) {
		this.linkType = linkType;
	}

	public Integer getPersonid() {
		return personid;
	}

	public void setPersonid(Integer personid) {
		this.personid = personid;
	}
}
