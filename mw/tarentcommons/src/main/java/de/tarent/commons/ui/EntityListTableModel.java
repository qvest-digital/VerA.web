package de.tarent.commons.ui;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
        fireTableModelListenerEventLater(
                new TableModelEvent(this, 0, Integer.MAX_VALUE, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    public void entityListChanged(EntityListEvent e) {
        fireTableModelListenerEventLater(
                new TableModelEvent(this, 0, Integer.MAX_VALUE, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
        // using the exact rows is cleaner, but produces revalidation problem in the tarent-contact address table
        fireTableModelListenerEventLater(
                new TableModelEvent(this, e.getFirstRow(), e.getLastRow(), TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    protected void fireTableModelListenerEventLater(final TableModelEvent e) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                for (int i = 0; i < tml.size(); i++) {
                    TableModelListener t = (TableModelListener) tml.get(i);
                    t.tableChanged(e);
                }
            }
        });
    }

    public void addTableModelListener(TableModelListener l) {
        tml.add(l);
    }

    public void removeTableModelListener(TableModelListener l) {
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
        if (entityO == null) {
            return "...";
        }

        try {
            if (entityO instanceof Entity) {
                Entity entity = (Entity) entityO;
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

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new RuntimeException("Operation not supported");
    }

    public int getRowOf(Object object) {
        if (tml.contains(object)) {
            return tml.indexOf(object);
        }
        return -1;
    }

}
