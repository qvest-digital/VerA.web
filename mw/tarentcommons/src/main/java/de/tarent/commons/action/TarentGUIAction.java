package de.tarent.commons.action;

import javax.swing.Action;

/**
 * <code>TarentGUIAction</code> is an interface to use actions across 'tarent' applications.<p>
 * Inherited Parameters:
 * <pre>
 * Action.NAME,
 * Action.MNEMONIC_KEY,
 * Action.SHORT_DESCRIPTION,
 * Action.LONG_DESCRIPTION,
 * Action.SMALL_ICON,
 * Action.ACTION_COMMAND_KEY,
 * Action.ACCELERATOR_KEY.
 * </pre>
 * <p>
 * XML definition example:<p>
 * <pre>
 *   &lt;action&gt;
 *    &lt;param name="UniqueName" value="help.about"/&gt;
 *    &lt;param name="enabled" value="true"/&gt;
 *    &lt;param name="MnemonicKey" value="a"/&gt;
 *    &lt;param name="withBackSeparator" value="false"/&gt;
 *    &lt;param name="withFrontSeparator" value="false"/&gt;
 *    &lt;param name="Name" value="About tarent-contact"/&gt;
 *    &lt;param name="SmallIcon" value="about.gif"/&gt;
 *    &lt;param name="AcceleratorKey" value="F12" enabled="false"/&gt;
 *    &lt;param name="ShortDescription" value="about" enabled="false"/&gt;
 *    &lt;param name="LongDescription" value="about" enabled="false"/&gt;
 *    &lt;param name="ActionCommandKey" value="show.about.dialog" enabled="false"/&gt;
 *    &lt;param name="ActionClass" value="de.tarent.contact.gui.action.HelpAboutAction"/&gt;
 *    &lt;param name="Container" type="array"&gt;
 *      &lt;value&gt;mainFrame.menu:Hilfe&lt;/value&gt;
 *    &lt;/param&gt;
 *  &lt;/action&gt;
 * </pre>
 *
 * @see XmlUtil#getParamMap(Node)
 * @see ActionRegistry#readActionDefinition(String)
 */
public interface TarentGUIAction extends Action {

    public String PROP_KEY_ENABLED = "enabled";
    public String PROP_KEY_UNIQUE_NAME = "UniqueName";
    /**
     * @deprecated does not position the separator correctly under all circumstances, use 'withBackSepartor' instead (or fix it..)
     */
    public String PROP_KEY_WITH_FRONT_SEPARATOR = "withFrontSeparator";
    public String PROP_KEY_WITH_BACK_SEPARATOR = "withBackSeparator";
    public String PROP_KEY_ACTION_CLASS = "ActionClass";
    public String PROP_KEY_CONTAINER = "Container";
    public String PROP_KEY_DATA_SOURCE = "DataProvider";

    public String PROP_KEY_DEACTIVATION_CONTEXT = "DeactivationContext";

    /** An activation list contains unique names of related actions. */
    public String PROP_KEY_ACTIVATION_LIST = "ActivationList";
    /** A deactivation list contains unique names of related actions. */
    public String PROP_KEY_DEACTIVATION_LIST = "DeActivationList";
    /**
     * The key to store and retrieve the list of actions
     * that should be enabled because of the context of a given action.
     * <p>
     *  @see MenuHelper#getActivationActions(AbstractGUIAction)
     */
    public String PROP_KEY_ACTIONS_TO_ENABLE = "actionsToEnable";
    /**
     * The key to store and retrieve the list of actions
     * that should be disabled because of the context of a given action.
     * <p>
     *  @see MenuHelper#getDeactivationActions(AbstractGUIAction)
     */
    public String PROP_KEY_ACTIONS_TO_DISABLE = "actionsToDisable";

    /**
     * The key used to store and retrieve the list of components
     * wich should be synchronized. The actual value of the key is irrelevant,
     * it just needs to be different from the other keys in use.
     *
     * @see MenuHelper#addSynchronizationComponent(Action, JComponent)
     * @see MenuHelper#getSynchronizationComponents(Action)
     */
    public String PROP_KEY_SYNCHRONIZATION_COMPONENTS = "sync.comps";

    public String PROP_KEY_ACTION_TYPE = "ActionType";
    public String TYPE_SEPARATOR = "separator";
    public String TYPE_TRIGGER = "trigger";
    public String TYPE_DEFAULT = TYPE_TRIGGER;
    public String TYPE_CHOISE = "choise";
    public String TYPE_CHECK = "check";

    /**
     * Returns the global unique name of an action in order to reference it.
     * This is the same as in the Property PROP_KEY_UNIQUE_NAME.
     */
    public String getUniqueName();

    /**
     * Says wether an action is assigned to an appropriate container (menu, toolbar, etc.).
     */
    public boolean isAssignedToContainer(String containerName);

    /**
     * Returns the menu path, where an action will be allocated in a container.
     *
     * @param containerName The container for which the path is requestet.
     * @return An containerspecific identifier which describes, how to place this action. Null or "" if not specified.
     */
    public String getMenuPath(String containerName);

}
