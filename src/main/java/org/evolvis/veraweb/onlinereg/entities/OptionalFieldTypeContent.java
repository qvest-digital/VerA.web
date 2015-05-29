package org.evolvis.veraweb.onlinereg.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * Class which represents the table toptional_field_type_content
 * 
 * @author jnunez
 */
@Data
@XmlRootElement
@Entity
@Table(name = "toptional_field_type_content")
@NamedQueries({
    @NamedQuery(name = "OptionalFieldTypeContent.findTypeContentsByOptionalField", 
    			query = "SELECT oftc from OptionalFieldTypeContent oftc WHERE oftc.fk_optional_field=:optionalFieldId AND ((oftc.content not like '') AND oftc.content is not null)")
})
public class OptionalFieldTypeContent {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pk;
	
	private Integer fk_optional_field;
	
	private String content;
	
	public int getPk() {
		return pk;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Integer getFk_optional_field() {
		return fk_optional_field;
	}
	
	public void setFk_optional_field(Integer fk_optional_field) {
		this.fk_optional_field = fk_optional_field;
	}
}
