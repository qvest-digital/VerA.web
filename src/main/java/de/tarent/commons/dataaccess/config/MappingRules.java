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

package de.tarent.commons.dataaccess.config;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MappingRules {
	public static final String DIRECTION_BOTH = "both";
	public static final String DIRECTION_LOAD = "load";
	public static final String DIRECTION_SAVE = "save";

	public static final boolean DROP_TRUE = true;
	public static final boolean DROP_FALSE = false;

	private List rules = new LinkedList();

	public void addRules(MappingRules mappingRules) {
		rules.addAll(mappingRules.rules);
	}

	public void addRule(Rule rule) {
		rules.add(rule);
	}

	public void addRenameRule(String resultAttribute, String backendAttribute) {
		rules.add(rename(resultAttribute, backendAttribute, DIRECTION_BOTH, DROP_TRUE));
	}

	public void addRenameRule(String resultAttribute, String backendAttribute, String direction, boolean drop) {
		rules.add(rename(resultAttribute, backendAttribute, direction, drop));
	}

	public void addIgnoreRule(String resultAttribute, String backendAttribute) {
		rules.add(ignore(resultAttribute, backendAttribute, DIRECTION_BOTH, DROP_TRUE));
	}

	public void addIgnoreRule(String resultAttribute, String backendAttribute, String direction, boolean drop) {
		rules.add(ignore(resultAttribute, backendAttribute, direction, drop));
	}

	public List getRules() {
		return rules;
	}
	
	public String transformBackendToResult(String attribute) {
		for (Iterator it = rules.iterator(); it.hasNext(); ) {
			Rule rule = (Rule) it.next();
			if (rule.matchLoading(attribute)) {
				attribute = rule.transformBackendToResult(attribute);
				if (rule.isLastRule())
					return attribute;
			}
		}
		return attribute;
	}

	public String transformResultToBackend(String attribute) {
		for (Iterator it = rules.iterator(); it.hasNext(); ) {
			Rule rule = (Rule) it.next();
			if (rule.matchSaving(attribute)) {
				attribute = rule.transformResultToBackend(attribute);
				if (rule.isLastRule())
					return attribute;
			}
		}
		return attribute;
	}

	public String toString() {
		return getClass().getSimpleName() + " " + rules;
	}

	public static Rule rename(String resultAttribute, String backendAttribute, String direction, boolean drop) {
		return new Rename(resultAttribute, backendAttribute, direction, drop);
	}

	public static Rule ignore(String resultAttribute, String backendAttribute, String direction, boolean drop) {
		return new Ignore(resultAttribute, backendAttribute, direction, drop);
	}
	
	public static abstract class Rule {
		protected String result;
		protected String resultPattern;
		protected String backend;
		protected String backendPattern;
		protected String direction;
		protected boolean drop;

		private Rule(String result, String backend, String direction, boolean drop) {
			this.result = result;
			this.resultPattern = result.replaceAll("\\*", ".*");
			this.backend = backend;
			this.backendPattern = backend.replaceAll("\\*", ".*");
			if (direction.equals(DIRECTION_BOTH))
				this.direction = DIRECTION_BOTH;
			else if (direction.equals(DIRECTION_LOAD))
				this.direction = DIRECTION_LOAD;
			else if (direction.equals(DIRECTION_SAVE))
				this.direction = DIRECTION_SAVE;
			else
				throw new IllegalArgumentException("Illegal direction: " + direction);
			this.drop = drop;
		}

		public String getResult() {
			return result;
		}

		public String getBackend() {
			return backend;
		}

		public String getDirection() {
			return direction;
		}

		public boolean isLastRule() {
			return drop;
		}

		public boolean matchLoading(String attribute) {
			return attribute != null && (
					attribute.equals(backend) ||
					attribute.matches(backendPattern));
		}

		public boolean matchSaving(String attribute) {
			return attribute != null && (
					attribute.equals(result) ||
					attribute.matches(resultPattern));
		}

		public abstract String transformBackendToResult(String attribute);

		public abstract String transformResultToBackend(String attribute);
		
		public String toString() {
			return getClass().getSimpleName() + "-Rule [" +
					"direction=" + direction + "; " +
					"result=" + result + "; " +
					"resultPattern=" + resultPattern + "; " +
					"backend=" + backend + "; " +
					"backendPattern=" + backendPattern + "]";
		}
	}

	public static class Rename extends Rule {
		private Rename(String result, String backend, String direction, boolean drop) {
			super(result, backend, direction, drop);
		}
		
		public String transformBackendToResult(String attribute) {
			// TODO regex rules
			return result;
		}
		
		public String transformResultToBackend(String attribute) {
			// TODO regex rules
			return backend;
		}
	}

	public static class Ignore extends Rule {
		private Ignore(String result, String backend, String direction, boolean drop) {
			super(result, backend, direction, drop);
		}
		
		public String transformBackendToResult(String attribute) {
			return null;
		}
		
		public String transformResultToBackend(String attribute) {
			return null;
		}
	}
}
