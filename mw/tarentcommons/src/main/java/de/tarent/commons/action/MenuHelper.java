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

import java.awt.Component;
import java.awt.MenuBar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JToggleButton;

/**
 * Here you can find help methods for Menus.
 *
 * @author Aleksej Palij (a.palij@tarent.de), tarent GmbH Bonn
 */
public class MenuHelper {

    private static final Logger logger = Logger.getLogger(MenuHelper.class.getName());

    /**
     * Returns the index to add some component sorted with it's priority value.
     *
     * <p>This method is needed for {@link de.tarent.contact.gui.action.ActionRegistry}
     * implementations where a <code>menuPath</code> contains an integer specifiying the
     * insertion order (= priority). Provide the priority of the component you want to
     * insert and receive the position to be used for the
     * {@link java.awt.Container#add(java.awt.Component, int)</p> method or similar ones.
     * </p>
     *
     * <p>To use this method you need to store the priority value in the
     * {@link javax.swing.Action} instance via its <code>putValue</code>
     * method or in the {@link javax.swing.JComponent} instance via its
     * <code>putClientProperty</code>.</p>
     *
     * <p>The <code>positionKey</code> argument is the key used to store the
     * priority value.</p>
     *
     * <p>The <code>positionValue</code> argument is the priority value of the
     * component you want to insert.</p>
     *
     * <p>The implementation iterates through the component array, tries to
     * get it's priority value. If the found priority value if smaller or
     * equal to the provided priority the currently processed component's
     * index is returned.</p>
     *
     * <p>A priority value for a component cannot be found if one of the
     * following conditions are met:
     * <lu>
     * <li>the component is not an <code>AbstractButton</code></li>
     * <li>the component has no <code>Action</code> associated with it</li>
     * <li>the component's <code>Action</code> does not return an integer
     * value for the key <code>priorityKey</code></li>
     * </lu>
     * In such a case the component is skipped and the next one is investigated.
     * </p>
     *
     * @param components  as sorted array
     * @param priorityKey to get position values of the sorted components
     * @param priority    to which a new component should be added
     * @return index
     */
    public static int getInsertPosition(Component[] components, String priorityKey, Integer priority) {
        if (components == null || components.length == 0) {
            return 0;// first position
        }
        int lastPosition = components.length;
        if ((priority != null)
                && (priority.intValue() != Integer.MAX_VALUE)) {//not the least priority
            for (int i = 0; i < lastPosition; i++) {
                Component c = components[i];
                Integer nextPriority = null;
                if (c instanceof AbstractButton) {//get action priority
                    AbstractButton nextButton = (AbstractButton) c;
                    Action a = nextButton.getAction();
                    if (a != null) {
                        nextPriority = (Integer) nextButton.getAction().getValue(priorityKey);
                    }
                } else if (c instanceof JComponent) {//get group priority (exists in JToolBar only)
                    JComponent nextComponent = (JComponent) c;
                    nextPriority = (Integer) nextComponent.getClientProperty(priorityKey);
                }

                //ignore whitespaces and separators
                if (nextPriority != null) {
                    if (priority.compareTo(nextPriority) >= 0) {
                        continue;//priority is still minor or equal
                    }

                    return i;//priority is greater than next priority found, thus (1 < 3), (7 < 9) etc.
                }
            }
        }

        return lastPosition;// last position
    }

    /**
     * Adds an element to the list of components which need to be synchronized for an
     * action.
     *
     * <p>Synchronizing components means that all affected elements take the state
     * of a 'leader' component. The leader is usually the component on which the user clicked
     * while the others are a different representation of the same functionality.
     * The state which is to be synchronized is most often the selected state, however an
     * implementation may choose to modify other states as well.</p>
     *
     * <p>This method is to be called by {@link de.tarent.contact.gui.action.ActionContainer}
     * implementation to register a particular JToggleButton (or subclass like JCheckBox)</p>
     *
     * @param action
     * @param component
     */
    public static void addSynchronizationComponent(Action action, JToggleButton component) {
        assert (component != null) : "JComponent argument must not be null.";

        List l = (List) action.getValue(TarentGUIAction.PROP_KEY_SYNCHRONIZATION_COMPONENTS);
        if (l == null) {
            l = new ArrayList();
            action.putValue(TarentGUIAction.PROP_KEY_SYNCHRONIZATION_COMPONENTS, l);
        }

        l.add(component);
    }

