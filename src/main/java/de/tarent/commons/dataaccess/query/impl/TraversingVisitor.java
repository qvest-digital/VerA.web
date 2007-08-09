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

package de.tarent.commons.dataaccess.query.impl;

import de.tarent.commons.dataaccess.query.AbstractQueryContext;
import de.tarent.commons.dataaccess.query.AbstractVisitorListener;
import de.tarent.commons.dataaccess.query.QueryVisitor;
import de.tarent.commons.dataaccess.query.QueryVisitorListener;

public class TraversingVisitor extends AbstractQueryContext implements QueryVisitor {
	private QueryVisitorListener emptyVisitorListener;
	private QueryVisitorListener termVisitorListener;
	private QueryVisitorListener andVisitorListener;
	private QueryVisitorListener orVisitorListener;
	private QueryVisitorListener xorVisitorListener;
	private QueryVisitorListener notVisitorListener;

	public TraversingVisitor() {
		this(null, null, null, null, null, null);
	}

	public TraversingVisitor(
			QueryVisitorListener emptyVisitorListener,
			QueryVisitorListener termVisitorListener,
			QueryVisitorListener andVisitorListener,
			QueryVisitorListener orVisitorListener,
			QueryVisitorListener xorVisitorListener,
			QueryVisitorListener notVisitorListener) {
		setEmptyVisitorListener(emptyVisitorListener);
		setTermVisitorListener(termVisitorListener);
		setAndVisitorListener(andVisitorListener);
		setOrVisitorListener(orVisitorListener);
		setXorVisitorListener(orVisitorListener);
		setNotVisitorListener(notVisitorListener);
	}

	public void setEmptyVisitorListener(QueryVisitorListener emptyVisitorListener) {
		if (emptyVisitorListener != null)
			this.emptyVisitorListener = emptyVisitorListener;
		else
			this.emptyVisitorListener = new AbstractVisitorListener();
	}

	public void setTermVisitorListener(QueryVisitorListener termVisitorListener) {
		if (termVisitorListener != null)
			this.termVisitorListener = termVisitorListener;
		else
			this.termVisitorListener = new AbstractVisitorListener();
	}

	public void setAndVisitorListener(QueryVisitorListener andVisitorListener) {
		if (andVisitorListener != null)
			this.andVisitorListener = andVisitorListener;
		else
			this.andVisitorListener = new AbstractVisitorListener();
	}

	public void setOrVisitorListener(QueryVisitorListener orVisitorListener) {
		if (orVisitorListener != null)
			this.orVisitorListener = orVisitorListener;
		else
			this.orVisitorListener = new AbstractVisitorListener();
	}

	public void setXorVisitorListener(QueryVisitorListener xorVisitorListener) {
		if (orVisitorListener != null)
			this.xorVisitorListener = xorVisitorListener;
		else
			this.xorVisitorListener = new AbstractVisitorListener();
	}

	public void setNotVisitorListener(QueryVisitorListener notVisitorListener) {
		if (notVisitorListener != null)
			this.notVisitorListener = notVisitorListener;
		else
			this.notVisitorListener = new AbstractVisitorListener();
	}

	public void empty() {
		emptyVisitorListener.handleBeforeAll(0);
		emptyVisitorListener.handleBeforeEntry(false, false);
//		queryParser.empty();
		emptyVisitorListener.handleAfterEntry(false, false);
		emptyVisitorListener.handleAfterAll(0);
	}

	public void term(Object object) {
		termVisitorListener.handleBeforeAll(1);
		termVisitorListener.handleBeforeEntry(true, true);
//		queryParser.term(object);
		termVisitorListener.handleAfterEntry(true, true);
		termVisitorListener.handleAfterAll(1);
	}

	public void and(Object[] objects) {
		andVisitorListener.handleBeforeAll(objects.length);
		for (int i = 0; i < objects.length; i++) {
			andVisitorListener.handleBeforeEntry(i == 0, i + 1 == objects.length);
			getQueryParser().parse(objects[i]);
			andVisitorListener.handleAfterEntry(i == 0, i + 1 == objects.length);
		}
		andVisitorListener.handleAfterAll(objects.length);
	}

	public void or(Object[] objects) {
		orVisitorListener.handleBeforeAll(objects.length);
		for (int i = 0; i < objects.length; i++) {
			orVisitorListener.handleBeforeEntry(i == 0, i + 1 == objects.length);
			getQueryParser().parse(objects[i]);
			orVisitorListener.handleAfterEntry(i == 0, i + 1 == objects.length);
		}
		orVisitorListener.handleAfterAll(objects.length);
	}

	public void xor(Object[] objects) {
		xorVisitorListener.handleBeforeAll(objects.length);
		for (int i = 0; i < objects.length; i++) {
			xorVisitorListener.handleBeforeEntry(i == 0, i + 1 == objects.length);
			getQueryParser().parse(objects[i]);
			xorVisitorListener.handleAfterEntry(i == 0, i + 1 == objects.length);
		}
		xorVisitorListener.handleAfterAll(objects.length);
	}

	public void not(Object[] objects) {
		notVisitorListener.handleBeforeAll(objects.length);
		for (int i = 0; i < objects.length; i++) {
			notVisitorListener.handleBeforeEntry(i == 0, i + 1 == objects.length);
			getQueryParser().parse(objects[i]);
			notVisitorListener.handleAfterEntry(i == 0, i + 1 == objects.length);
		}
		notVisitorListener.handleAfterAll(objects.length);
	}
}
