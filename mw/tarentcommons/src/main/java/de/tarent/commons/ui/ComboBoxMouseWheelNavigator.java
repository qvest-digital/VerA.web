package de.tarent.commons.ui;

import javax.swing.JComboBox;

/**
 *
 * <p>The ComboBoxMouseWheelNavigator is intended to be an extension to {@link JComboBox}es,
 * for using the mouse-wheel to navigate fast and convenient through the entries of a {@link JComboBox}.</p>
 *
 * <p>Inspired by the KDE Konqueror (<a href="http://www.konqueror.org">konqueror.org</a>),
 * but can be found in several Applications and Desktop Environments nowadays.</p>
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */

public class ComboBoxMouseWheelNavigator extends AbstractMouseWheelNavigator
{
	private JComboBox comboBox;

	/**
	 * <p>Constructs a ComboBoxMouseWheelNavigator for the given JComboBox.</p>
	 * <p>Same as ComboBoxMouseWheelNavigator(pComboBox, true)</p>
	 *
	 * @param pComboBox the JComboBox to navigate on
	 */

	public ComboBoxMouseWheelNavigator(JComboBox pComboBox)
	{
		comboBox = pComboBox;
	}

	/**
	 * Constructs a ComboBoxMouseWheelNavigator for the given JComboBox. Allows you to determine whether we jump over the boundaries.
	 *
	 * @param pComboBox the JComboBox to navigate on
	 * @param pJumpOverBoundaries whether we jump from the last entry to the first and the other way around
	 */

	public ComboBoxMouseWheelNavigator(JComboBox pComboBox, boolean pJumpOverBoundaries)
	{
		super(pJumpOverBoundaries);
		comboBox = pComboBox;
	}

	protected int getCurrentIndex()
	{
		if(comboBox != null) return comboBox.getSelectedIndex();
		return 0;
	}

	protected int getMaxIndex()
	{
		if(comboBox != null) return comboBox.getItemCount()-1;
		return 0;
	}

	protected boolean isValidIndex(int pIndex)
	{
		return (comboBox != null);
	}

	protected void setIndex(int pIndex)
	{
		if(comboBox != null) comboBox.setSelectedIndex(pIndex);
	}

	protected boolean isComponentEnabled()
	{
		return (comboBox != null && comboBox.isEnabled());
	}
}
