package de.tarent.commons.messages;

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
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
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

import java.text.MessageFormat;

/**
 * This simple message helper class help you to i18n your application
 * and make error messages compile safe. Use this with the static
 * {@link MessageHelper#init() init}-method of the {@link MessageHelper}.
 *
 * @author Christoph Jerolimov, tarent GmbH
 */
public class MessageImpl implements Message {
    /**
     * The key of this message.
     */
    private String key;
    /**
     * The source of this message.
     */
    private String source;
    /**
     * The untransformed message.
     */
    private String message;

    /**
     * This class implement a {@link Message} and use the given parameter
     * <code>key</code>, <code>source</code> and <code>message</code> for
     * {@link #getKey()}, {@link #getSource()} and transforming message
     * with the different {@link #getMessage()} methods.
     *
     * @param key
     * @param source
     * @param message
     */
    MessageImpl(String key, String source, String message) {
        this.key = key;
        this.source = source;
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    public String getKey() {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    public String getSource() {
        return source;
    }

    /**
     * {@inheritDoc}
     */
    public String getPlainMessage() {
        return message;
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage() {
        return getMessage(new Object[] {});
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage(Object arg0) {
        return getMessage(new Object[] { arg0 });
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage(Object arg0, Object arg1) {
        return getMessage(new Object[] { arg0, arg1 });
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage(Object arg0, Object arg1, Object arg2) {
        return getMessage(new Object[] { arg0, arg1, arg2 });
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage(Object arg0, Object arg1, Object arg2, Object arg3) {
        return getMessage(new Object[] { arg0, arg1, arg2, arg3 });
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        return getMessage(new Object[] { arg0, arg1, arg2, arg3, arg4 });
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage(Object[] arguments) {
        return MessageFormat.format(getPlainMessage(), arguments);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return super.toString() + " [source=" + getSource() + "; key=" + getKey() + "]";
    }
}
