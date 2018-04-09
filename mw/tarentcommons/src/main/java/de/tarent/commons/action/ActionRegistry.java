/* $Id: ActionRegistry.java,v 1.3 2007/08/21 13:32:15 fkoester Exp $
 *
 * tarent-contact, Plattform-Independent Webservice-Based Contactmanagement
 * Copyright (C) 2002 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-contact'
 * (which makes passes at compilers) written
 * by Sebastian Mancke.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.commons.action;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.tarent.commons.config.DataFormatException;
import de.tarent.commons.config.XmlUtil;
import de.tarent.commons.ui.IconFactory;
import de.tarent.commons.ui.Messages;

/**
 * <p>
 * Action registry should be used in order to manage GUI
 * actions and their context. This class is a singleton.
 * </p>
 * <p>
 * In order to be notified about context changes you can implement a {@link ContextChangeListener}
 * interface and register it's instance with {@link #addContextChangeListener(ContextChangeListener)}
 * method.
 * </p>
 * <p>
 * At the moment there are four major contexts:
 * <ul>
 * <li> {@link de.tarent.config.StartUpConstants#VIEW_MODE} when nothing processed,</li>
 * <li> {@link de.tarent.config.StartUpConstants#SEARCH_MODE} when search is being processed,</li>
 * <li> {@link de.tarent.config.StartUpConstants#NEW_MODE} when a new contact entry is being processed, and</li>
 * <li> {@link de.tarent.config.StartUpConstants#EDIT_MODE} when a selected contact entry is being processed.</li>
 * </ul>
 * </p>
 * <p>
 * For each context (also for dynamically registered) <code>ActionRegistry</code> holds a set of actions
 * to be disabled when a given context will be activated. The same set of actions will be also used
 * when deactivating a context: the actions within a set will be then enabled.
 * The context can be managed by two simple methods:
 * <ul>
 * <li> {@link #enableContext(String)}</li>
 * <li> {@link #disableContext(String)}</li>
 * </ul>
 * </p>
 * <p>
 * and by one optimized method {@link #switchContext(String, String)},
 * which facilitates an efficient swithching between two disjunctive contexts.
 * </p>
 * <p>
 * The context modification is performed by the {@link de.tarent.contact.controller.ActionManager} and it's super classes,
 * when appropriate gui requests or action events processed.
 * Here is the context change-over table (top-down) with according actions, that causes a major context change:
 * </p>
 * <pre>
 *  _______________________________________________
 * |xxxxxxx|---NEW---|---EDIT--|--SEARCH--|--VIEW--|
 * |_______________________________________________|
 * |NEW----|xxxxxxxxx|xxxxxxxxx|---new----|--new---|
 * |-------|xxxxxxxxx|xxxxxxxxx|----------|-import-|
 * |_______________________________________________|
 * |EDIT---|xxxxxxxxx|xxxxxxxxx|---edit---|--edit--|
 * |_______________________________________________|
 * |SEARCH-|xxxxxxxxx|xxxxxxxxx|xxxxxxxxxx|-search-|
 * |_______________________________________________|
 * |-------|--cancel-|--cancel-|filter-off|xxxxxxxx|
 * |VIEW---|--save---|---save--|----------|xxxxxxxx|
 * |-------|---------|--delete-|----------|xxxxxxxx|
 * |_______________________________________________|
 * </pre>
 * <p>
 * And here is the list of actions to be disabled for a given context:<p>
 * </p>
 * <p>
 * NEW:
 * <ul>
 * <li>new.contact.entry</li>
 * <li>new.journal.entry</li>
 * <li>edit.contact</li>
 * <li>delete.contact</li>
 * <li>search.contact</li>
 * <li>search.off</li>
 * <li>go.to.contact.homepage</li>
 * <li>{navigation-actions: first, previous, next, last}</li>
 * <li>dispatch/send/post/export</li>
 * <li>view{oders,mass-mail,journal}</li>
 * </ul>
 * </p>
 * <p>
 * EDIT:
 * <ul>
 * <li> {the same as NEW, but without delete}</li>
 * <li> import.VCard</li>
 * </ul>
 * </p>
 * <p>
 * SEARCH:
 * <ul>
 * <li> search.contact</li>
 * <li> save.contact</li>
 * <li> import.VCard</li>
 * </ul>
 * </p>
 * <p>
 * VIEW:
 * <ul>
 * <li>search.off</li>
 * <li>save.contact</li>
 * </ul>
 * </p>
 *
 * <p>
 * There are two ways for a container to obtain his Actions.
 * </p>
 * <ul>
 * <li>Push: Register the container before any action was loaded. For each
 * action the container gets notified.</li>
 * <li>Pull: Obtain all Actions for a speciffic container by getActions(UID)
 * Method.</li>
 * </ul>
 *
 * @see de.tarent.contact.controller.manager.AmSelection
 * @see de.tarent.contact.controller.manager.AmRequestFacade
 *
 */
