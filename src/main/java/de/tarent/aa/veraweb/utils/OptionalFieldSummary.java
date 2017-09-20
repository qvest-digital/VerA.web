package de.tarent.aa.veraweb.utils;

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
import de.tarent.aa.veraweb.beans.OptionalField;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to allow correct behaviour to show results from the optional fields site
 *
 * Created by Jon Nuñez, tarent solutions GmbH on 19.06.15.
 */
public class OptionalFieldSummary {

    public Integer totalCreatedFields = 0;
    public Integer totalChangedFields = 0;
    public Integer totalDeletedFields = 0;

    public List<OptionalField> createdFields = new ArrayList<OptionalField>();
    public List<OptionalField> changedFields = new ArrayList<OptionalField>();
    public List<OptionalField> deletedFields = new ArrayList<OptionalField>();

    public Integer getTotalCreatedFields() {
        if (this.createdFields != null) {
            return this.createdFields.size();
        }
        return 0;
    }

    public Integer getTotalChangedFields() {
        if (this.changedFields != null) {
            return this.changedFields.size();
        }
        return 0;
    }

    public Integer getTotalDeletedFields() {
        if (this.deletedFields != null) {
            return this.deletedFields.size();
        }
        return 0;
    }

    public void setTotalCreatedFields(Integer totalCreatedFields) {
        this.totalCreatedFields = totalCreatedFields;
    }

    public void setTotalChangedFields(Integer totalChangedFields) {
        this.totalChangedFields = totalChangedFields;
    }

    public void setTotalDeletedFields(Integer totalDeletedFields) {
        this.totalDeletedFields = totalDeletedFields;
    }

    public List<OptionalField> getCreatedFields() {
        return this.createdFields;
    }

    public void setCreatedFields(List<OptionalField> createdFields) {
        this.createdFields = createdFields;
    }

    public List<OptionalField> getChangedFields() {
        return this.changedFields;
    }

    public void setChangedFields(List<OptionalField> changedFields) {
        this.changedFields = changedFields;
    }

    public List<OptionalField> getDeletedFields() {
        return this.deletedFields;
    }

    public void setDeletedFields(List<OptionalField> deletedFields) {
        this.deletedFields = deletedFields;
    }

    /**
     * Automatic insertion into createdFields list
     *
     * @param newOptionalField new field
     */
    public void addCreatedOptionalField(OptionalField newOptionalField) {
        if (this.createdFields == null) {
            this.createdFields = new ArrayList<OptionalField>();
        }
        this.createdFields.add(newOptionalField);
    }

    /**
     * Automatic insertion into changedFields list
     *
     * @param changedOptionalField changed field
     */
    public void addChangedOptionalField(OptionalField changedOptionalField) {
        if (this.changedFields == null) {
            this.changedFields = new ArrayList<OptionalField>();
        }
        this.changedFields.add(changedOptionalField);
    }

    /**
     * Automatic insertion into deletedFields list
     *
     * @param deletedOptionalField deleted field
     */
    public void addDeletedOptionalField(OptionalField deletedOptionalField) {
        if (this.deletedFields == null) {
            this.deletedFields = new ArrayList<OptionalField>();
        }
        this.deletedFields.add(deletedOptionalField);
    }
}
