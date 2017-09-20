package org.evolvis.veraweb.onlinereg.entities;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Stefan Weiz, tarent solutions GmbH
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tsalutation")
@NamedNativeQueries(value={
        @NamedNativeQuery(name=Salutation.GET_ALL_SALUTATIONS, query = "SELECT s.* FROM tsalutation s ", resultClass=Salutation.class),
        @NamedNativeQuery(name=Salutation.GET_SALUTATIONS_WITHOUT_ALTERNATIVE_CONTENT, query = "SELECT s.* FROM tsalutation s WHERE pk NOT IN (SELECT salutation_id FROM salutation_alternative WHERE pdftemplate_id=:" + SalutationAlternative.PARAM_PDFTEMPLATE_ID + ")", resultClass=Salutation.class)
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
