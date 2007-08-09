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

package de.tarent.commons.dataaccess.query;

public abstract class FilterSummery {
	private FilterSummery() {
		// Use subclasses instead.
	}

	abstract public boolean addMatchResult(boolean match);
	abstract public boolean getMatchResult();

	/** <code>AND</code> or "all of them" */
	public static class AND extends FilterSummery {
		private boolean match = true;
		
		public boolean addMatchResult(boolean match) {
			if (!match)
				this.match = false;
			return getMatchResult();
		}
		
		public boolean getMatchResult() {
			return match;
		}
	}

	/** <code>OR</code> or "one of them" */
	public static class OR extends FilterSummery {
		private boolean match = false;
		
		public boolean addMatchResult(boolean match) {
			if (match)
				this.match = true;
			return getMatchResult();
		}
		
		public boolean getMatchResult() {
			return match;
		}
	}

	/** <code>NOT</code> or "none of them" */
	public static class NOT extends FilterSummery {
		private boolean match = true;
		
		public boolean addMatchResult(boolean match) {
			if (match)
				this.match = false;
			return getMatchResult();
		}
		
		public boolean getMatchResult() {
			return match;
		}
	}

	/** <code>XOR</code> or "xor of them" */
	public static class XOR extends FilterSummery {
		private boolean match = true;

		public boolean addMatchResult(boolean match) {
			this.match ^= match;
			return getMatchResult();
		}

		public boolean getMatchResult() {
			return match;
		}
	}
}
