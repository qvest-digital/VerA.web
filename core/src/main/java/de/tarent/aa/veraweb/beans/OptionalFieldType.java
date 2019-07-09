package de.tarent.aa.veraweb.beans;
/**
 * Entity to represent the table <code>veraweb.toptional_field_type</code>
 *
 * @author jnunez
 */
public class OptionalFieldType extends AbstractHistoryBean {

    /* pk */
    private Integer id;
    /* description */
    private String description;

    public OptionalFieldType() {
    }

    public OptionalFieldType(String description) {
        this.description = description;
    }

    public OptionalFieldType(Integer id, String description) {
        super();
        this.id = id;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
