package de.tarent.aa.veraweb.cucumber.pagedefinitions;

public enum HtmlType {

    SPAN("span"), SELECT("select"), TEXTAREA("textarea"), CHECKBOX("checkbox"), INPUT("input"), RADIO("radio");
    
    public String name;
    
    private HtmlType(String name) {
        this.name = name;
    }
}
