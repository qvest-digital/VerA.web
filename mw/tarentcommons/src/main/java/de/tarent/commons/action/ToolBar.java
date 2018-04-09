package de.tarent.commons.action;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

/**
 * Implementation of the {@link de.tarent.contact.gui.action.ActionContainer} interface.
 * It follows the example of {@ de.tarent.contact.gui.action.MenuBar} und {@link de.tarent.contact.gui.action.SideMenuBar}.
 * <p><p>
 * <code>ToolBar</code> is an intsance of a {@link JToolBar} and thus a root container
 * for groups of buttons.
 * <p><p>
 *  A group of buttons is also an instance of a {@link JToolBar}, so we can add dynamically
 *  any Buttons to it and also change its position by 'drag & drop'
 *  if it's set as floatable.
 *  <p><p>
 *
 * In order to allocate some button at a particular position in the tool bar
 * you can use the following three optional characteristics separated via ':' colon:
 * <li> button allocation to a certain {groupName},
 * <li> group order assignment via {groupPriority},
 * <li> button order assignment inside a given group via {actionPriority}.<p>
 * <p>
 * Path:={groupName}:{groupPriority}:{actionPriority}.<p>
 * <p>
 * For example "edit:1:4" path allocates an action to a new button within the 'edit' group,
 * the 'edit' group will be placed as first group among the other groups and
 * the assigned/created button will be placed before all the buttons with priority greater than <code>4</code>.
 * That means '1' has the major priority and everything greater '1' has minor priority.<p>
 * <p>
 * The "" empty path adds a new Button to the default group and at the last position within this group.
 * If all actions are without path, so all buttons will be placed in one default group in the same order as they registered.
 * <p>
 *
 * @author Aleksej Palij (a.palij@tarent.de), tarent GmbH Bonn
 */
public class ToolBar extends JToolBar implements ActionContainer {

    /**
	 *
	 */
	private static final long serialVersionUID = -4105102964317205938L;

	private static final Logger logger = Logger.getLogger(ToolBar.class.getName());

    /** The key to store and retrieve the assigned group name via {@link Action#putValue()}
     */
    private static final String GROUP_NAME = "toolBar.groupName";

    /** The key to store and retrieve the assigned group priority via {@link Action#putValue()}
     */
    private static final String GROUP_PRIORITY = "toolBar.groupPriority";

    /** The key to store and retrieve the assigned action priority via {@link Action#putValue()}
     */
    private static final String ACTION_PRIORITY = "toolBar.actionPriority";

    //fix minor priority as default
    private static final int DEFAULT_PRIORITY = Integer.MAX_VALUE;
    //fix default group name
    private static final String DEFAULT_GROUP = "default";
    //fix offset between groups
    private static final int GROUP_OFFSET = 9;
    //fix tool bar height: see constructor!
    private static final int HEIGHT = 45;

    // whether the toolbar should be small (without text) or large with large symbols and the action-text under it
    protected boolean small = true;

    //The cached groups:
    //each new group will be encapsulated via one new JToolBar instance
    //and added to the root JToolBar (thus de.tarent.contact.gui.action.ToolBar).
    //While all groups are assigned only to one (root) container it's still possible
    //to make them floatable and so dock the whole root container to {NORTH,WEST,EAST,BOTTOM} side.
    private final Map groups = new HashMap();

    private String uniqueName;

    /** Returns empty and not floatable tool bar with the assigned unique name.*/
    public ToolBar(String aUniqueName) {
	this(aUniqueName, true);
    }

    /**
     * Returns empty and not floatable tool bar with the assigned unique name.
     *
     * @param aUniqueName - a unique name
     * @param small - whether the toolbar should be small (without text) or large with large symbols and the action-text under it
     */
    public ToolBar(String aUniqueName, boolean small){
	uniqueName = aUniqueName;

	this.small = small;

	//we set hight of a tool bar with invisible component (rigid area):
	//the space between tool bar and button edges will be required,
	//because of empty border (explicitly deactivated for better look).
	//add(Box.createRigidArea(new Dimension(5,HEIGHT)));
	setFloatable(false);

	if(!small)
		setOpaque(false);
    }

    public String getContainerUniqueName() {
	return uniqueName;
    }

    /**
     * Adds the given action to a tool bar according to it's path.<p>
     * A path consists of group name, group ranking, location, action ranking.
     *
     * @param action The action to attach
     * @param path to paste the given action, i.e. "editGroup:1/NORTH:1"
     */
    public void attachGUIAction( Action action, String path ) throws ActionContainerException {
	if(action == null) throw new ActionContainerException("can't create any button from empty action");

	checkType(action);
	checkPath(action, path);

	addButton( action );
    }

    /**
     * Creates a new Button for the given action,
     * finds an assigned group of buttons and adds this button
     * at assigned position according to the position ranking.
     *
     * @param action The action to attach
     */
    private void addButton( Action action ) throws ActionContainerException {
	AbstractButton newButton = initButton( action );
	if(small) {
		Dimension d = new Dimension(28,28);
		newButton.setMinimumSize(d);
		newButton.setMaximumSize(d);
		newButton.setPreferredSize(d);
	} else {
		newButton.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
	    newButton.setOpaque(false);
	}

	JToolBar assignedGroup = getAssignedGroupToolBar( action );
	int pos = MenuHelper.getInsertPosition(assignedGroup.getComponents(), ACTION_PRIORITY, (Integer) action.getValue(ACTION_PRIORITY));

	assignedGroup.add(newButton, pos);

    }

