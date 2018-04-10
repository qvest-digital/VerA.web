package de.tarent.commons.ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

/**
 * This implements a quality feedback agent that can be started whenever
 * an application crashes. It displays a window collecting additional information
 * to be sent along with technical information to a support team.
 * <p>
 * To use this in an application, you have to:
 * <ul>
 * <li>Inherit this abstract class and implement send().</li>
 * <li>Catch RuntimeException from your main loop and start
 * a quality feedback agent if thrown.</li>
 * </ul>
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public abstract class QualityFeedbackAgent extends JDialog
{
    private JPanel jContentPane = null;
    private JButton sendButton = null;
    private JButton detailsButton = null;
    private JButton dontSendButton = null;
    private JTextPane jTextPane = null;
    private JTextField emailField = null;
    private JLabel jLabel2 = null;
    private JLabel jLabel3 = null;
    private JTextArea doneTextArea = null;
    private Exception exception = null;
    private String message = null;
    private JScrollPane doneTextAreaPane = null;

    private class ImagePanel extends JPanel
    {
        /** serialVersionUID */
		private static final long serialVersionUID = -2575886879594471370L;

		public ImagePanel()
        {
            super();
            this.setBounds(new java.awt.Rectangle(0,0,120,469));
        }

        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            Image aboutImage = null;
            try
            {
                URL url = getClass().getResource("/de/tarent/commons/gfx/qfba-splash.png");
                aboutImage = javax.imageio.ImageIO.read(url);
            }
            catch (IOException ioe)
            {
                throw new RuntimeException("Can't open about image.");
            }

            g.drawImage(aboutImage, 0, 0, this);
        }
    }

    /**
     * This creates a new Quality Feedback Agent.
     *
     * @param message Message to be displayed in the detail dialog.
     * @param exception Exception that triggered the crash.
     */
    public QualityFeedbackAgent(String message, Exception exception)
    {
        super();
        this.exception = exception;
        this.message = message;
        initialize();
    }

    private void initialize()
    {
        this.setModal(true);
// FIXME       this.setAlwaysOnTop(true);
        this.setTitle("tarent Quality Feedback Agent");
        this.setSize(500,500);
 // FIXME       this.setPreferredSize(new Dimension(300, 200));
        this.setContentPane(getJContentPane());
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private JPanel getJContentPane()
    {
        if (jContentPane == null)
        {
            jLabel3 = new JLabel();
            jLabel3.setBounds(new java.awt.Rectangle(134,190,341,21));
            jLabel3.setText("Describe what you were doing (optional)");
            jLabel2 = new JLabel();
            jLabel2.setBounds(new java.awt.Rectangle(134,125,219,20));
            jLabel2.setText("Your email address (optional)");
            jContentPane = new JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(new ImagePanel());
            jContentPane.add(getSendButton(), null);
            jContentPane.add(getDetailsButton(), null);
            jContentPane.add(getDontSendButton(), null);
            jContentPane.add(getJTextPane(), null);
            jContentPane.add(getEmailField(), null);
            jContentPane.add(jLabel2, null);
            jContentPane.add(jLabel3, null);
            jContentPane.add(getDoneTextAreaPane(), null);

        }
        return jContentPane;
    }

    private JButton getSendButton()
    {
        if (sendButton == null)
        {
            sendButton = new JButton();
            sendButton.setText("Send");
            sendButton.setBounds(new java.awt.Rectangle(316,430,64,25));
            sendButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    QualityFeedbackAgent.this.send(emailField.getText(), doneTextArea.getText(), message, exception);
                    QualityFeedbackAgent.this.dispose();
                    System.exit(1);
                }
            });
        }
        return sendButton;
    }

    private JButton getDetailsButton()
    {
        if (detailsButton == null)
        {
            detailsButton = new JButton();
            detailsButton.setText("Details");
            detailsButton.setBounds(new java.awt.Rectangle(395,430,78,25));
            detailsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    new QualityFeedbackAgentDetailDialog(message, exception);
                }
            });
        }
        return detailsButton;
    }

    private JButton getDontSendButton()
    {
        if (dontSendButton == null)
        {
            dontSendButton = new JButton();
            dontSendButton.setText("Don't send");
            dontSendButton.setBounds(new java.awt.Rectangle(199,430,102,25));
            dontSendButton.addActionListener(new java.awt.event.ActionListener()
                    {
                        public void actionPerformed(java.awt.event.ActionEvent e)
                        {
                            QualityFeedbackAgent.this.setVisible(false);
                            QualityFeedbackAgent.this.dispose();
                            System.exit(1);
                        }
                    });
        }
        return dontSendButton;
    }

    private JTextPane getJTextPane()
    {
        if (jTextPane == null)
        {
            jTextPane = new JTextPane();
            jTextPane.setEditable(false);
            jTextPane.setBackground(jContentPane.getBackground());
            jTextPane.setFont(new Font("Helvetica", Font.PLAIN, 12));
            jTextPane.setText("The tarent quality feedback agent has captured information that tarent needs to improve this products quality.\n\nEnter your email address (optional), describe how you were using this product (optional), then click Send.");
            jTextPane.setBounds(new java.awt.Rectangle(134,15,342,104));
        }
        return jTextPane;
    }

    private JTextField getEmailField()
    {
        if (emailField == null)
        {
            emailField = new JTextField();
            emailField.setBounds(new java.awt.Rectangle(134,150,343,23));
        }
        return emailField;
    }

    private JScrollPane getDoneTextAreaPane()
    {
        if (doneTextAreaPane == null)
        {
            doneTextAreaPane = new JScrollPane(getDoneTextArea());
            doneTextAreaPane.setBorder(emailField.getBorder());
            doneTextAreaPane.setBounds(new java.awt.Rectangle(134,215,339,195));
        }
        return doneTextAreaPane;
    }

    private JTextArea getDoneTextArea()
    {
        if (doneTextArea == null)
        {
            doneTextArea = new JTextArea();
            doneTextArea.setAutoscrolls(true);
            doneTextArea.setWrapStyleWord(true);
            doneTextArea.setLineWrap(true);
        }
        return doneTextArea;
    }

    // abstract methods follow

    /**
     * This method has to be implemented in all explicit quality feedback agents. It will
     * be called from the qfa when the user klicks on the send button. The text entered in
     * the dialog will be attached as parameters. The implementing method chooses the
     * transport method for sending out the message.
     */
    public abstract void send(String email, String doneText, String message, Exception exception);
}
