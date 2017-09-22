package org.evolvis.veraweb.onlinereg.utils;

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
import java.io.IOException;
import java.io.Writer;

public class KeepOpenWriter extends Writer {
    
    private final Writer delegatee;

    public KeepOpenWriter(Writer delegatee) {
        this.delegatee = delegatee;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        delegatee.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        delegatee.flush();
    }

    @Override
    public void close() throws IOException {
        delegatee.flush();
    }

}