public class ActionRegistry {

    private static final Logger logger = Logger.getLogger( ActionRegistry.class.getName() );

    private static final Object[] emptyObjectArray = new Object[0];

    private static final Class[] emptyClassArray = new Class[0];

    private static final Object singletonMonitor = new Object();

    private static ActionRegistry instance;

    /** List of GUI Containers, that use actions. */
    protected List containersList = new ArrayList();

    /** Actions Map - [action.uniqueName:Action]. */
    protected Map actionsMap = new HashMap();

    /** Data Provider Map - [className:DataProvider]. */
    protected Map dataProvidersMap = new HashMap();

    /** Context Map - [context:List[Action]]. */
    protected Map deactivationContextMap = new HashMap();

    /** A set of active contexts.*/
    private Set activeContexts = new HashSet();

    /** A list of context change listeners.*/
    private List contextChangeListeners = new ArrayList();

    /** A list of <code>Runnable</code> that need to be run
     * as soon as it is considered safe.
     *
     * <p>If the <code>List</code> instance is <code>null</code>
     * the delayed initializations have been run already.</p>
     *
     */
    private List delayedInitializiations = new ArrayList();

    private ActionRegistry() {
    }

    /**
     * Runs all the action's delayed initiallizations.
     *
     * <p>As the actions contained within the registry may depend on
     * other program modules being initialized first, the
     * <code>ActionRegistry</code> will delay the action's initialization
     * by putting them into a wait list.</p>
     *
     * <p>When the rest of the application is in the proper state for the
     * actions initialization this method has to be called. This needs to
     * done only once.</p>
     */
    public void runDelayedInitializations()
    {
      if (delayedInitializiations == null)
        return;

      synchronized (singletonMonitor)
      {
        if (delayedInitializiations == null)
          return;

        Iterator ite = delayedInitializiations.iterator();
        while (ite.hasNext())
          ((Runnable) ite.next()).run();

        delayedInitializiations.clear();
        delayedInitializiations = null;

      }
    }

    /**
     * Returns a singleton instance.<p>
     * @return ActionRegistry
     */
    public static ActionRegistry getInstance() {
      // Try without synchronization first.
      if (instance != null)
        return instance;

      // Otherwise pay the cost for synchronization
      // and try again.
      synchronized ( singletonMonitor )
      {
        if (instance == null)
          instance = new ActionRegistry();
      }

      return instance;
    }

    /**
     * Registers a {@link ContextChangeListener} intance.
     * This instance will be notified about context changes.<p>
     * @param l listener
     */
    public void addContextChangeListener( ContextChangeListener l ) {
        if ( !contextChangeListeners.contains( l ) )
            contextChangeListeners.add( l );
        else
            logger.fine( "[!] context change listener already registered" );
    }

    /**
     * Removes a {@link ContextChangeListener} intance from the list of listeners.<p>
     * @param l listener
     * @return 'true' if listener removed
     */
    public boolean removeContextChangeListener( ContextChangeListener l ) {
        return contextChangeListeners.remove( l );
    }

    public void addContainer( ActionContainer actionContainer ) {
        containersList.add( actionContainer );
    }

    public void removeContainer( ActionContainer actionContainer ) {
        containersList.remove( actionContainer );
    }

    public void addDataProvider( DataProvider provider ) {
        dataProvidersMap.put( provider.getClass().getName(), provider );
    }

    public void removeDataProvider( DataProvider provider ) {
        dataProvidersMap.remove( provider.getClass().getName() );
    }

    /**
     * Provides a List of all Actions for a special Container
     * @param actionContainerUID
     *          The Unique ID for the container
     * @return A list of TarentGUIAction instances
     */
    public Collection getActions( String actionContainerUID ) {
        List actionList = new LinkedList();
        TarentGUIAction action;
        for ( Iterator iter = actionsMap.values().iterator(); iter.hasNext(); ) {
            action = (TarentGUIAction) iter.next();
            if ( action.isAssignedToContainer( actionContainerUID ) ) {
                actionList.add( action );
            }
        }
        return actionList;
    }

