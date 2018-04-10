package de.tarent.commons.action;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

/**
 * Implementation of a Menubar as an ActionContainer.
 *
 * Actions may be appended by an menuPath:
 * <pre>
 * menuPath :== MenuName ("/" MenuName)* (":" Position)?
 * MenuName :== &lt;Name&gt;
 * Position :== [0-9]+
 * </pre>
 *
 * The Menus will be created as needed. A missing position or a position of -1 means appending at the end.
 * The Position of the Actions is not forced. A later appended Action may relocate other actions.
 *
 * @see de.tarent.contact.gui.action.ActionRegistry
 */
public class MenuBar extends JMenuBar implements ActionContainer {

    private static final long serialVersionUID = -6683876792991292962L;
    private static final Logger logger = Logger.getLogger(MenuBar.class.getName());

    protected String uniqueName;
    protected Map menus = new HashMap();
    protected ResourceBundle resourceBundle;
    protected static final String DEFAULT_MENU = "Extras";
    private static final String PRIORITY_KEY = "menuBar.items.priority.key";

    public MenuBar(String uniqueName, ResourceBundle resourceBundle) {
        this.uniqueName = uniqueName;
        this.resourceBundle = resourceBundle;
    }

    /**
     * Returns container's unique name.
     */
    public String getContainerUniqueName() {
        return uniqueName;
    }

    /**
     * Adds the given action to a menu bar at assigned position.
     *
     * @param action   The action to attach
     * @param menuPath The path and rules to attach, as described above
     */
    public void attachGUIAction(Action action, String menuPath) {
        JMenuItem item;
        String actionType = (String) action.getValue(AbstractGUIAction.PROP_KEY_ACTION_TYPE);

        if (AbstractGUIAction.TYPE_CHECK.equals(actionType)) {
            item = new JCheckBoxMenuItem(action);
            // Adds the checkbox to the list of elements whose selection state
            // can be set through AbstractGUIAction.setSelected(boolean)
            MenuHelper.addSynchronizationComponent(action, (JCheckBoxMenuItem) item);

        } else if (AbstractGUIAction.TYPE_CHOISE.equals(actionType)) {
            item = new JRadioButtonMenuItem(action);

        } else if (AbstractGUIAction.TYPE_SEPARATOR.equals(actionType)) {
            addSeparator(menuPath);
            return;//READY

        } else {
            item = new JMenuItem(action);
        }
        // set component name (for later ecxeption handling)
        item.setName((String) action.getValue(AbstractGUIAction.NAME));
        addItem(item, menuPath, MenuHelper.isWithFrontSeparator(action), MenuHelper.isWithBackSeparator(action));
    }

    private void addSeparator(String menuPath) {
        getAssignedMenu(menuPath).addSeparator();
    }

    public void removeGUIAction(Action action) {
        logger.warning("optional, not implemented method yet: removeGUIAction");
    }

    public void initActions() {
        Iterator mbIt = ActionRegistry.getInstance().getActions(getContainerUniqueName()).iterator();

        while (mbIt.hasNext()) {
            TarentGUIAction action = (TarentGUIAction) mbIt.next();
            attachGUIAction(action, action.getMenuPath(getContainerUniqueName()));
        }
    }

    /**
     * @param item              The item to attach
     * @param menuPath          The path and rules to attach, as described above
     * @param withBackSeparator
     */
    public void addItem(JMenuItem item, String menuPath, boolean withFrontSeparator, boolean withBackSeparator) {
        JMenu currentMenu = getAssignedMenu(menuPath);

        int priority = MenuHelper.getAssignedPriority(removeMainMenuPriority(menuPath));

        item.getAction().putValue(PRIORITY_KEY, new Integer(priority));

        int pos = MenuHelper.getInsertPosition(currentMenu.getMenuComponents(), PRIORITY_KEY, new Integer(priority));
        if (withFrontSeparator) {
            currentMenu.add(new JSeparator(), pos++);
        }

        logger.fine("[menuBar]: " + item.getAction() + ": pos=" + String.valueOf(pos) + " path=" + menuPath);
        currentMenu.add(item, pos++);

        if (withBackSeparator) {
            currentMenu.add(new JSeparator(), pos);
        }
    }

    private JMenu getAssignedMenu(String menuPath) {
        String menuName;
        JMenu parentMenu = null;
        JMenu currentMenu = null;
        String[] menuSteps = getMenuSteps(removeMainMenuPriority(menuPath));

        for (int i = 0; i < menuSteps.length; i++) {
            menuName = menuSteps[i];

            currentMenu = (JMenu) menus.get(menuName);

            if (null == currentMenu) {
                currentMenu = new JMenu(menuName);

                setMnemonicKey(menuName, currentMenu);
                MenuHelper.checkMenuNameForSpaces(currentMenu);
                menus.put(menuName, currentMenu);

                int priority = MenuHelper.getAssignedMainMenuPriority(menuPath);

                if (null != parentMenu) {
                    if (priority != -1 && priority < getMenuCount()) {
                        parentMenu.add(currentMenu, priority);
                    } else {
                        parentMenu.add(currentMenu);
                    }
                } else {
                    if (priority != -1 && priority < getMenuCount()) {
                        add(currentMenu, priority);
                    } else {
                        add(currentMenu);
                    }
                }
            }
            parentMenu = currentMenu;
        }
        return currentMenu;
    }

    private String[] getMenuSteps(String menuPath) {
        String[] menuSteps;
        if (null == menuPath || "".equals(menuPath)) {
            menuSteps = new String[] { DEFAULT_MENU };
        } else {
            String[] menuPathParts = menuPath.split(":");
            menuSteps = menuPathParts[0].split("/");
        }
        return menuSteps;
    }

    private String removeMainMenuPriority(String menuPath) {
        String[] menuPathParts = null;
        if (menuPath != null) {
            menuPathParts = menuPath.split(":");
        } else {
            return null;
        }

        // check if the menuPath starts with a digit
        if (menuPathParts != null && menuPathParts.length > 0 && Character.isDigit(menuPathParts[0].charAt(0))) {
            // this menuPath contains a priority-value for the main-menu. remove it
            return menuPath.substring(menuPath.indexOf(':') + 1);
        }

        // does not contain a priority-value for the main-menu. return without change
        return menuPath;
    }

    private void setMnemonicKey(String menuName, JMenu currentMenu) {
        try {
            String key = resourceBundle.getString(menuName);

            if (null != key) {
                int mnemonic = ActionRegistry.getKeyEventID(key);
                if (mnemonic != -1) {
                    currentMenu.setMnemonic(mnemonic);

                    return;
                }
            }
            logger.warning("[!] couldn't get mnemonic key for '" + menuName + "' menu");

        } catch (MissingResourceException e) {
            String bundleSuffix = "";
            if (!"".equals(resourceBundle.getLocale().getLanguage())) {
                bundleSuffix += "_" + resourceBundle.getLocale().getLanguage();
            }
            if (!"".equals(resourceBundle.getLocale().getCountry())) {
                bundleSuffix += "_" + resourceBundle.getLocale().getCountry();
            }
            logger.info("[MenuBar]: no mnemonic key found for '" + menuName + "' in Resource Bundle");
        }
    }
}
