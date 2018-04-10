package de.tarent.commons.ui;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class IconFactory {

    private static final Logger log = Logger.getLogger(IconFactory.class.getName());

    // constants for icon-filenames:
    public static final String CATEGORY = "tab_assign_category.gif";

    public static final String SUBCATEGORY = "tab_assign_category.gif";

    public static final String ERROR = "error.gif";

    public static final String INFO = "info.gif";

    public static final String QUESTION = "question.gif";

    public static final String USER = "sysuser.gif";

    public static final String CONTACT = "extuser.gif";

    public static final String FILTER = "tab_suchen.gif";

    public static final String CATEGORY_TREE_POSITIVE_SELECTION = "selection-positive.png";

    public static final String CATEGORY_TREE_NEGATIVE_SELECTION = "selection-negative.png";

    public static final String CATEGORY_TREE_MIXED_SELECTION = "selection-mixed.png";

    public static final String CATEGORY_TREE_NEUTRAL_SELECTION = "selection-neutral.png";

    public static final String CATEGORY_TREE_FIND = "categoryTree-find.png";

    private Set resourceURLs = new HashSet();

    private Map iconsCacheMap = new HashMap();

    /**
     * Guard for accessing {@link #resourceURLs} and {@link #iconsCacheMap}.
     */
    private static final Object singletonMonitor = new Object();

    private static IconFactory instance = new IconFactory();

    private IconFactory() {
        // No IconFactory for everyone.
    }

    /**
     * Returns Icon Factory.
     */
    public static IconFactory getInstance() {
        return instance;
    }

    /**
     * Loads and returns an Image Icon. Returns 'null' if icon not found.
     */
    public ImageIcon getIcon(String iconID) {
        synchronized (singletonMonitor) {
            if (iconsCacheMap.containsKey(iconID)) {
                return (ImageIcon) iconsCacheMap.get(iconID);
            }
        }

        return loadIcon(iconID);
    }

    /**
     * Accesses the
     *
     * @param iconId
     * @return
     */
    private ImageIcon loadIcon(String iconId) {
        ImageIcon result;
        Iterator ite = null;

        synchronized (singletonMonitor) {
            ite = resourceURLs.iterator();
            while (ite.hasNext()) {
                URL url = (URL) ite.next();
                URL loadURL = null;
                try {
                    loadURL = new URL(url, iconId);
                } catch (MalformedURLException e) {
                    log.warning("The resource URL '" + url + "' and the icon id '"
                            + iconId
                            + "' are not a valid URL when concatenated.");

                    // Try the next resource URL then.
                    continue;
                }

                try {
                    result = new ImageIcon(ImageIO.read(loadURL));

                    iconsCacheMap.put(iconId, result);

                    return result;
                } catch (IOException e) {
                    // Expected if the icon does not exist at the
                    // given location. Unexpected if a real load
                    // error occurs. => Bad design ...

                    // Try the next
                    continue;
                }
            }
        }
        log.warning(Messages.getString("GUI_Icon_Factory_Load_Error") + ": "
                + iconId);

        return null;
    }

    /**
     * Registers a new resource URL.
     * <p>
     * Modifier set to private as no one else used this. Change if needed.
     * </p>
     *
     * @throws IconFactoryException if empty URL
     */
    private void addResourcesURL(URL newImageLocationURL)
            throws IconFactoryException {

        if (newImageLocationURL == null) {

            throw new IconFactoryException(
                    Messages.getString("GUI_Icon_Factory_Add_Resources_URL_Error"));
        }

        synchronized (singletonMonitor) {
            if (resourceURLs.contains(newImageLocationURL)) {
                log.info(Messages.getString("GUI_Icon_Factory_URL_allready_registered_Error")
                        + ": " + newImageLocationURL.getPath());

                return;
            }

            resourceURLs.add(newImageLocationURL);
        }

        log.fine(Messages.getString("GUI_Icon_Factory_Resources_URL_added") + ": "
                + newImageLocationURL.getPath());
    }

    /**
     * Registers a new resources folder.
     */
    public void addResourcesFolder(String folderPath) throws IconFactoryException {

        try {

            addResourcesURL(getClass().getResource(folderPath));
        } catch (IconFactoryException e) {

            throw new IconFactoryException(e.getMessage() + ": " + folderPath);
        }
    }
}