    /**
     * Provides a List of all registered Actions
     * @return A list of TarentGUIAction instances
     */
    public Collection getActions() {
        return actionsMap.values();
    }

    /**
     * This method initializes the properties of an action and registers it by it's assigned containers
     * according to the defined container path.<p>
     *
     * @param action to register
     */
    public void registerAction( TarentGUIAction action ) {
        actionsMap.put( action.getUniqueName(), action );
        checkContext( action );

        for ( Iterator iter = containersList.iterator(); iter.hasNext(); ) {
            ActionContainer currentContainer = (ActionContainer) iter.next();
            if ( action.isAssignedToContainer( currentContainer.getContainerUniqueName() ) ) {
                try {
                    currentContainer.attachGUIAction( action, action.getMenuPath( currentContainer
                        .getContainerUniqueName() ) );
                }
                catch ( Exception e ) {
                    logger.warning( "[!] failed registering action at assigned container: " + e.getMessage());
                }
            }
        }
    }

    /** Registers an action to its deactivation context.*/
    private void checkContext( TarentGUIAction action ) {
        String value = (String) action.getValue( TarentGUIAction.PROP_KEY_DEACTIVATION_CONTEXT );
        if ( value != null && !"".equals( value ) ) {
            String[] contexts = value.split( "\\s*,\\s*" );
            for ( int i = contexts.length - 1; i >= 0; i-- ) {//for each context
                String currentContext = contexts[i];
                List registeredActions = (List) deactivationContextMap.get( currentContext );
                if ( registeredActions == null ) {
                    registeredActions = new ArrayList();
                    deactivationContextMap.put( currentContext, registeredActions );
                }
                registeredActions.add( action );//register a given action
            }
        }
    }

    public void deregisterAction( TarentGUIAction action ) {
        logger.warning( "[ActionRegistry] 'deregisterAction' method is not implemented yet" );
    }

    /**
     * checks for data-sources the action needs and registers the action
     */
    public void registerAtDataProviders( TarentGUIAction action ) {
        Object providerObj = action.getValue( TarentGUIAction.PROP_KEY_DATA_SOURCE );

        if ( providerObj != null ) {
            List providerList = ( providerObj instanceof List ) ? (List) providerObj : Collections.singletonList( ""
                + providerObj );
            String providerClassName;
            DataProvider provider;
            for ( Iterator iterator = providerList.iterator(); iterator.hasNext(); ) {
                providerClassName = (String) iterator.next();
                if ( dataProvidersMap.containsKey( providerClassName ) ) {
                    logger.finer( "Action " + action.getUniqueName() + " wird bei Provider " + providerClassName
                        + " registriert" );
                    provider = (DataProvider) dataProvidersMap.get( providerClassName );
                    provider.registerDataConsumer( action );
                }
                else {
                    logger.info( "Provider " + providerClassName
                        + " ist als Datenquelle konfiguriert aber nicht in der ActionRegistry vorhanden" );
                }
            }
        }// else: Provider-Property was empty
    }

   /**
     * Creates the actions from the definitions loaded
     * by the config management.
     */
    public void init( Iterator actionDefinitions ) {

            TarentGUIAction nextAction;

            while ( actionDefinitions.hasNext() ) {
                Map map = (Map) actionDefinitions.next();
                try {
                    nextAction = createActionFromMap(map);
                }
                catch ( ActionDefinitionException e ) {// action failed

                    logger.warning( e.getMessage() + " " + e.getCause() + " (Action: " + e.getActionUniqueName() +")");

                    continue;// next action
                }

                registerAction( nextAction );

                registerAtDataProviders( nextAction );
            }

    }
/**
   * Reads the Action-Definitions from a input Stream and registers all actions
   * in this file.
   *
   * <p>A base URI must be provided to allow resolving of relative URIs within
   * the document.</p>
   *
   * <p>For 1.4-compatibility the base URI must contain the protocol, e.g. "http://"
   * or "file://".</p>
   */
  public void readActionDefinition(InputStream is, String baseURI) throws IOException,
      ActionDefinitionException
  {
    try
      {
        Document definition = XmlUtil.getParsedDocument(is, baseURI);
        Element root = definition.getDocumentElement();
        NodeList actions = root.getElementsByTagName("action");
        int length = actions.getLength();

        ArrayList list = new ArrayList();
        for (int i=0;i<length;i++)
          list.add(XmlUtil.getParamMap(actions.item(i)));

        init(list.iterator());
      }
    catch (XmlUtil.Exception e)
      {
        throw new ActionDefinitionException(e);
      }
    catch (DataFormatException e)
    {
      throw new ActionDefinitionException(e);
    }
  }

