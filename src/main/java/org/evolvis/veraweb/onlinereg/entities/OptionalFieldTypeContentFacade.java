package org.evolvis.veraweb.onlinereg.entities;

/**
 * This Class is created to include the property isSelected for the
 * OptionalFieldTypeContent Object i.e: possible selections.
 */
public class OptionalFieldTypeContentFacade extends OptionalFieldTypeContent {
	
	private Boolean isSelected = false;

	public OptionalFieldTypeContentFacade () {
	}
	
	public OptionalFieldTypeContentFacade (OptionalFieldTypeContent optionalFieldTypeContent) {
		this.setPk(optionalFieldTypeContent.getPk());
		this.setContent(optionalFieldTypeContent.getContent());
		this.setFk_optional_field(optionalFieldTypeContent.getFk_optional_field());
	}
	
	public Boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}

	
}
