package org.evolvis.veraweb.onlinereg.entities;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
@Entity
@Table(name = "tperson_mailinglist")
@NamedQueries(value = {
  @NamedQuery(name = "PersonMailinglist.findByMailinglist",
    query = "SELECT p FROM PersonMailinglist p where mailinglistId = :listId"), })
public class PersonMailinglist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pk;
    @ManyToOne
    @JoinColumn(name = "fk_person", nullable = false)
    private Person person;
    @Column(name = "fk_mailinglist")
    private int mailinglistId;
    private String address;
}