    /**
     * Creates an Contact Action from the Values in the map. This needs
     * consistence-checks and type conversions.
     * @param map
     *          The Attributes from this Action. This Map will be modified, during
     *          the processing.
     * @throws ActionDefinitionException
     *           if not all params are suitable
     */
    protected TarentGUIAction createActionFromMap( Map map ) throws ActionDefinitionException {
        TarentGUIAction ga = getActionInstance( map );
        Object containerObject = getRequiredAttributeFromMap( map, TarentGUIAction.PROP_KEY_CONTAINER );

        List containerList = ( containerObject instanceof List ) ? (List) containerObject : Collections
            .singletonList( containerObject.toString() );
        setProperties( map, ga );
        initAbstractAction( ga, containerList );

        return ga;
    }

    private void setProperties( Map map, TarentGUIAction ga ) {
        Map.Entry mapEntry;
        String stringKey;

        logger.finest( "[ActionRegistry] " + map.get( TarentGUIAction.NAME ) + "..." );
        for ( Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
            mapEntry = (Map.Entry) iter.next();
            stringKey = mapEntry.getKey().toString();

            if ( Action.ACCELERATOR_KEY.equals( stringKey ) ) {
                setAccelerator( ga, mapEntry );
            }
            else if ( Action.MNEMONIC_KEY.equals( stringKey ) ) {
                setMnemonic( ga, mapEntry );
            }
            else if ( Action.SMALL_ICON.equals( stringKey ) ) {
                loadIcon( ga, mapEntry.getValue() );
            }
            else if ( TarentGUIAction.PROP_KEY_ENABLED.equals( stringKey ) ) {
                // Not using Boolean.parseBoolean() for 1.4-compatibility
                ga.setEnabled( Boolean.valueOf( mapEntry.getValue().toString() ).booleanValue() );
            }
            else if ( TarentGUIAction.PROP_KEY_ACTIVATION_LIST.equals( stringKey ) ) {
                ga
                    .putValue( TarentGUIAction.PROP_KEY_ACTIVATION_LIST,
                               getStringArray( mapEntry.getValue().toString() ) );
            }
            else if ( TarentGUIAction.PROP_KEY_DEACTIVATION_LIST.equals( stringKey ) ) {
                ga.putValue( TarentGUIAction.PROP_KEY_DEACTIVATION_LIST,
                             getStringArray( mapEntry.getValue().toString() ) );
            }
            else {
                ga.putValue( mapEntry.getKey().toString(), mapEntry.getValue() );
            }
        }
    }

    /**
     * Splits a given comma separed string.<p>
     * The tring "foo,bla,\n\tblabla",for example, yields the following result:<p>
     * {"foo","bla","blabla"}.<p>
     *
     * @return String[] array of splited strings
     */
    private String[] getStringArray( String string ) {
        return string.split( "\\s*,\\s*" );
    }

    private void loadIcon( TarentGUIAction ga, Object value ) {
        if ( null == value || "".equals( value ) ) {
            logger.warning( Messages.getString( "ActionRegistry_Invalid_Attribute_SmallIcon" ) + " < "
                + ga.getUniqueName() + " >" );
            return;
        }

        Icon smallIcon = IconFactory.getInstance().getIcon( value.toString() );
        if ( smallIcon != null ) {
            ga.putValue( Action.SMALL_ICON, smallIcon );
        }
    }

    // Do some extra services for AbstractGUIAction instances
    private void initAbstractAction( TarentGUIAction ga, List containerList ) {
        if ( ga instanceof AbstractGUIAction ) {
            final AbstractGUIAction aga = (AbstractGUIAction) ga;
            assignContainers( aga, containerList );

            synchronized (singletonMonitor)
            {
              if (delayedInitializiations != null)
                {
                  // Save initialization for later execution.
                  delayedInitializiations.add(new Runnable()
                  {
                    public void run()
                    {
                      aga.init();
                    }
                  });

                  // Nothing to be done any more.
                  return;
                }
            }
            // If we get here, delayed initialization is not needed anymore
            // (IOW delayedInitiations == null) and we can run it at once.
            aga.init();
        }
    }

