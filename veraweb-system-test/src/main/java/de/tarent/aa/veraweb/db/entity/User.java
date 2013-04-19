package de.tarent.aa.veraweb.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity 'User'.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * 
 */
@Entity
@Table(name = "tuser")
public class User extends AbstractEntity {

    public static final String DEFAULT_VERAWEB_ADMIN = "verawebadmin";

    /**
     * Generated Serial id.
     */
    private static final long serialVersionUID = -4021558560545704298L;

    /**
     * Primary key (sequential number).
     */
    @Id
    @SequenceGenerator(name = "userSeqGen", sequenceName = "tuser_pk_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSeqGen")
    @Column(name = "pk")
    private Long id;

    /**
     * Username.
     */
    @Column(name = "username")
    private String username;

    /**
     * Related role.
     */
    @Column(name = "role")
    private Integer role;

    /**
     * User's assigned organization unit.
     */
    @ManyToOne()
    @JoinColumn(name = "fk_orgunit", referencedColumnName = "pk")
    private Orgunit orgunit;

    /**
     * Default constructor.
     */
    public User() {
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
     * Get username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set username.
     * 
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get role.
     * 
     * @return the role
     */
    public Integer getRole() {
        return role;
    }

    /**
     * Set role.
     * 
     * @param role
     *            the role to set
     */
    public void setRole(Integer role) {
        this.role = role;
    }

    /**
     * Get organization unit.
     * 
     * @return the orgunit
     */
    public Orgunit getOrgunit() {
        return orgunit;
    }

    /**
     * Set organization unit.
     * 
     * @param orgunit
     *            the organization unit to set
     */
    public void setOrgunit(Orgunit orgunit) {
        this.orgunit = orgunit;
    }

}