    /**
     * Provides the same feature as {@link #addSynchronizationComponent(Action, JToggleButton)}
     * but wraps the <code>JCheckBoxMenuItem</code> in a <code>JToggleButton</code> subclass
     * effectively disguising the component as a toggle button.
     *
     * <p>This approach is neccessary because <code>JCheckBoxMenuItem</code> and <code>JCheckBox</code>
     * are not both descendents of <code>JToggleButton</code> and maintaining two separate component
     * lists was not feasible.</p>
     *
     * @param action
     * @param component
     */
    public static void addSynchronizationComponent(Action action, JCheckBoxMenuItem component) {
        assert (component != null) : "JComponent argument must not be null.";
        addSynchronizationComponent(action, new DelegatingToggleButton(component));
    }

    /**
     * Returns an iterator allowing the traversal through the list of
     * components to be synchronized.
     *
     * <p>If only the selected state of the component is to be changed
     * and you have an {@link de.tarent.contact.gui.action.AbstractGUIAction}
     * instance at hand call its
     * {@link de.tarent.contact.gui.action.AbstractGUIAction#setSelected(boolean)
     * method instead.<p>
     * <p>
     *
     * @param action
     * @return iterator
     * @see #addSynchronizationComponent(Action, JToggleButton)
     * <p>
     */
    public static Iterator getSynchronizationComponents(Action action) {
        List l = (List) action.getValue(TarentGUIAction.PROP_KEY_SYNCHRONIZATION_COMPONENTS);

        if (l == null) {
            l = Collections.EMPTY_LIST;
        }

        return l.iterator();
    }

    /**
     * A simple wrapper around a <code>JCheckBoxMenuItem</code> to make
     * it behave programmatically like a <code>JToggleButton</code>.
     *
     * <p>More delegating methods can be added if needed.</p>
     *
     * @see MenuHelper#addSynchronizationComponent(Action, JCheckBoxMenuItem)
     */
    private static class DelegatingToggleButton extends JToggleButton {
        /**
         *
         */
        private static final long serialVersionUID = -2352133760894362291L;
        JCheckBoxMenuItem item;

        DelegatingToggleButton(JCheckBoxMenuItem item) {
            this.item = item;
        }

        public void setSelected(boolean s) {
            item.setSelected(s);
        }

        public boolean isSelected() {
            return item.isSelected();
        }

    }

    /**
     * Returns an iterator allowing the traversal through the list of
     * actions to be enabled.
     * <p>
     * This method can be called by any instance of {@link AbstractGUIAction}.
     *
     * @param action to which context some related actions should be enabled
     * @return iterator with AbstractGUIActions to enable
     */
    public static Iterator getActivationActions(AbstractGUIAction action) {
        String[] array = (String[]) action.getValue(TarentGUIAction.PROP_KEY_ACTIVATION_LIST);

        if (array != null) {
            List list = (List) action.getValue(AbstractGUIAction.PROP_KEY_ACTIONS_TO_ENABLE);
            //retrieve if possible
            if (list != null) {
                return list.iterator();//-> found
            }

            //save to retrieve later
            list = getRelatedActions(array);
            action.putValue(AbstractGUIAction.PROP_KEY_ACTIONS_TO_ENABLE, list);
            return list.iterator();

        }

        return Collections.EMPTY_LIST.iterator();
    }

    /**
     * Returns an iterator allowing the traversal through the list of
     * actions to be disabled.
     * <p>
     * This method can be called by any instance of {@link AbstractGUIAction}.
     *
     * @param action to which context some related actions should be disabled
     * @return iterator with AbstractGUIActions to disable
     */
    public static Iterator getDeactivationActions(AbstractGUIAction action) {
        String[] array = (String[]) action.getValue(TarentGUIAction.PROP_KEY_DEACTIVATION_LIST);

        if (array != null) {
            List list = (List) action.getValue(AbstractGUIAction.PROP_KEY_ACTIONS_TO_DISABLE);
            //retrieve if possible
            if (list != null) {
                return list.iterator();//-> found
            }

            //save to retrieve later
            list = getRelatedActions(array);
            action.putValue(AbstractGUIAction.PROP_KEY_ACTIONS_TO_DISABLE, list);
            return list.iterator();

        }

        return Collections.EMPTY_LIST.iterator();
    }