    private void assignContainers( AbstractGUIAction aga, List containerList ) {
        String containerDesc;
        String[] containerDescParts;

        for ( Iterator iter = containerList.iterator(); iter.hasNext(); ) {

            containerDesc = (String) iter.next();

            containerDescParts = containerDesc.split( ":", 2 );

            aga.addContainerAssignment( containerDescParts[0],
                                        ( containerDescParts.length == 2 ) ? containerDescParts[1] : null );
        }
    }

    private void setAccelerator( TarentGUIAction ga, Map.Entry entry ) {
        KeyStroke accel = KeyStroke.getKeyStroke( entry.getValue().toString() );
        if ( accel != null ) {
            ga.putValue( Action.ACCELERATOR_KEY, accel );
        }
        else {
            logger.info( "[!] could not convert '" + entry.getValue() + "' to an KeyStroke." );
        }
    }

    private void setMnemonic( TarentGUIAction ga, Map.Entry entry ) {
        int mnemonic = getKeyEventID( entry.getValue().toString() );
        if ( mnemonic != -1 ) {
            ga.putValue( Action.MNEMONIC_KEY, new Integer( mnemonic ) );
        }
    }

    private TarentGUIAction getActionInstance( Map map ) throws ActionDefinitionException {
        String uniqueName = getRequiredStringAttributeFromMap( map, TarentGUIAction.PROP_KEY_UNIQUE_NAME );
        try {
            try {
                String classString = getRequiredStringAttributeFromMap( map, TarentGUIAction.PROP_KEY_ACTION_CLASS );
                Class clazz = Class.forName( classString );
                return (TarentGUIAction) clazz.getConstructor( emptyClassArray ).newInstance( emptyObjectArray );
            }
            catch ( ClassCastException cce ) {
                throw new ActionDefinitionException( Messages.getString( "ActionRegistry_EXC_NotAnTarentGUIAction" ),
                                                     cce );
            }
            catch ( Exception e ) {
                throw new ActionDefinitionException( Messages.getString( "ActionRegistry_EXC_CreatingAction" ), e );
            }
        }
        catch ( ActionDefinitionException de ) {
            if ( uniqueName != null )
                de.setActionUniqueName( uniqueName );

            throw de;
        }
    }

    /**
     * Returns the numeric key code for a String containing the key Valid are all
     * int constants from java.lang.KeyEvent. (without there prefix VK_)
     * @return the key code, or -1 if key is an illegal argument
     */
    protected static int getKeyEventID( String key ) {
        try {
            return KeyEvent.class.getDeclaredField( "VK_" + key.toUpperCase() ).getInt( null );
        }
        catch ( Exception e ) {
            logger.info( "[!] could not convert '" + key + "' to a key code." );
            return -1;
        }
    }

    protected String getRequiredStringAttributeFromMap( Map map, String attributeKey ) throws ActionDefinitionException {

        String value = (String) map.get( attributeKey );
        if ( null == value || "".equals( value ) )

            throw new ActionDefinitionException( Messages
                .getString( "ActionRegistry_Missing_Attribute_" + attributeKey ) );
        return value;
    }

    protected Object getRequiredAttributeFromMap( Map map, String attributeKey ) throws ActionDefinitionException {
        Object value = map.get( attributeKey );
        if ( null == value )
            throw new ActionDefinitionException( Messages
                .getString( "ActionRegistry_Missing_Attribute_" + attributeKey ) );
        return value;
    }

    public boolean isContextEnabled( String edit_modus ) {
        return activeContexts.contains(edit_modus);
    }

