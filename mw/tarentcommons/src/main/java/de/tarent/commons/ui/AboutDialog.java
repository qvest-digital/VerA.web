/**
 *
 */
package de.tarent.commons.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 *
 * A simple AboutDialog showing information about the program like
 * versions or contributions
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */
public class AboutDialog extends EscapeDialog implements ClipboardOwner
{
	/**
	 *
	 */
	private static final long serialVersionUID = 6631158858725021221L;
	protected JPanel mainPanel;
	protected JPanel versionPanel;
	protected JPanel serverPanel;
	protected JPanel systemPanel;
	protected JPanel authorsPanel;
	protected JPanel contributionsPanel;
	protected JPanel licensePanel;
	protected JPanel buttonPanel;
	protected JTabbedPane tabbedPane;
	protected JButton contributionButton;
	protected JButton licenseButton;
	protected JButton closeButton;
	protected JButton copyToClipboardButton;
	protected JButton showCompleteURLButton;

	protected ImageIcon productLogo;
	protected String productName;
	protected String productDesc;
	protected String version;
	protected String build;
	protected String server;
	protected String database;
	protected String contributionsFile;
	protected String licenseFile;

	protected final static Logger logger = Logger.getLogger(AboutDialog.class.getName());

	public AboutDialog(Frame owner,  ImageIcon productLogo, String productName, String productDesc, String version, String build, String server, String database, String contributionsFile, String licenseFile)
	{
		super(owner, true);
		this.productLogo = productLogo;
		this.productName = productName;
		this.productDesc = productDesc;
		this.version = version;
		this.build = build;
		this.server = server;
		this.database = database;
		this.contributionsFile = contributionsFile;
		this.licenseFile = licenseFile;

		setTitle(Messages.getFormattedString("About_Title", productName));

		setContentPane(getMainPanel());

		getRootPane().setDefaultButton(getCloseButton());
	}

	protected JPanel getMainPanel() {
		if(mainPanel == null) {
			FormLayout layout = new FormLayout(
					"pref:grow", // columns
			productLogo == null ? "0dlu, pref, pref, 15dlu, fill:pref:grow, 15dlu, pref" :
				"5dlu, pref, 0dlu, 5dlu, fill:pref:grow, 15dlu, pref"); // rows

			PanelBuilder builder = new PanelBuilder(layout);

			builder.setDefaultDialogBorder();

			CellConstraints cc = new CellConstraints();

			if(productLogo == null) {
				builder.add(createPlainLabel("<html><h1>"+productName+"</h1></html>"), cc.xy(1, 2));
				builder.add(createPlainLabel(productDesc), cc.xy(1, 3));
			} else
				builder.add(new JLabel(productLogo, SwingConstants.LEFT), cc.xy(1, 2));

			builder.add(getTabbedPane(), cc.xy(1, 5));

			builder.add(getButtonPanel(), cc.xy(1, 7));

			mainPanel = builder.getPanel();
		}
		return mainPanel;
	}

	protected JTabbedPane getTabbedPane() {
		if(tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.setFont(tabbedPane.getFont().deriveFont(Font.PLAIN));
			tabbedPane.addMouseWheelListener(new TabbedPaneMouseWheelNavigator(tabbedPane));

			tabbedPane.addTab(Messages.getString("About_Tab_Version"), getVersionPanel());
			tabbedPane.addTab(Messages.getString("About_Tab_Server"), getServerPanel());
			tabbedPane.addTab(Messages.getString("About_Tab_System"), getSystemPanel());
			if(contributionsFile != null)
				tabbedPane.addTab(Messages.getString("About_Tab_Contributions"), getContributionsPanel());
			if(licenseFile != null)
				tabbedPane.addTab(Messages.getString("About_Tab_License"), getLicensePanel());
		}
		return tabbedPane;
	}

	protected JPanel getVersionPanel()
	{
		if(versionPanel == null)
		{
			FormLayout layout = new FormLayout(
					"pref:grow", // columns
			"pref:grow, pref, 5dlu, pref, pref:grow"); // rows

			PanelBuilder builder = new PanelBuilder(layout);

			builder.setDefaultDialogBorder();

			CellConstraints cc = new CellConstraints();

			builder.add(createPlainLabel(Messages.getFormattedString("About_Version", version)), cc.xy(1, 2));
			builder.add(createPlainLabel(Messages.getFormattedString("About_Build", build)), cc.xy(1, 4));

			versionPanel = builder.getPanel();
		}
		return versionPanel;
	}

	protected JPanel getSystemPanel() {
		if(systemPanel == null) {
			FormLayout layout = new FormLayout(
					"pref:grow", // columns
			"pref:grow, pref, 5dlu, pref, 5dlu, pref, pref:grow"); // rows

			PanelBuilder builder = new PanelBuilder(layout);

			builder.setDefaultDialogBorder();

			CellConstraints cc = new CellConstraints();

			builder.add(createPlainLabel(Messages.getFormattedString("About_JRE", (Object)System.getProperty("java.vendor"), (Object)System.getProperty("java.vendor.url"), (Object)System.getProperty("java.version"))), cc.xy(1, 2));

			builder.add(createPlainLabel(Messages.getFormattedString("About_JVM", (Object)System.getProperty("java.vm.vendor"), (Object)System.getProperty("java.vm.name"), (Object)System.getProperty("java.vm.version"))), cc.xy(1, 4));

			builder.add(createPlainLabel(Messages.getFormattedString("About_OS", (Object)System.getProperty("os.name"), (Object)System.getProperty("os.arch"), (Object)System.getProperty("os.version"))), cc.xy(1, 6));

			systemPanel = builder.getPanel();
		}
		return systemPanel;
	}

