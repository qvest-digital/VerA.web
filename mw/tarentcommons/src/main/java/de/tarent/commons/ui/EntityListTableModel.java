/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.ui;

import de.tarent.commons.datahandling.entity.AsyncEntityListImpl;
import de.tarent.commons.datahandling.entity.Entity;
import de.tarent.commons.datahandling.entity.EntityListEvent;
import de.tarent.commons.datahandling.entity.EntityListListener;
import de.tarent.commons.logging.LogFactory;

import java.awt.EventQueue;
import java.util.*;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

import org.apache.commons.logging.Log;

import de.tarent.commons.utils.Pojo;

/**
 * Table model for an Asynchronous Entity List
 */
public class EntityListTableModel
    implements TableModel, EntityListListener {

    private static final Log logger = LogFactory.getLog(EntityListTableModel.class);

    List tml = new ArrayList();
    AsyncEntityListImpl list;
    ColumnDescription[] columnDescriptions = null;

    public EntityListTableModel(AsyncEntityListImpl list, ColumnDescription[] columnDescriptions) {
	this.list = list;
	this.columnDescriptions = columnDescriptions;
	list.addEntityListListener(this);
    }

    public void setColumnDescriptions(ColumnDescription[] columnDescriptions) {
	this.columnDescriptions = columnDescriptions;
	fireTableModelListenerEventLater(new TableModelEvent(this, 0, Integer.MAX_VALUE, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    public void entityListChanged(EntityListEvent e) {
	fireTableModelListenerEventLater(new TableModelEvent(this, 0, Integer.MAX_VALUE, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
	// using the exact rows is cleaner, but produces revalidation problem in the tarent-contact address table
	fireTableModelListenerEventLater(new TableModelEvent(this, e.getFirstRow(), e.getLastRow(), TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    protected void fireTableModelListenerEventLater(final TableModelEvent e) {
	EventQueue.invokeLater(new Runnable() {
		public void run() {
		    for (int i = 0; i < tml.size(); i++) {
			TableModelListener t = (TableModelListener)tml.get(i);
			t.tableChanged(e);
		    }
		}
	    });
    }

    public void addTableModelListener(TableModelListener l) {
	tml.add(l);
    }

    public void	removeTableModelListener(TableModelListener l) {
	tml.remove(l);
    }

    public Class getColumnClass(int columnIndex) {
	return columnDescriptions[columnIndex].getType();
    }

    public int getColumnCount() {
	return columnDescriptions.length;
    }

    public String getColumnName(int columnIndex) {
	return columnDescriptions[columnIndex].getTitle();
    }

    public int getRowCount() {
	return list.getSize();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
	Object entityO = list.getEntityAt(rowIndex);
	if (entityO == null)
	    return "...";

	try {
	    if (entityO instanceof Entity) {
		Entity entity = (Entity)entityO;
		return entity.getAttribute(columnDescriptions[columnIndex].getAttributeName());
	    } else {
		return Pojo.get(entityO, columnDescriptions[columnIndex].getAttributeName());
	    }
	} catch (Exception e) {
	    logger.error("Error while accessing entity attribute", e);
	    return null;
	}
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
	return false;
    }

    public void	setValueAt(Object aValue, int rowIndex, int columnIndex) {
	throw new RuntimeException("Operation not supported");
    }

    public int getRowOf(Object object){
	if(tml.contains(object))
		return tml.indexOf(object);
	return -1;
    }

}