    /**
     * Switchs from one context to another.
     * This method is more efficient than sequence of enable and disable methods.<p>
     *
     * @param from context
     * @param to context
     */
    public void switchContext( String from, String to ) {
        List fromContextList = (List) deactivationContextMap.get(from);
        List toContextList = (List) deactivationContextMap.get(to);
        logger.fine("[switch-context]: " + from + " -> " + to);
        //disable 'from' if active
        if(canHandleContext(from, false)) {
            //IMPORTANT: as first update active contexts (because of getCurrentDeactivationSet())
            activeContexts.remove(from);
            //enable actions: in 'from' & not in 'to' & not not in 'deactivation set'
            Set toEnableSet = new HashSet();
            toEnableSet.addAll(fromContextList);
            toEnableSet.removeAll(toContextList);
            toEnableSet.removeAll(getCurrentDeactivationSet());
            setActionsContextEnabled( toEnableSet, true );
            fireContextChanged(from, false);
        }
        //enable 'to' if not active
        if(canHandleContext(to, true)) {
            //disable actions: in 'to' & not not in 'deactivation set'
            Set toDisableSet = new HashSet();
            toDisableSet.addAll(toContextList);
            toDisableSet.removeAll(getCurrentDeactivationSet());
            setActionsContextEnabled( toDisableSet, false );
            //IMPORTANT: as last update active contexts (because of getCurrentDeactivationSet())
            activeContexts.add(to);
            fireContextChanged(to, true);
        }
    }

    /**
     * Sets the <code>contextEnabled</code> property of the actions in the given set.
     *
     * @param actions
     * @param toEnable
     */
    private void setActionsContextEnabled( Set actions, boolean toEnable ) {
        Iterator iter1 = actions.iterator();
        while(iter1.hasNext()){
            ((TarentGUIAction) iter1.next()).setEnabled(toEnable);
        }
    }

    /** Enables a given context, that is the set of assigned actions will be disabled.<p>
     *
     * @param context to enable
     */
    public void enableContext( String context ) {
        if(!canHandleContext(context, true)) return;
        //disable relevant actions: respecting current deactivation set
        setContextIndependendActionsEnabled( context, false );
        //IMPORTANT: as last update active contexts (because of getCurrentDeactivationSet())
        activeContexts.add( context );
        //notify listeners
        fireContextChanged( context , true);
    }

    /** Disables a given context, that is the set of assigned actions will be enabled.<p>
     *
     * @param context to disable
     */
    public void disableContext( String context ) {
        if(!canHandleContext(context, false)) return;
        //IMPORTANT: as first update active contexts (because of getCurrentDeactivationSet())
        activeContexts.remove( context );
        //enable relevant actions: respecting current deactivation set
        setContextIndependendActionsEnabled( context, true );
        //notify listeners
        fireContextChanged( context , false);
    }

    private boolean canHandleContext( String context, boolean toBeEnabled ) {
        if ( context == null || "".equals( context ) ) {
            logger.warning( "can't " + (toBeEnabled ? "enable":"disable")+" empty action context!" );
            return false;
        }
        if ( toBeEnabled == isContextEnabled( context ) ) {
            logger.info("[!] context already "+ (toBeEnabled ? "":"not")+"active: " + context );
            return false;
        }
        return true;
    }

    private void fireContextChanged( String context, boolean isEnabled ) {
        logger.fine("fire [context]: " + context + " is " + (isEnabled ? "activated" : "deactivated"));
        Iterator iter = contextChangeListeners.iterator();
        while(iter.hasNext()){
            ((ContextChangeListener) iter.next()).contextChanged(context, isEnabled);
        }
    }

    /** Enables/disables only actions that are not in deactivation set.*/
    private void setContextIndependendActionsEnabled( String context, boolean toEnable) {
        assert context != null : "[!] can't handle empty context";
        Set setToHandle = new HashSet();
        List relevantActions = (List) deactivationContextMap.get(context);
        if(relevantActions != null) {
            setToHandle.addAll(relevantActions);
            setToHandle.removeAll(getCurrentDeactivationSet());
            setActionsContextEnabled( setToHandle, toEnable );
        } else logger.fine("[!] no independent actions found");
    }

    private Set getCurrentDeactivationSet() {
        Iterator deactivationContexts = activeContexts.iterator();
        Set deactivationSet = new HashSet();
        while ( deactivationContexts.hasNext() ) {
            List currentList = (List) deactivationContextMap.get(deactivationContexts.next());
            deactivationSet.addAll(currentList);
        }
        return deactivationSet;
    }

    public AbstractGUIAction getAction( String nextUniqueName ) {
        return (AbstractGUIAction) actionsMap.get( nextUniqueName );
    }

    public boolean hasAction( String uniqueName ) {
        return ( null != uniqueName ) && ( !"".equals( uniqueName ) ) && actionsMap.containsKey( uniqueName );
    }

    /** Use it for debugging ;) */
    public void printActiveContexts() {
        for(Iterator iter = activeContexts.iterator();iter.hasNext();){
            System.out.println("[active-context]: " + iter.next());
        }

    }
}
