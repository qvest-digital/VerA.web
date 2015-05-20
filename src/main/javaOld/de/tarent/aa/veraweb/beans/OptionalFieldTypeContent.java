package de.tarent.aa.veraweb.beans;

/**
 * Entity to represent the table <code>veraweb.toptional_field_type_content</code>
 *
 * @author jnunez
 */
public class OptionalFieldTypeContent extends AbstractHistoryBean {

	/* pk */
	public Integer id;
	/* fk_optional_field */
	public Integer fk_optional_field;
	/* content */
	public String content;
	
	public OptionalFieldTypeContent() {
	}
	
	public OptionalFieldTypeContent(Integer fk_optional_field, String content) {
		super();
		this.fk_optional_field = fk_optional_field;
		this.content = content;
	}

	public OptionalFieldTypeContent(Integer id, Integer fk_optional_field,
			String content) {
		super();
		this.id = id;
		this.fk_optional_field = fk_optional_field;
		this.content = content;
	}

	public Integer getId() {
		return id;
	}

	public Integer getFk_optional_field() {
		return fk_optional_field;
	}
	
	public void setFk_optional_field(Integer fk_optional_field) {
		this.fk_optional_field = fk_optional_field;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
