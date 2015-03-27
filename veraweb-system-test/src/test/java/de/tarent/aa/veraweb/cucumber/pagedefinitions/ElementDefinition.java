/**
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
package de.tarent.aa.veraweb.cucumber.pagedefinitions;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ByIdOrName;

/**
 * Class to define a page element.
 *
 * @author Michael Kutz, tarent Solutions GmbH
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 *
 */
public class ElementDefinition {

    /**
     * A name for the element that may be used in cucumber feature definitions. Will be used to resolve the element by
     * {@link PageDefinition#elementForName(String)}.
     */
    public final String name;

    /**
     * The {@link By} criteria to find the element.
     */
    public final By by;

    /**
     * True if the element is required to be present on its page.
     */
    public final boolean required;

    /**
     * The {@link HtmlType} of the element.
     */
    public final HtmlType type;

    /**
     * The next page definition.
     */
    public PageDefinition nextPageDefinition;

    /**
     * Standard constructor.
     *
     * @param name
     *            the {@link #name} {@link String}.
     * @param idOrName
     *            the elemet's ID or name attribute value. Will be used to create the element's {@link #by} object to
     *            find the actual element on an actual page.
     * @param required
     *            the {@link #required} {@link Boolean}.
     * @param type
     *            the {@link #type} {@link HtmlType}.
     */
    public ElementDefinition(final String name, final String idOrName, final boolean required, final HtmlType type,
            final PageDefinition nextPageDefinition) {
        this.name = name;
        this.by = new ByIdOrName(idOrName);
        this.required = required;
        this.type = type;
        this.nextPageDefinition = nextPageDefinition;
    }

    /**
     * Convenience constructor (sets {@link #required} to {@code true} and {@link #type} to {@code null}).
     *
     * @param name
     *            the {@link #name} {@link String}.
     * @param idOrName
     *            the elemet's ID or name attribute value. Will be used to create the element's {@link #by} object to
     *            find the actual element on an actual page.
     * @param required
     *            the {@link #required} {@link Boolean}.
     */
    public ElementDefinition(final String name, final String idOrName, final PageDefinition nextPageDefinition) {
        this(name, idOrName, true, null, nextPageDefinition);
    }

    /**
     * Convenience constructor (sets {@link #type} to {@code null}).
     *
     * @param name
     *            the {@link #name} {@link String}.
     * @param idOrName
     *            the elemet's ID or name attribute value. Will be used to create the element's {@link #by} object to
     *            find the actual element on an actual page.
     * @param required
     *            the {@link #required} {@link Boolean}.
     */
    public ElementDefinition(final String name, final String idOrName, final boolean required) {
        this(name, idOrName, required, null, null);
    }

    /**
     * Convenience constructor (sets {@link #required} to {@code true}).
     *
     * @param name
     *            the {@link #name} {@link String}.
     * @param idOrName
     *            the elemet's ID or name attribute value. Will be used to create the element's {@link #by} object to
     *            find the actual element on an actual page.
     * @param type
     *            the {@link #type} {@link HtmlType}.
     */
    public ElementDefinition(final String name, final String idOrName, final HtmlType type) {
        this(name, idOrName, true, type, null);
    }

    /**
     * Convenience constructor (sets {@link #required} to {@code true} and {@link #type} to {@code null} and
     * {@link #nextPageDefinition} to {@code null}).
     *
     * @param name
     *            the {@link #name} {@link String}.
     * @param idOrName
     *            the elemet's ID or name attribute value. Will be used to create the element's {@link #by} object to
     *            find the actual element on an actual page.
     */
    public ElementDefinition(final String name, final String idOrName) {
        this(name, idOrName, true, null, null);
    }

    @Override
    public String toString() {
        return "ElementDefinition [name=" + name + ", by=" + by + ", required=" + required + "]";
    }
}
