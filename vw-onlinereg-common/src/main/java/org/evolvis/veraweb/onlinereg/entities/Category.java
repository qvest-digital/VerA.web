package org.evolvis.veraweb.onlinereg.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * @author jnunez
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tcategorie")
@NamedNativeQueries({
	@NamedNativeQuery(name = "Category.findIdByCatname", query = "SELECT c.pk FROM tcategorie c where catname=:pcatname and fk_orgunit=(SELECT fk_orgunit from tevent where mediarepresentatives=:uuid)")
})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pk;
    private String catname;
    
	public int getPk() {
		return pk;
	}
	public void setPk(int pk) {
		this.pk = pk;
	}
	public String getCatname() {
		return catname;
	}
	public void setCatname(String catname) {
		this.catname = catname;
	}
    
}
