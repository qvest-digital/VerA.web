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

/**
 * This simple message helper class help you to i18n your application
 * and make error messages compile safe. Use this with the static
 * {@link MessageHelper#init() init}-method of the {@link MessageHelper}.
 *
 * @author Christoph Jerolimov, tarent GmbH
 */
public interface Message {
    /**
     * Return the key of the current message.
     *
     * @return Key of current message.
     */
    public String getKey();

    /**
     * Return the source path of the current message. For example the full
     * resourcepath (Classpath incl. the absolut position of the jar file
     * which include this.)
     *
     * @return Absolut source position of current message.
     */
    public String getSource();

    /**
     * Return the current message in a plain format, incl. <code>{0}</code>
     * as placeholder for arguments.
     */
    public String getPlainMessage();

    //	/**
    //	 * This was the short form for the multiple getMessage methods, but it is
    //	 * only available in java 5.
    //	 *
    //	 * @param arguments
    //	 * @return
    //	 */
    //	public String getMessage(Object... arguments);

    /**
     * Return the transformed message with no argument.
     *
     * @return Message
     */
    public String getMessage();

    /**
     * Return the transformed message with one argument.
     *
     * @param arg0
     * @return Message
     */
    public String getMessage(Object arg0);

    /**
     * Return the transformed message with two arguments.
     *
     * @param arg0
     * @param arg1
     * @return Message
     */
    public String getMessage(Object arg0, Object arg1);

    /**
     * Return the transformed message with three arguments.
     *
     * @param arg0
     * @param arg1
     * @param arg2
     * @return Message
     */
    public String getMessage(Object arg0, Object arg1, Object arg2);

    /**
     * Return the transformed message with four arguments.
     *
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @return Message
     */
    public String getMessage(Object arg0, Object arg1, Object arg2, Object arg3);

    /**
     * Return the transformed message with five arguments.
     *
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @param arg4
     * @return Message
     */
    public String getMessage(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4);

    /**
     * Return the transformed message with flexible arguments as an array.
     *
     * @param arguments
     * @return Message
     */
    public String getMessage(Object arguments[]);
}