    /** Retrieves an assigned group if already exists or creates one. */
    private JToolBar getAssignedGroupToolBar( Action action ) {
	JToolBar groupToolBar = null;
	String groupName = (String) action.getValue(GROUP_NAME);
	if(groupName == null) groupName = DEFAULT_GROUP;
	if(groups.containsKey(groupName)){
	    //retrieve the assigned group
	    groupToolBar = (JToolBar) groups.get(groupName);
	} else {
	    //create a new group
	    groupToolBar = new JToolBar(groupName);
	    //fix allocation
	    groupToolBar.setFloatable(false);
	    //remove border for better Look&Feel
	    groupToolBar.setBorder(null);

	    groupToolBar.setOpaque(false);

	    //set priority
	    Integer groupPriority = (Integer) action.getValue(GROUP_PRIORITY);
	    if(groupPriority == null) groupPriority = new Integer(DEFAULT_PRIORITY);

	    //client property will be used in MenuHelper.getInsertPosition()
	    //in order to sort groups whithin the root tool bar
	    groupToolBar.putClientProperty(GROUP_PRIORITY, action.getValue(GROUP_PRIORITY));

	    //insert the new group at sorted position
	    int pos = MenuHelper.getInsertPosition(getComponents(),GROUP_PRIORITY,groupPriority);
	    add(groupToolBar, pos);
	    //cache the new group
	    groups.put(groupName, groupToolBar);

	    //make space between groups
	    add(Box.createHorizontalStrut(GROUP_OFFSET), ++pos);
	}
	return groupToolBar;
    }

    /** Handles default properties and then additional group and action priority values. */
    private AbstractButton initButton( Action action ) throws ActionContainerException {

	String actionType = (String) action.getValue(AbstractGUIAction.PROP_KEY_ACTION_TYPE);
	AbstractButton newButton;

	if (AbstractGUIAction.TYPE_CHECK.equals(actionType))
	{
		newButton = new JToggleButton();

		// Adds the checkbox to the list of elements whose selection state
		// can be set through AbstractGUIAction.setSelected(boolean)
	    MenuHelper.addSynchronizationComponent( action, (JToggleButton) newButton );
	}
	else
		newButton = new JButton();

	//1. set state for toggle-buttons
	if(action instanceof AbstractGUIAction)
		newButton.setSelected(((AbstractGUIAction)action).isSelected());

	//2. retrieve common properties
	if(small)
		newButton.putClientProperty("hideActionText", Boolean.TRUE);//don't use name as text of tool button
	else {
		newButton.setHorizontalTextPosition(SwingConstants.CENTER);
	    newButton.setVerticalTextPosition(SwingConstants.BOTTOM);
	}

	//set default properties:
	// MNEMONIC_KEY,
	// NAME,SHORT_DESCRIPTION,
	// SMALL_ICON,
	// ACTION_COMMAND_KEY,
	// enabled state property
	newButton.setAction(action);

	//3. retrieve priority values
	try {
	    String value;
	    //set group ranking
	    value = (String) action.getValue(GROUP_PRIORITY);
	    if (value != null) action.putValue(GROUP_PRIORITY, Integer.valueOf(value));
	    //set action ranking
	    value = (String) action.getValue(ACTION_PRIORITY);
	    if (value != null) action.putValue(ACTION_PRIORITY, Integer.valueOf(value));

	}
	catch ( NumberFormatException e ) {
	    logger.warning("[!] invalid position ranking values: " + e.getMessage());
	}
	return newButton;//initialized
    }

    /** Throws ActionContainerException if an action has invalid type:
     * <li> Choice Box, </li>
     * <li> or Separator. </li>
     */
    private void checkType( Action action ) throws ActionContainerException {
	String actionType = (String) action.getValue(AbstractGUIAction.PROP_KEY_ACTION_TYPE);

	if (AbstractGUIAction.TYPE_CHOISE.equals(actionType))
	{
	    throw new ActionContainerException("illegally defined a radio button for the tool bar");
	}
	else if (AbstractGUIAction.TYPE_SEPARATOR.equals(actionType))
	{
	    throw new ActionContainerException("illegally defined a separator for the tool bar");
	}
    }

    /**
     * Four cases are possible:
     * <li> "" empty path,
     * <li> "groupName" or "actionPriority" only,
     * <li> "groupName:actionPriority" or
     * <li> "groupName:groupPriority:actionPriority" as full path.
     */
    private void checkPath( Action action, String path ) {
	if(path == null || "".equals(path)) {
	    logger.fine("[!] no path: last position will be used as default");
	    return;
	}

	//check valid format
	String[] pathData = path.split(":");
	if(pathData.length > 3 || pathData.length <= 0) {
	    logger.fine("[!] invalid action path format [" + pathData + "]: last position will be used as default");
	    return;
	}
	//retrieve data
	switch ( pathData.length ) {
	    case 1:
		try{
		    Integer.valueOf(pathData[0]);
		    //is action priority
		    action.putValue(ACTION_PRIORITY, pathData[0]);
		}catch(NumberFormatException e){
		    //is group name
		    action.putValue(GROUP_NAME, pathData[0]);
		}
		break;
	    case 2:
		action.putValue(GROUP_NAME, pathData[0]);
		action.putValue(ACTION_PRIORITY, pathData[1]);
		break;
	    case 3:
		action.putValue(GROUP_NAME, pathData[0]);
		action.putValue(GROUP_PRIORITY, pathData[1]);
		action.putValue(ACTION_PRIORITY, pathData[2]);
		break;
	}
    }

    public void removeGUIAction( Action action ) {
	logger.warning("optional, not implemented method yet: removeGUIAction");
    }

    public void initActions() throws ActionContainerException {
	Iterator tbIt = ActionRegistry.getInstance().getActions(getContainerUniqueName()).iterator();

		while(tbIt.hasNext())
		{
			TarentGUIAction action = (TarentGUIAction)tbIt.next();
			attachGUIAction(action, action.getMenuPath(getContainerUniqueName()));
		}
    }
}
