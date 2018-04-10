package de.tarent.commons.ui;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
