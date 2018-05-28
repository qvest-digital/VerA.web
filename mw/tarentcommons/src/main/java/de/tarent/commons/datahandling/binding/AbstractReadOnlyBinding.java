package de.tarent.commons.datahandling.binding;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
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

/**
 * This abstract implementation of the {@link Binding}
 * interface eases the effort to implement a read-only
 * view connection to the model.
 *
 * <p>The only things you need to do is:
 * <ul>
 * <li>provide the model attribute key at instantiation time</li>
 * <li>implement {@link Binding#setViewData(Object)}</li>
 * </ul>
 *
 * @author Robert Schuster
 */
public abstract class AbstractReadOnlyBinding implements Binding {
    /**
     * In case subclasses want to access the attribute key they
     * do not need to use the {@link #getModelAttributeKey()} and
     * instead read this field instead.
     */
    protected final String modelAttributeKey;

    protected AbstractReadOnlyBinding(String modelAttributeKey) {
        assert (modelAttributeKey != null);

        this.modelAttributeKey = modelAttributeKey;
    }

    /**
     * As this is a read-only binding pushing a view
     * to the model is not needed.
     *
     * <p>The implementation returns <code>null</code>.</p>
     */
    public final Object getViewData() {
        return null;
    }

    /**
     * Returns the model attribute key that was provided
     * to the constructor.
     */
    public final String getModelAttributeKey() {
        return modelAttributeKey;
    }

    /**
     * As this is a read-only binding the value in the
     * view never changes.
     *
     * <p>The implementation returns <code>false</code>.</p>
     */
    public final boolean wasViewModified() {
        return false;
    }

    /**
     * As this is a read-only binding the value in the
     * view never changes and are never pushed back into
     * the model.
     *
     * <p>The implementation returns <code>false</code>.</p>
     */
    public final boolean onChangeWriteToModel() {
        return false;
    }

    /**
     * As this is a read-only binding the implementation
     * returns <code>true</code>.
     */
    public final boolean isReadOnly() {
        return true;
    }
}
