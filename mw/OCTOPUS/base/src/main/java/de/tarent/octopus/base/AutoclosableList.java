package de.tarent.octopus.base;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Collects elements (in its {@link #record(AutoCloseable)} method) for later
 * autoclosing and is itself {@link AutoCloseable}.
 *
 * @author mirabilos (t.glaser@tarent.de)
 */
@Log4j2
@EqualsAndHashCode(callSuper = true)
public class AutoclosableList extends ArrayList<AutoCloseable> implements AutoCloseable {
    private static final AtomicInteger counter = new AtomicInteger(0);
    private final Integer id;

    /**
     * Constructs an empty list with a default initial capacity.
     */
    public AutoclosableList() {
        super();
        id = counter.getAndIncrement();
        logger.debug("{}: new autoclosable list", id);
    }

    /**
     * Logs when this list is finalised and checks it’s empty.
     */
    @SuppressWarnings({ "squid:CallToDeprecatedMethod", "deprecation", "squid:ObjectFinalizeOverridenCheck" })
    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.isEmpty()) {
                logger.debug("{}: finalising", id);
            } else {
                logger.error("{}: not empty while finalising, {} elements not closed", id,
                  this.size());
            }
        } finally {
            super.finalize();
        }
    }

    /**
     * Records a new {@link AutoCloseable} element (with the {@link #add(Object)} method)
     * and returns it, for use in call chains.
     *
     * @param element the element to record
     * @return element (unchanged)
     */
    public <T extends AutoCloseable> T record(T element) {
        logger.debug("{}: adding #{} {}", id, this.size(), element);
        this.add(element);
        return element;
    }

    /**
     * Abstract base class for exceptions thrown during {@link #close()}.
     * Our implementation throws one of these, and you can check it to
     * avoid logging the causes twice (we already log them).
     */
    abstract static class ClosingException extends Exception {
        /**
         * Constructs a new exception.
         *
         * @param message the detail message
         * @param cause   the cause
         */
        ClosingException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception thrown during {@link #close()} when only one element’s threw.
     * The exception thrown from the element’s {@code close} was already logged,
     * it’s also provided as cause for convenience.
     */
    public static class SingleClosingException extends ClosingException {
        SingleClosingException(Throwable t) {
            super("single exception closing an AutoclosableList", t);
        }
    }

    /**
     * Exception thrown during {@link #close()} when only multiple elements’ threw.
     * The exception thrown from the element’s {@code close} was already logged.
     */
    public static class MultipleClosingException extends ClosingException {
        MultipleClosingException() {
            super("multiple exceptions closing an AutoclosableList", null);
        }
    }

    /**
     * Closes all elements of this list, then clears the list.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     *
     * Cases where the close operation may fail require careful
     * attention by implementers. It is strongly advised to relinquish
     * the underlying resources and to internally <em>mark</em> the
     * resource as closed, prior to throwing the exception. The {@code
     * close} method is unlikely to be invoked more than once and so
     * this ensures that the resources are released in a timely manner.
     * Furthermore it reduces problems that could arise when the resource
     * wraps, or is wrapped, by another resource.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        int caught = 0;
        Exception cause = null;
        int i = 0;
        for (AutoCloseable element : this) {
            if (element != null) {
                logger.debug("{}: closing #{} {}", id, i, element);
                try {
                    element.close();
                } catch (Exception e) {
                    logger.warn("{}: error closing #{}", id, i, e);
                    cause = e;
                    ++caught;
                }
            } else {
                logger.debug("{}: is null #{}", id, i);
            }
            ++i;
        }
        logger.debug("{}: emptying", id);
        this.clear();
        if (caught == 1) {
            throw new SingleClosingException(cause);
        } else if (caught > 1) {
            throw new MultipleClosingException();
        }
    }
}
