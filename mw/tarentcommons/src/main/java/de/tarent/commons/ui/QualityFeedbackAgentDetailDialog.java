package de.tarent.commons.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JScrollPane;

/**
 * This class displays the qfba details dialog.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public class QualityFeedbackAgentDetailDialog extends JDialog
{
    /** serialVersionUID */
	private static final long serialVersionUID = -3038149248710691582L;
	private JPanel jContentPane = null;
    private JButton closeButton = null;
    private String message = null;

    /**
     * Constructs a new qfba details dialog. It displays the given text message.
     *
     * @param pMessage Text message.
     */
    public QualityFeedbackAgentDetailDialog(String pMessage)
    {
	this(pMessage, null);
    }

    /**
     * Constructs a new qfba details dialog. It displays the given text message and the given exception.
     *
     * @param pMessage Text message.
     * @param pException a Exception
     */

    public QualityFeedbackAgentDetailDialog(String pMessage, Exception pException)
    {
	super();
	this.setModal(true);
	this.setTitle("Details");
	this.message = pMessage;
	if(pException != null)
	{
		this.message += "\r\n\nTechnical Information:\r\n"+pException.toString();

		StackTraceElement[] stackTrace = pException.getStackTrace();
		for(int i=0; i < stackTrace.length; i++)
		{
			this.message += "\r\n"+stackTrace[i].toString();
		}
	}
	initialize();
    }

    private void initialize()
    {
	this.setContentPane(getJContentPane());
	this.pack();
	this.setLocationRelativeTo(null);
	this.setVisible(true);
    }

    private JPanel getJContentPane()
    {
	if (jContentPane == null)
	{
	    jContentPane = new JPanel();
	    jContentPane.setLayout(new BorderLayout());
	    jContentPane.add(getCloseButton(), java.awt.BorderLayout.SOUTH);

	    JEditorPane pane = new JEditorPane();
	    pane.setContentType("text/plain");
	    pane.setEditorKit(JEditorPane.createEditorKitForContentType("text/plain"));
	    pane.setFocusable( false );
	    pane.setEditable(false);
	    pane.setText(message);

	    JScrollPane scrollPane = new JScrollPane(pane);

	    jContentPane.setPreferredSize(new Dimension(400, 300));
	    jContentPane.add(scrollPane, "Center");
	}
	return jContentPane;
    }

    private JButton getCloseButton()
    {
	if (closeButton == null)
	{
	    closeButton = new JButton();
	    closeButton.setText("Close");
	    closeButton.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    QualityFeedbackAgentDetailDialog.this.setVisible(false);
		    QualityFeedbackAgentDetailDialog.this.dispose();
		}
	    });
	}
	return closeButton;
    }
}
