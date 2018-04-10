package de.tarent.commons.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * @author niko
 *
 */
public class SleepScreen extends JDialog
{
	/**
	 *
	 */
	private static final long serialVersionUID = -5956411502610782809L;
	private int totalSeconds;
	private int m_iSecondsLeft;
	private JLabel m_oSecondsLabel;
	private JFrame parent;

	public SleepScreen(JFrame parent, String caption, String message, int numseconds)
	{
		super(parent);
		this.parent = parent;
		setModal(true);
		setTitle(caption);
		totalSeconds = numseconds;
		m_iSecondsLeft = numseconds;

		JPanel mainpanel = new JPanel(new BorderLayout());
		mainpanel.setBackground(new Color(0xA7, 0xB1, 0xCA));
		mainpanel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 1), new EmptyBorder(5,5,5,5)));

		JTextArea m_oMessageLabel = new JTextArea(message);
		m_oMessageLabel.setEditable(false);
		m_oMessageLabel.setSelectedTextColor(Color.BLACK);
		m_oMessageLabel.setSelectionColor(new Color(0xA7, 0xB1, 0xCA));
		m_oMessageLabel.setBackground(new Color(0xA7, 0xB1, 0xCA));
		m_oMessageLabel.setFont(new Font(null, Font.PLAIN, 12));

		m_oSecondsLabel = new JLabel(Integer.toString(m_iSecondsLeft), JLabel.CENTER);
		m_oSecondsLabel.setFont(new Font(null, Font.PLAIN, 60));

		mainpanel.add(m_oMessageLabel, BorderLayout.NORTH);
		mainpanel.add(m_oSecondsLabel, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		getContentPane().add(mainpanel);
		setResizable(false);
		setUndecorated(true);
		pack();

		Rectangle parentrect = parent.getBounds();
		setLocation( (parentrect.width / 2)-(this.getWidth() / 2) + (parentrect.x), (parentrect.height / 2)-(this.getHeight() / 2)  + (parentrect.y));

		startTimerTask();
		setVisible(true);
	}

	private class SleepTimer extends TimerTask
	{
		public void run()
		{
			m_iSecondsLeft --;
			m_oSecondsLabel.setText(Integer.toString(m_iSecondsLeft));

			if (m_iSecondsLeft <= 0)
			{
				setVisible(false);
				dispose();
			}
			else
			{
				if(parent instanceof DeferringLoginProvider)
					((DeferringLoginProvider)parent).setStatus(Math.round(((float)m_iSecondsLeft/totalSeconds)*100));
				startTimerTask();
			}
		}
	}

	private void startTimerTask()
	{
		Timer timer = new Timer();
		timer.schedule(new SleepTimer(), 1000);
	}

}
