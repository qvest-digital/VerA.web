package de.tarent.aa.veraweb.beans;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OptionalDelegationField extends AbstractHistoryBean {
    private int fkGuest;
    private int fkDelegationField;
    private String label;
    private Integer fkType;
    private String content;
    private List<OptionalFieldTypeContent> optionalFieldTypeContents;

    public OptionalDelegationField(ResultSet resultSet) throws SQLException {
        this.fkGuest = resultSet.getInt("fk_guest");
        this.fkDelegationField = resultSet.getInt("fk_delegation_field");
        this.label = resultSet.getString("label");
        this.fkType = resultSet.getInt("fk_type");
        this.content = resultSet.getString("value");
    }

    public OptionalDelegationField() {

    }

    public int getFkGuest() {
        return fkGuest;
    }

    public void setFk_guest(int fkGuest) {
        this.fkGuest = fkGuest;
    }

    public int getFkDelegationField() {
        return fkDelegationField;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getFkType() {
        return fkType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFkDelegationField(int fkDelegationField) {
        this.fkDelegationField = fkDelegationField;
    }

    public void setFkType(Integer fkType) {
        this.fkType = fkType;
    }

    public void setOptionalFieldTypeContents(List<OptionalFieldTypeContent> optionalFieldTypeContents) {
        this.optionalFieldTypeContents = optionalFieldTypeContents;
    }

    public List<OptionalFieldTypeContent> getOptionalFieldTypeContents() {
        return optionalFieldTypeContents;
    }

    public boolean equals(OptionalDelegationField optionalDelegationField) {
        return this.getFkDelegationField() == optionalDelegationField.getFkDelegationField() &&
          this.getFkType().intValue() == 3 &&
          optionalDelegationField.getFkType().intValue() == 3;
    }
}
