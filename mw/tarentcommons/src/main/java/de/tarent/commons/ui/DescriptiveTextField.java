/**
 *
 */
package de.tarent.commons.ui;

import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 *
 * A Textfield which can display an informative text when it is not focused.
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de) tarent GmbH Bonn
 *
 */
public class DescriptiveTextField extends JTextField {

	/**
	 *
	 */
	private static final long serialVersionUID = -8782822484717981539L;

	protected FocusListener focusListener;
	protected Document descDoc;
	protected Document editDoc;

	public DescriptiveTextField() {
		super();
		this.addFocusListener(getFocusListener());
	}

	public DescriptiveTextField(String text) {
		super();

		editDoc = getDocument();

		setDescription(text);
	}

	public void setDescription(String description) {
		try {
			if(descDoc == null)
				descDoc = new PlainDocument();

			if (descDoc instanceof AbstractDocument) {
				((AbstractDocument)descDoc).replace(0, descDoc.getLength(), description ,null);
			}
			else {
				descDoc.remove(0, descDoc.getLength());
				descDoc.insertString(0, description, null);
			}
		} catch (BadLocationException e) {
			UIManager.getLookAndFeel().provideErrorFeedback(DescriptiveTextField.this);
		}
		if(!hasFocus())
			showDescriptiveText();
	}

	protected FocusListener getFocusListener() {
		if(focusListener == null) {
			focusListener = new FocusListener() {
				public void focusLost(FocusEvent event) {
					if(getText().trim().length() == 0)
						DescriptiveTextField.this.showDescriptiveText();
				}

				public void focusGained(FocusEvent event) {
					DescriptiveTextField.this.showInputText();
				}
			};
		}
		return focusListener;
	}

	protected void showInputText() {
		setFont(getFont().deriveFont(Font.PLAIN));
		setDocument(editDoc);
		repaint();
	}

	protected void showDescriptiveText() {
		setDocument(descDoc);
		setFont(getFont().deriveFont(Font.ITALIC));
		repaint();

	}

	public void setText(String text) {
		super.setText(text);
		if(text.trim().length() == 0)
			showDescriptiveText();
	}

	public Document getDocument() {
		if(editDoc == null)
			editDoc = super.getDocument();
		return editDoc;
	}
}
