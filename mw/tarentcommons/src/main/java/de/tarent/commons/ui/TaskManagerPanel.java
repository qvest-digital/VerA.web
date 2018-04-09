/**
 *
 */
package de.tarent.commons.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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

import de.tarent.commons.utils.TaskManager;
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
	protected ImageIcon cancelIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/de/tarent/commons/gfx/process-stop.png")));
//	protected ImageIcon collapsed = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/de/tarent/commons/gfx/expanded.gif")));
//	protected ImageIcon expanded = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/de/tarent/commons/gfx/collapsed.gif")));
	protected Context currentContext;

	protected int mode;

	protected final static Logger logger = Logger.getLogger(TaskManagerPanel.class.getName());
	protected final static int SINGLE_MODE = 0;
	protected final static int MULTI_MODE = 1;
	protected final static int NONE_MODE = 2;

	public TaskManagerPanel() {
		this(null);
	}

	public TaskManagerPanel(final Component resizeComp) {
		// we should listen on component resizes in order to ensure correct position of popup
		if(resizeComp != null)
			resizeComp.addComponentListener(new ComponentAdapter() {

				/**
				 * @see java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)
				 */
				@Override
				public void componentResized(ComponentEvent e) {
					if(resizeComp.isVisible())
						TaskManagerPanel.this.showPopup();
				}

			});

		setLayout(new FormLayout("pref, 3dlu, pref, 3dlu, pref, 3dlu, pref",
		"1dlu, fill:pref, 1dlu"));

		CellConstraints cc = new CellConstraints();
		add(getGlobalLabel(), cc.xy(1, 2));
		add(getGlobalProgressBar(), cc.xy(3, 2));
		add(getGlobalCancelButton(), cc.xy(5, 2));
		add(getToggleExtendedButton(), cc.xy(7, 2));
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
			contextList.addMouseListener(new MouseAdapter() {

				protected MouseEvent mousePressedEvent;

				public Component getComponentAt(MouseEvent e) {
					int index = getContextList().locationToIndex(e.getPoint());
					int y = e.getY() - getContextList().indexToLocation(index).y;
					return ((TaskPanel)getContextList().getModel().getElementAt(index)).getComponentAt(e.getX(), y);
				}

				public Rectangle getRepaintBounds(MouseEvent e) {
					int index = getContextList().locationToIndex(e.getPoint());
					Point p = getContextList().indexToLocation(index);
					TaskPanel taskPanel = (TaskPanel)getContextList().getModel().getElementAt(index);
					return new Rectangle(p.x, p.y, taskPanel.getPreferredSize().width, taskPanel.getPreferredSize().height);
				}

				/**
				 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
				 */
				public void mouseClicked(MouseEvent e) {
					Component c = getComponentAt(e);
					if (c instanceof JButton) {
						c.dispatchEvent(new MouseEvent(c, e.getID(), e.getWhen(), e.getModifiers(), 0, 0, e.getClickCount(), e.isPopupTrigger(), e.getButton()));
						getContextList().repaint(getRepaintBounds(e));
					}
				}

				/**
				 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
				 */
				public void mousePressed(MouseEvent e) {
					Component c = getComponentAt(e);
					mousePressedEvent = e;
					if (c instanceof JButton) {
						c.dispatchEvent(new MouseEvent(c, e.getID(), e.getWhen(), e.getModifiers(), 0, 0, e.getClickCount(), e.isPopupTrigger(), e.getButton()));
						getContextList().repaint(getRepaintBounds(e));
					}
				}

				/**
				 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
				 */
				public void mouseReleased(MouseEvent e) {
					Component c = getComponentAt(mousePressedEvent);
					if (c instanceof JButton) {
						c.dispatchEvent(new MouseEvent(c, MouseEvent.MOUSE_RELEASED, e.getWhen(), e.getModifiers(), 0, 0, e.getClickCount(), e.isPopupTrigger(), e.getButton()));
						getContextList().repaint(getRepaintBounds(mousePressedEvent));
					}
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
			globalProgressBar.setIndeterminate(true);
		}
		return globalProgressBar;
	}

	protected JButton getGlobalCancelButton() {
		if(globalCancelButton == null) {
			globalCancelButton = new JButton(cancelIcon);
			//globalCancelButton = new JButton("x");
			globalCancelButton.setVisible(false);
			globalCancelButton.addActionListener(new ActionListener() {

				/**
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e) {
					// Make the button unclickable instantly because a task
					// should not be cancelled twice.
					getGlobalCancelButton().setEnabled(false);

					new Thread()
					{
						public void run()
						{
							if (getContexts().keySet().toArray()[0] != null)
								((Context)getContexts().keySet().toArray()[0]).cancel();
						}
					}.start();
				}
			});
		}
		return globalCancelButton;
	}

	protected JToggleButton getToggleExtendedButton() {
		if(toggleExtendedButton == null) {
			//toggleExtendedButton = new JToggleButton(collapsed);
			toggleExtendedButton = new JToggleButton("^");
			toggleExtendedButton.setPreferredSize(new Dimension(toggleExtendedButton.getPreferredSize().width, 1));
			toggleExtendedButton.setVisible(false);
			toggleExtendedButton.setToolTipText(Messages.getString("TaskManagerPanel_ExtendedButton_ToolTip"));
			toggleExtendedButton.addActionListener(new ActionListener() {

				/**
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e) {
					if(getToggleExtendedButton().isSelected()) {
						//getToggleExtendedButton().setIcon(expanded);
						showPopup();
					} else {
						popup.hide();
						//getToggleExtendedButton().setIcon(collapsed);
					}
				}
			});
		}
		return toggleExtendedButton;
	}

	protected void showPopup() {
		if(popup != null)
			popup.hide();

		if(getToggleExtendedButton().isVisible()) {
			int x = (int)(getToggleExtendedButton().getLocationOnScreen().getX()+getToggleExtendedButton().getWidth()-getContextScrollPane().getPreferredSize().getWidth());
			int y = (int)(getToggleExtendedButton().getLocationOnScreen().getY()-getContextScrollPane().getPreferredSize().getHeight());
			popup = PopupFactory.getSharedInstance().getPopup(TaskManagerPanel.this, getContextScrollPane(), x, y);
			popup.show();
		}
	}

	/**
	 * @see de.tarent.commons.utils.TaskManager.TaskListener#activityDescriptionSet(de.tarent.commons.utils.TaskManager.Context, java.lang.String)
	 */
	public void activityDescriptionSet(Context t, String description) {
		if(mode == MULTI_MODE) {
			synchronized(getContexts()) {
				TaskPanel taskPanel = getContexts().get(t);

				if(taskPanel == null) {
					logger.warning("activityDescriptionSet taskPanel null");
					return;
				}

				taskPanel.getLabel().setText(description);
			}
			getContextList().repaint();
		} else
			getGlobalLabel().setText(description);
	}

	/**
	 * @see de.tarent.commons.utils.TaskManager.TaskListener#currentUpdated(de.tarent.commons.utils.TaskManager.Context, int)
	 */
	public void currentUpdated(Context t, int amount) {
		logger.fine("currentUpdated");

		if(mode == MULTI_MODE) {
			synchronized (getContexts()) {
				TaskPanel taskPanel = getContexts().get(t);

				if(taskPanel == null) {
					logger.warning("currentUpdated taskPanel null");
					return;
				}

				taskPanel.getProgressBar().setValue(amount);
			}
			getContextList().repaint();
		} else
			getGlobalProgressBar().setValue(amount);
	}

	/**
	 * @see de.tarent.commons.utils.TaskManager.TaskListener#goalUpdated(de.tarent.commons.utils.TaskManager.Context, int)
	 */
	public void goalUpdated(Context t, int amount) {
		logger.info("goalUpdated");

		if(mode == MULTI_MODE) {
			synchronized (getContexts()) {
				TaskPanel taskPanel = getContexts().get(t);

				if(taskPanel == null) {
					logger.warning("goalUpdated taskPanel null");
					return;
				}

				taskPanel.getProgressBar().setMaximum(amount);
			}
			getContextList().repaint();
		} else
			getGlobalProgressBar().setMaximum(amount);
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
		TaskPanel taskPanel = new TaskPanel(context, description);
		getContexts().put(context, taskPanel);
		((DefaultListModel)getContextList().getModel()).addElement(taskPanel);
		setGlobalElementsState();
	}

	protected void removeContext(Context context) {
		synchronized (getContexts()) {
			((DefaultListModel)contextList.getModel()).removeElement(getContexts().get(context));
			getContexts().remove(context);
			setGlobalElementsState();
		}
	}

	protected void setGlobalElementsState() {
		synchronized (getContexts()) {
			if(getContexts().size() == 0) {
				// no tasks running, hide components
				mode = NONE_MODE;
				getGlobalLabel().setVisible(false);
				getGlobalProgressBar().setVisible(false);
				getGlobalCancelButton().setVisible(false);
				getToggleExtendedButton().setVisible(false);

				// if visible, also hide popup and deselect toggle-button
				if(popup != null)
					popup.hide();

				getToggleExtendedButton().setSelected(false);
				//getToggleExtendedButton().setIcon(collapsed);

			} else if(getContexts().size() == 1) {
				// single task running, visualize this tasks with the global elements and hide extended-button
				mode = SINGLE_MODE;
				getGlobalProgressBar().setVisible(true);
				getGlobalLabel().setVisible(true);

			} else {
				// multiple tasks running
				mode = MULTI_MODE;
				getGlobalCancelButton().setEnabled(false);
				getGlobalCancelButton().setVisible(false);
				getGlobalProgressBar().setIndeterminate(true);
				getGlobalProgressBar().setVisible(true);
				getToggleExtendedButton().setVisible(true);
				getGlobalLabel().setVisible(true);
				getGlobalLabel().setText(Messages.getString("TaskManagerPanel_Multiple_Tasks_Running"));
			}
		}
	}

	protected void setProgressLater(final Context context, final int progress) {

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				TaskPanel taskPanel;

				if(mode == MULTI_MODE) {

					synchronized (getContexts()) {
						taskPanel = getContexts().get(context);
					}

					if(taskPanel == null) {
						logger.warning("setProgress taskPanel null");
						return;
					}

					if (progress == 0) {
						taskPanel.getCancelButton().setVisible(context != null && context.isCancelable());
						taskPanel.getCancelButton().setEnabled(true);

						if (taskPanel.getProgressBar().getMaximum() == 0)
							taskPanel.getProgressBar().setIndeterminate(true);
					}
					else {
						taskPanel.getProgressBar().setIndeterminate(false);
						taskPanel.getProgressBar().setValue(progress);
					}
				} else {
					if (progress == 0) {
						getGlobalCancelButton().setVisible(context != null && context.isCancelable());
						getGlobalCancelButton().setEnabled(true);

						if (getGlobalProgressBar().getMaximum() == 0)
							getGlobalProgressBar().setIndeterminate(true);
					}
					else {
						getGlobalProgressBar().setIndeterminate(false);
						getGlobalProgressBar().setValue(progress);
					}
				}
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
		protected Context context;

		public TaskPanel(Context context, String description) {
			super();

			this.description = description;

			FormLayout layout = new FormLayout("fill:pref:grow, 1dlu, pref", // columns
			"fill:pref, 1dlu, pref"); // rows

			setLayout(layout);

			CellConstraints cc = new CellConstraints();

			add(getProgressBar(), cc.xy(1, 1));
			add(getCancelButton(), cc.xy(3, 1));
			add(getLabel(), cc.xyw(1, 3, 3));

			getLabel().setText(description);
			getCancelButton().setToolTipText(Messages.getFormattedString("SmallTaskManagerPanel_CancelProgress", description));
			//setBorder(BorderFactory.createLoweredBevelBorder());
			setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
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
			if(cancelButton == null) {
				cancelButton = new JButton(TaskManagerPanel.this.cancelIcon);
				//cancelButton = new JButton("x");
				cancelButton.setPreferredSize(new Dimension(cancelButton.getPreferredSize().width, 1));
				cancelButton.addActionListener(new ActionListener() {

					/**
					 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
					 */
					public void actionPerformed(ActionEvent e) {

						// Make the button unclickable instantly because a task
						// should not be cancelled twice.
						getCancelButton().setEnabled(false);

						new Thread()
						{
							public void run()
							{
								if (TaskPanel.this.context != null)
									TaskPanel.this.context.cancel();
							}
						}.start();
					}

				});
			}
			return cancelButton;
		}

		public Component getComponentAt(int x, int y) {
            for (int i = 0; i < getComponentCount(); i++)
                if (getComponent(i).getBounds().contains(x, y))
                    return getComponent(i);

            return null;
        }
	}

	protected class RelocatingPopup extends Popup {
		public RelocatingPopup() {

		}
	}
}