	protected JPanel getServerPanel() {
		if(serverPanel == null) {
			FormLayout layout = new FormLayout(
					"pref:grow", // columns
			"pref:grow, pref, 5dlu, pref, pref:grow"); // rows

			PanelBuilder builder = new PanelBuilder(layout);

			builder.setDefaultDialogBorder();

			CellConstraints cc = new CellConstraints();

			builder.add(createPlainLabel(Messages.getFormattedString("About_Server", server != null ? server : Messages.getString("About_Server_unknown"))), cc.xy(1, 2));
			builder.add(createPlainLabel(Messages.getFormattedString("About_Database", database != null ? database : Messages.getString("About_Database_unknown"))), cc.xy(1, 4));

			serverPanel = builder.getPanel();
		}
		return serverPanel;
	}

	protected JPanel getContributionsPanel() {
		if(contributionsPanel == null) {
			try {
				contributionsPanel = new HTMLPanel(contributionsFile);
			} catch (IOException e) {
				logger.warning("Could not read contributions-file: " + e.getMessage());
			}
		}
		return contributionsPanel;
	}

	protected JPanel getLicensePanel() {
		if(licensePanel == null) {
			try {
				licensePanel = new HTMLPanel(licenseFile);
			} catch (IOException e) {
				logger.warning("Could not read license-file: " + e.getMessage());
			}
		}
		return licensePanel;
	}

	protected JLabel createPlainLabel(String text)
	{
		JLabel label = new JLabel(text);
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
		return label;
	}

	protected JButton getCopyToClipboardButton() {
		if(copyToClipboardButton == null) {
			copyToClipboardButton = new JButton(Messages.getString("About_CopyToClipboard"));
			copyToClipboardButton.setToolTipText(Messages.getString("About_CopyToClipboard_ToolTip"));
			copyToClipboardButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e)
				{
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

					clipboard.setContents(new StringSelection(
							version + "\r\n" +
							build + "\r\n" +
							server != null ? server : Messages.getString("About_Server_unknown") + "\r\n" +
									database != null ? database : Messages.getString("About_Database_unknown") + "\r\n" +
											System.getProperty("java.vm.vendor") + ", " + System.getProperty("java.vm.name") + ", " + System.getProperty("java.vm.version") + "\r\n" +
											System.getProperty("java.vendor") + ", " + System.getProperty("java.vendor.url") + ", " + System.getProperty("java.version") + "\r\n" +
											System.getProperty("os.name") + ", " + System.getProperty("os.arch") + ", " + System.getProperty("os.version"))
					, AboutDialog.this);
				}
			});
		}
		return copyToClipboardButton;
	}

	protected JPanel getButtonPanel()
	{
		if(buttonPanel == null)
		{
			FormLayout layout = new FormLayout(
					"pref, 3dlu:grow, pref",  // Columns.
					"pref" // Rows.
			);

			PanelBuilder builder = new PanelBuilder(layout);

			CellConstraints cc = new CellConstraints();

			builder.add(getCopyToClipboardButton(), cc.xy(1, 1));
			builder.add(getCloseButton(), cc.xy(3, 1));

			buttonPanel = builder.getPanel();
		}
		return buttonPanel;
	}

	protected JButton getCloseButton()
	{
		if(closeButton == null)
		{
			closeButton = new JButton(Messages.getString("About_Close"));
			closeButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event)
				{
					AboutDialog.this.setVisible(false);
					AboutDialog.this.dispose();
				}
			});
		}
		return closeButton;
	}

	/**
	 * implements ClipboardOwner
	 */

	public void lostOwnership(Clipboard clipboard, Transferable contents)
	{
		// I do not think I should do anything here? Do I?
	}

	protected class HTMLPanel extends JPanel implements HyperlinkListener
	{
		/**
		 *
		 */
		private static final long serialVersionUID = 1757438482225641428L;

		public HTMLPanel(String resource) throws IOException
		{
			super();
			setLayout(new BorderLayout());

			JEditorPane htmlPane = new JEditorPane(getClass().getResource(resource))
			{
				/**
				 *
				 */
				private static final long serialVersionUID = -7951097550114658569L;

				protected void paintComponent(Graphics g)
				{
					Graphics2D g2d = (Graphics2D)g;
					g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
							RenderingHints.VALUE_RENDER_QUALITY);
					super.paintComponent(g);
				}
			};
			htmlPane.setContentType("text/html");
			htmlPane.setEditable(false);
			htmlPane.setOpaque(false);
			htmlPane.addHyperlinkListener(this);
			JScrollPane scrollPane = new JScrollPane(htmlPane);
			scrollPane.setPreferredSize(new Dimension(640, 150));

			add(scrollPane, BorderLayout.CENTER);
		}

		public void hyperlinkUpdate(HyperlinkEvent e)
		{
			//if(e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
			// FIXME ApplicationStarter.getSystemApplicationStarter().startBrowser(e.getURL().toString());
		}
	}
}
