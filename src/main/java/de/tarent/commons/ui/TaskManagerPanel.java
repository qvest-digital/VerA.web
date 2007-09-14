/**
 * 
 */
package de.tarent.commons.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.tarent.commons.utils.TaskManager.Context;
import de.tarent.commons.utils.TaskManager.TaskListener;

/**
 * An implementation of the {@link TaskManager.TaskListener} interface which
 * is capable of displaying multiple tasks with a label and a progress bar and provides a
 * cancel button for cancelable tasks.
 * 
 * @author Fabian K&ouml;ster (f.koester@tarent.de) tarent GmbH Bonn
 *
 */
public class TaskManagerPanel extends JComponent implements TaskListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3515935584947905202L;

	protected Map<Context, TaskPanel> contexts;
	protected JList contextList;
	protected JLabel globalLabel;
	protected JProgressBar globalProgressBar;
	protected JButton globalCancelButton;
	protected JToggleButton toggleExtendedButton;
	protected Popup popup;
	protected JScrollPane contextScrollPane;

	protected final static Logger logger = Logger.getLogger(TaskManagerPanel.class.getName());

	public TaskManagerPanel() {
		setLayout(new FormLayout("pref, 3dlu, pref, 3dlu, pref, 3dlu, pref",
		"pref"));

		CellConstraints cc = new CellConstraints();
		add(getGlobalLabel(), cc.xy(1, 1));
		add(getGlobalProgressBar(), cc.xy(3, 1));
		add(getGlobalCancelButton(), cc.xy(5, 1));
		add(getToggleExtendedButton(), cc.xy(7, 1));
	}

	protected Map<Context, TaskPanel> getContexts() {
		if(contexts == null)
			contexts = Collections.synchronizedMap(new Hashtable<Context, TaskPanel>());
		return contexts;
	}
	
	protected JScrollPane getContextScrollPane() {
		if(contextScrollPane == null)
			contextScrollPane = new JScrollPane(getContextList());
		
		return contextScrollPane;
	}
	
	protected JList getContextList() {
		if(contextList == null) {
			contextList = new JList(new DefaultListModel());
			contextList.setOpaque(false);
			contextList.setCellRenderer(new ListCellRenderer() {

				/**
				 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
				 */
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					return (TaskPanel)value; 
				}

			});
		}
		return contextList;
	}
	
	protected JLabel getGlobalLabel() {
		if(globalLabel == null) {
			globalLabel = new JLabel();
			globalLabel.setVisible(false);
		}
		return globalLabel;
	}

	protected JProgressBar getGlobalProgressBar() {
		if(globalProgressBar == null) {
			globalProgressBar = new JProgressBar();
			globalProgressBar.setVisible(false);
		}
		return globalProgressBar;
	}

	protected JButton getGlobalCancelButton() {
		if(globalCancelButton == null) {
			globalCancelButton = new JButton("X");
			globalCancelButton.setVisible(false);
		}
		return globalCancelButton;
	}

	protected JToggleButton getToggleExtendedButton() {
		if(toggleExtendedButton == null) {
			toggleExtendedButton = new JToggleButton("^");
			toggleExtendedButton.setVisible(false);
			toggleExtendedButton.addActionListener(new ActionListener() {

				/**
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e) {
					if(getToggleExtendedButton().isSelected()) {
						int x = (int)(getToggleExtendedButton().getLocationOnScreen().getX()+getToggleExtendedButton().getWidth()-getContextScrollPane().getPreferredSize().getWidth());
						int y = (int)(getToggleExtendedButton().getLocationOnScreen().getY()-getContextScrollPane().getPreferredSize().getHeight());
						popup = PopupFactory.getSharedInstance().getPopup(TaskManagerPanel.this, getContextScrollPane(), x, y);
						popup.show();
					} else
						popup.hide();
				}
			});
		}
		return toggleExtendedButton;
	}

	/**
	 * @see de.tarent.commons.utils.TaskManager.TaskListener#activityDescriptionSet(de.tarent.commons.utils.TaskManager.Context, java.lang.String)
	 */
	public void activityDescriptionSet(Context t, String description) {
		TaskPanel taskPanel = getContexts().get(t);

		if(taskPanel == null) {
			logger.warning("activityDescriptionSet taskPanel null");
			return;
		}

		taskPanel.getLabel().setText(description);
	}

	/**
	 * @see de.tarent.commons.utils.TaskManager.TaskListener#currentUpdated(de.tarent.commons.utils.TaskManager.Context, int)
	 */
	public void currentUpdated(Context t, int amount) {
		logger.info("currentUpdated");
		TaskPanel taskPanel = getContexts().get(t);

		if(taskPanel == null) {
			logger.warning("currentUpdated taskPanel null");
			return;
		}

		taskPanel.getProgressBar().setValue(amount);
	}

	/**
	 * @see de.tarent.commons.utils.TaskManager.TaskListener#goalUpdated(de.tarent.commons.utils.TaskManager.Context, int)
	 */
	public void goalUpdated(Context t, int amount) {
		logger.info("goalUpdated");
		TaskPanel taskPanel = getContexts().get(t);

		if(taskPanel == null) {
			logger.warning("goalUpdated taskPanel null");
			return;
		}

		taskPanel.getProgressBar().setMaximum(amount);
	}

	/**
	 * @see de.tarent.commons.utils.TaskManager.TaskListener#taskCancelled(de.tarent.commons.utils.TaskManager.Context)
	 */
	public void taskCancelled(Context t) {
		logger.info("taskCancelled");
		removeContext(t);
	}

	/**
	 * @see de.tarent.commons.utils.TaskManager.TaskListener#taskCompleted(de.tarent.commons.utils.TaskManager.Context)
	 */
	public void taskCompleted(Context t) {
		logger.info("taskCompleted");
		removeContext(t);
	}

	/**
	 * @see de.tarent.commons.utils.TaskManager.TaskListener#taskRegistered(de.tarent.commons.utils.TaskManager.Context, java.lang.String)
	 */
	public void taskRegistered(Context t, String description) {
		logger.info("taskRegistered");
		addContext(t, description);
	}

	/**
	 * @see de.tarent.commons.utils.TaskManager.TaskListener#exclusiveTaskRegistered(de.tarent.commons.utils.TaskManager.Context, java.lang.String)
	 */
	public void exclusiveTaskRegistered(Context t, String description) {
		logger.info("exclusiveTaskRegistered");
		addContext(t, description);
	}

	/**
	 * @see de.tarent.commons.utils.TaskManager.TaskListener#blockingTaskRegistered(de.tarent.commons.utils.TaskManager.Context, java.lang.String)
	 */
	public void blockingTaskRegistered(Context t, String description) {
		logger.info("blockingTaskRegistered");
		addContext(t, description);
	}

	/**
	 * @see de.tarent.commons.utils.TaskManager.TaskListener#taskStarted(de.tarent.commons.utils.TaskManager.Context)
	 */
	public void taskStarted(Context t) {
		logger.info("taskStarted");
		setProgressLater(t, 0);
	}

	protected void addContext(Context context, String description) {
		TaskPanel taskPanel = new TaskPanel(description);
		getContexts().put(context, taskPanel);
		((DefaultListModel)getContextList().getModel()).addElement(taskPanel);
		getGlobalProgressBar().setIndeterminate(true);
		getGlobalProgressBar().setVisible(true);
		getGlobalLabel().setText(description);
		getToggleExtendedButton().setVisible(true);
	}

	protected void removeContext(Context context) {
		((DefaultListModel)contextList.getModel()).removeElement(contexts.get(context));
		contexts.remove(context);
	}

	protected void setProgressLater(final Context context, final int progress) {

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				TaskPanel taskPanel = getContexts().get(context);

				if(taskPanel == null) {
					logger.warning("setProgress taskPanel null");
					return;
				}

				if (progress == 0) {
					taskPanel.setVisible(true);
					taskPanel.getCancelButton().setVisible(context != null && context.isCancelable());
					taskPanel.getCancelButton().setEnabled(true);

					if (taskPanel.getProgressBar().getMaximum() == 0)
						taskPanel.getProgressBar().setIndeterminate(true);
				}
				else
					taskPanel.getProgressBar().setValue(progress);
			}
		});
	}

	protected class TaskPanel extends JComponent {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4717031274747790529L;

		protected JProgressBar progressBar;
		protected JLabel label;
		protected JButton cancelButton;
		protected String description;

		public TaskPanel(String description) {
			super();

			this.description = description;

			FormLayout layout = new FormLayout("fill:pref:grow, 1dlu, pref", // columns
			"fill:pref, 1dlu, fill:pref"); // rows
			
			//layout.setRowGroups(new int[][] { { 1, 3} });

			setLayout(layout);
			
			CellConstraints cc = new CellConstraints();

			add(getProgressBar(), cc.xy(1, 1));
			add(getCancelButton(), cc.xy(3, 1));
			add(getLabel(), cc.xyw(1, 3, 3));
			
			getLabel().setText(description);
			setBorder(BorderFactory.createLoweredBevelBorder());
		}

		public JProgressBar getProgressBar() {
			if(progressBar == null)
				progressBar = new JProgressBar();
			return progressBar;
		}

		public JLabel getLabel() {
			if(label == null)
				label = new JLabel();
			return label;
		}

		public JButton getCancelButton() {
			if(cancelButton == null)
				cancelButton = new JButton("x");
			return cancelButton;
		}
	}
}