    /**
     * Used by {@link #getActivationActions(AbstractGUIAction)} {@link #getDeactivationActions(AbstractGUIAction)}.
     *
     * @return List of Actions to the given unique names
     */
    private static List getRelatedActions(String[] array) {
        List list;
        list = new ArrayList(array.length);
        for (int i = array.length - 1; i >= 0; i--) {//for every unique name
            String nextUniqueName = array[i].trim();
            AbstractGUIAction nextAction = ActionRegistry.getInstance().getAction(nextUniqueName);
            if (nextAction != null) {
                list.add(nextAction);//get action
            } else {
                logger.warning("[!] action to de-/activate not found: " + nextUniqueName);
            }
        }
        return list;
    }

    /**
     * Returns 'true' if a separator should be added after a given action.
     */
    public static boolean isWithBackSeparator(Action action) {
        boolean withBackSeparator = false;
        String separatorValue;
        separatorValue = (String) action.getValue(AbstractGUIAction.PROP_KEY_WITH_BACK_SEPARATOR);
        if (separatorValue != null) {
            withBackSeparator = Boolean.valueOf(separatorValue).booleanValue();
        }
        return withBackSeparator;
    }

    /**
     * Returns 'true' if a separator should be added before a given action.
     */
    public static boolean isWithFrontSeparator(Action action) {
        boolean withFrontSeparator = false;
        String separatorValue = (String) action.getValue(AbstractGUIAction.PROP_KEY_WITH_FRONT_SEPARATOR);
        if (separatorValue != null) {
            withFrontSeparator = Boolean.valueOf(separatorValue).booleanValue();
        }
        return withFrontSeparator;
    }

    /**
     * Replaces underscores with spaces in the menu title.
     * The required spaces have been encoded because of XML entities.
     *
     * @param currentMenu
     * @see MenuBar
     * @see SideMenuBar
     */
    public static void checkMenuNameForSpaces(JMenu currentMenu) {
        String menuNameToParse = currentMenu.getText();
        if (menuNameToParse.indexOf("_") > 0) {
            currentMenu.setText(menuNameToParse.replace('_', ' '));
        }
    }

    /**
     * Fumbles out the priority from a menu path.
     *
     * <p>Eg. in a string like "trülü:bla/foo/baz:42" you will
     * get 42. If the priority does not exist, you will get
     * <code>Integer.MAX_VALUE</code> denoting that the element should
     * be added to the end.<p>
     */
    public static int getAssignedPriority(String menuPath) {
        // check if position assigned
        String[] menuPathParts = menuPath.split(":");
        if (menuPathParts.length > 1 && !"".equals(menuPathParts[1])) {
            try {
                return Integer.parseInt(menuPathParts[menuPathParts.length - 1]);
            } catch (NumberFormatException nfe) {
                logger.warning("Invalid number format for menu priority in menu path: "
                        + menuPath);
            }
        }

        // No priority given, then return maximal possible value.
        return Integer.MAX_VALUE;
    }

    /**
     * Does quite the same as getAssignedPriority but for the main-menu-priority
     *
     * @param menuPath the menuPath to get the main-priority for
     * @return the priority for the assigned-main-menu or -1 if not specified
     */

    public static int getAssignedMainMenuPriority(String menuPath) {
        String[] menuPathParts = menuPath.split(":");

        // check if the menuPath starts with a digit
        if (menuPathParts != null && menuPathParts.length > 0 && Character.isDigit(menuPathParts[0].charAt(0))) {
            try {
                return Integer.parseInt(menuPathParts[0]);
            } catch (NumberFormatException pExcp) {
                logger.warning("Invalid number format for main-menu priority in menu path: " + menuPath);
            }
        }
        return -1;
    }
}
