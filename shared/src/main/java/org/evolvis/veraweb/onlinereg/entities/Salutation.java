package org.evolvis.veraweb.onlinereg.entities;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Stefan Weiz, tarent solutions GmbH
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tsalutation")
@NamedNativeQueries(value = {
  @NamedNativeQuery(name = Salutation.GET_ALL_SALUTATIONS, query = "SELECT s.* FROM tsalutation s ",
    resultClass = Salutation.class),
  @NamedNativeQuery(name = Salutation.GET_SALUTATIONS_WITHOUT_ALTERNATIVE_CONTENT,
    query = "SELECT s.* FROM tsalutation s WHERE pk NOT IN (SELECT salutation_id FROM salutation_alternative WHERE " +
      "pdftemplate_id=:" +
      SalutationAlternative.PARAM_PDFTEMPLATE_ID + ")", resultClass = Salutation.class)
})

public class Salutation {
    public static final String GET_ALL_SALUTATIONS = "Salutation.getAllSalutations";
    public static final String GET_SALUTATIONS_WITHOUT_ALTERNATIVE_CONTENT = "Salutation.getSalutationsWithoutAlternativeContent";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pk;
    private String salutation;
    private char gender;

    public int getPk() {
        return this.pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getSalutation() {
        return this.salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public char getGender() {
        return this.gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }
}
