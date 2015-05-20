package de.tarent.aa.veraweb.beans;

/**
 * Entity to represent the table <code>veraweb.toptional_field_type_content</code>
 *
 * @author jnunez
 */
public class OptionalFieldTypeContent extends AbstractHistoryBean {

	/* pk */
	private Integer id;
	/* fk_optional_field */
	private Integer fkOptionalField;
	/* content */
	private String content;
	
	public OptionalFieldTypeContent() {
	}
	
	public OptionalFieldTypeContent(Integer fkOptionalField, String content) {
		super();
		this.fkOptionalField = fkOptionalField;
		this.content = content;
	}

	public OptionalFieldTypeContent(Integer id, Integer fkOptionalField,
			String content) {
		super();
		this.id = id;
		this.fkOptionalField = fkOptionalField;
		this.content = content;
	}

	public Integer getId() {
		return id;
	}

	public Integer getFk_optional_field() {
		return fkOptionalField;
	}
	
	public void setFk_optional_field(Integer fkOptionalField) {
		this.fkOptionalField = fkOptionalField;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
