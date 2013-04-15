package de.tarent.aa.veraweb.db.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entiy 'Orgunit'.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * 
 */
@Entity
@Table(name = "torgunit")
public class Orgunit implements Serializable {

    /**
     * Generated Serial id.
     */
    private static final long serialVersionUID = 8509335974597454299L;

    /**
     * Primary key (sequential number).
     */
    @Id
    @GeneratedValue(generator = "orgunitSeqGen")
    @SequenceGenerator(name = "orgunitSeqGen", sequenceName = "torgunit_pk_seq", allocationSize = 1)
    @Column(name = "pk")
    private Long id;

    /**
     * Unit name.
     */
    @Column(name = "unitname")
    private String unitname;
    
    @OneToMany(mappedBy = "orgunit", fetch = FetchType.LAZY)
    private Set<User> users;

    /**
     * Default constructor.
     */
    public Orgunit() {
        users = new HashSet<User>();
    }

    /**
     * Get id.
     * 
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set id.
     * 
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get unit name.
     * 
     * @return the unitname
     */
    public String getUnitname() {
        return unitname;
    }

    /**
     * Set unit name.
     * 
     * @param unitname
     *            the unitname to set
     */
    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    /**
     * Get users.
     * 
     * @return the users
     */
    public Set<User> getUsers() {
        return users;
    }

    /**
     * Set users.
     * 
     * @param users
     *            the users to set
     */
    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
