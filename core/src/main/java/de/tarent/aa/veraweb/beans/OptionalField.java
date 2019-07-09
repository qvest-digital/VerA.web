package de.tarent.aa.veraweb.beans;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Dieses Bean bildet einen Eintrag der Tabelle <em>veraweb.toptional_fields</em> ab.
 *
 * @author Max Marche, m.marche@tarent.de
 */
public class OptionalField extends AbstractHistoryBean {
    private int id;
    private String label;
    private int fkEvent;
    /* references to OptionalFieldType */
    private Integer fkType;
    private String content;

    public OptionalField() {
        this.label = "";
        this.fkEvent = -1;
    }

    public OptionalField(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("pk");
        this.label = resultSet.getString("label");
        this.fkEvent = resultSet.getInt("fk_event");
        this.fkType = resultSet.getInt("fk_type");
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getFkEvent() {
        return fkEvent;
    }

    public void setFkEvent(int fkEvent) {
        this.fkEvent = fkEvent;
    }

    public Integer getFkType() {
        return fkType;
    }

    public void setFkType(Integer fkType) {
        this.fkType = fkType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
