package de.tarent.commons.ui;

public class ColumnDescription {
    String attributeName;
    String title;
    Class type;

    public ColumnDescription(String attributeName, String title, Class type) {
        this.attributeName = attributeName;
        this.title = title;
        this.type = type;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class newType) {
        this.type = newType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String newAttributeName) {
        this.attributeName = newAttributeName;
    }
}
