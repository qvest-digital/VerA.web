package de.tarent.aa.veraweb.beans;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
 * Entity to represent the table <code>veraweb.toptional_field_type_content</code>
 *
 * @author jnunez
 */
public class OptionalFieldTypeContent extends AbstractHistoryBean {


	/* pk */
	private Integer id;
	/* fk_optional_field */
	private Integer fk_optional_field;
	/* content */
	private String content;

	private Boolean isSelected;

	public OptionalFieldTypeContent() {
	}

	public OptionalFieldTypeContent(Integer fk_optional_field, String content) {
		super();
		this.fk_optional_field = fk_optional_field;
		this.content = content;
	}

	public OptionalFieldTypeContent(Integer id, Integer fk_optional_field,
			String content) {
		super();
		this.id = id;
		this.fk_optional_field = fk_optional_field;
		this.content = content;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFk_optional_field() {
		return fk_optional_field;
	}

	public void setFk_optional_field(Integer fk_optional_field) {
		this.fk_optional_field = fk_optional_field;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Boolean getIsSelected() {
	    return isSelected;
    }

	public void setIsSelected(Boolean isSelected) {
	    this.isSelected = isSelected;
    }

}
