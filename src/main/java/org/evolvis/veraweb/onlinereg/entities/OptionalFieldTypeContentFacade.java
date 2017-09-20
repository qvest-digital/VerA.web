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
