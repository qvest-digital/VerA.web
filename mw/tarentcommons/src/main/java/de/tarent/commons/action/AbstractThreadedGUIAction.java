package de.tarent.commons.action;

import java.awt.event.ActionEvent;

/**
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */
public abstract class AbstractThreadedGUIAction extends AbstractGUIAction implements Runnable
{
	ActionEvent event = null;

	public void actionPerformed(ActionEvent pEvent)
	{
		event = pEvent;
		new Thread(this).start();
	}

	public void run()
	{
		if(event != null) eventOccured(event);
	}

	/**
	 *
	 * Threaded counter-part to actionPerformed in <code>AbstractGUIAction</code>
	 *
	 * @param pEvent
	 */

	protected abstract void eventOccured(ActionEvent pEvent);

}
