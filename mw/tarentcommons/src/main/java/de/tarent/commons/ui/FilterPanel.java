/**
 *
 */
package de.tarent.commons.ui;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.tarent.commons.datahandling.ListFilter;

/**
 * @author Fabian K&ouml;ster (f.koester@tarent.de) tarent GmbH Bonn
 *
 */
public class FilterPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 8500774231416921033L;
	protected DescriptiveTextField textField;
	protected JComboBox filterSelection;

	// java 1.5: protected List<ListFilter> availableFilters;
	protected List availableFilters;

	protected List filterChangeListeners;

	// java 1.5: public FilterPanel(List<ListFilter> availableFilters) {
	public FilterPanel(List availableFilters) {

		this.availableFilters = availableFilters;

		FormLayout layout = new FormLayout("fill:pref:grow, 2dlu, pref", // columns
				"fill:pref"); // rows

		layout.setColumnGroups(new int[][] { { 1, 3 } });

		setLayout(layout);

		CellConstraints cc = new CellConstraints();

		add(getTextField(), cc.xy(1, 1));
		add(getFilterSelection(), cc.xy(3, 1));
	}

	protected DescriptiveTextField getTextField() {
		if(textField == null) {
			textField = new DescriptiveTextField("Enter search params here...");
			textField.getDocument().addDocumentListener(new DocumentListener() {

				/**
				 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
				 */
				public void changedUpdate(DocumentEvent e) {
					fireFilterChanged();
				}

				/**
				 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
				 */
				public void insertUpdate(DocumentEvent e) {
					fireFilterChanged();
				}

				/**
				 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
				 */
				public void removeUpdate(DocumentEvent e) {
					fireFilterChanged();
				}

			});
		}

		return textField;
	}

	protected JComboBox getFilterSelection() {
		if(filterSelection == null) {
			filterSelection = new JComboBox(availableFilters.toArray());
			filterSelection.setRenderer(new DefaultListCellRenderer() {

				/**
				 *
				 */
				private static final long serialVersionUID = 3011751938668796062L;

				/**
				 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
				 */
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					return super.getListCellRendererComponent(list, ((ListFilter)value).getFilterName(), index, isSelected,
							cellHasFocus);
				}

			});
			filterSelection.addItemListener(new ItemListener() {

				/**
				 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
				 */
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED)
						fireFilterChanged();
				}
			});
			filterSelection.addMouseWheelListener(new ComboBoxMouseWheelNavigator(filterSelection));
		}

		return filterSelection;
	}

	public ListFilter getActiveFilter() {
		return (ListFilter)getFilterSelection().getSelectedItem();
	}

	public void addFilterChangeListener(FilterChangeListener listener) {
		getFilterChangeListeners().add(listener);
	}

	public void removeFilterChangeListener(FilterChangeListener listener) {
		getFilterChangeListeners().remove(listener);
	}

	protected List getFilterChangeListeners() {
		if(filterChangeListeners == null)
			filterChangeListeners = new ArrayList();
		return filterChangeListeners;
	}

	protected void fireFilterChanged() {
		Iterator it = getFilterChangeListeners().iterator();

		while(it.hasNext())
			((FilterChangeListener)it.next()).filterChanged((ListFilter)getFilterSelection().getSelectedItem());
	}

	public interface FilterChangeListener {
		public void filterChanged(ListFilter newFilter);
	}
}
