package de.tarent.aa.veraweb.worker;

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
public class JumpOffset {

    final private String letter;
    final private int offset;
    final private int targetPageOffset;
    final private int currentPageOffset;

    public JumpOffset(String letter, Integer offset, Integer currentPageOffset, Integer itemsPerPage) {
        this.letter = letter;
        this.offset = offset;
        this.currentPageOffset = currentPageOffset;
        this.targetPageOffset = (offset / itemsPerPage) * itemsPerPage;
        
    }

    public String getLetter() {
        return letter;
    }

    public Integer getOffset() {
        return offset;
    }
    
    public Integer getTargetPageOffset(){
        return targetPageOffset;   
    }
    
    public boolean isTargetOnCurrentPage(){
        return targetPageOffset == currentPageOffset;
    }

}
