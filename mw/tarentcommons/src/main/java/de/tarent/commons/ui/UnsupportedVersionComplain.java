/**
 *
 */
package de.tarent.commons.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Fabian K&ouml;ster (f.koester@tarent.de) tarent GmbH Bonn
 *
 */
public class UnsupportedVersionComplain {

    /**
     * Displays a warning dialog and returns whether the check which caused
     * the dialog to be opened should be done again at the next application started.
     *
     * @return false if user does not want to see this warning any more
     */
    public static boolean showUnsupportedVersionComplain(String appName)
    {
      // Warning dialog is presented in its own frame for the sole reason to have
      // a valid 'window entry' in the surrounding windowing environment in all cases
      // without the need to initialize a parental frame somewhere or reuse one (e.g.
      // the login window). However being on our own means that we have to implement
      // thread suspension ...

      // A special (privately-known) monitor object that is used
      // for thread suspension and re-awakening.
      final Object monitor = new Object();

      // A result array which is itself final and can be accessed
      // from an inner class to set its values (sort of a hack, but JLS compliant).
      final boolean result[] = new boolean[] { false };

      // Construction of the warning dialog.
      FormLayout l = new FormLayout("3dlu, 10dlu, 6dlu, pref:grow, 3dlu, pref:grow, 6dlu, 10dlu, 3dlu",
				    "3dlu, pref, 12dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 12dlu, pref, 12dlu, pref, 3dlu, pref, 3dlu");

      final JFrame frame = new JFrame(Messages.getString("UnsupportedVersionComplain_Title"));

      frame.addWindowListener(new WindowAdapter() {

	  public void windowClosing(WindowEvent arg0) {

		  // Makes frame invisible and stores the result.
		  super.windowClosing(arg0);
		  result[0] = false;

		  // Re-awakens the main thread.
		  synchronized (monitor)
		  {
			  monitor.notifyAll();
		  }
	  }
      });

      Container cp = frame.getContentPane();
      cp.setLayout(l);
      CellConstraints cc = new CellConstraints();

      cp.add(new JLabel(Messages.getFormattedString("UnsupportedVersionComplain_Unsupported", appName)), cc.xyw(2, 2, 7));

      cp.add(new JLabel(Messages.getString("UnsupportedVersionComplain_Implementation_Version")), cc.xyw(3, 4, 2));
      cp.add(new JLabel(System.getProperty("java.version")), cc.xyw(6, 4, 2));

      cp.add(new JLabel(Messages.getString("UnsupportedVersionComplain_Implementation_Vendor")), cc.xyw(3, 6, 2));
      cp.add(new JLabel(System.getProperty("java.vendor")), cc.xyw(6, 6, 2));

      cp.add(new JLabel(Messages.getString("UnsupportedVersionComplain_VM_Name")), cc.xyw(3, 8, 2));
      cp.add(new JLabel(System.getProperty("java.vm.name")), cc.xyw(6, 8, 2));

      cp.add(new JLabel(Messages.getString("UnsupportedVersionComplain_VM_Vendor")), cc.xyw(3, 10, 2));
      cp.add(new JLabel(System.getProperty("java.vm.vendor")), cc.xyw(6, 10, 2));

      cp.add(new JLabel(Messages.getString("UnsupportedVersionComplain_Again")), cc.xyw(2, 12, 7));

      // yes and no button get their own panel to make their layout independent from the rest
      // of the dialog (e.g. to have equal button widths).
      JButton yesButton = new JButton(Messages.getString("UnsupportedVersionComplain_Yes"));
      yesButton.addActionListener(new ActionListener()
      {
	  public void actionPerformed(ActionEvent ae)
	  {
		  // Makes frame invisible and stores the result.
		  frame.setVisible(false);
		  result[0] = true;

		  // Re-awakens the main thread.
		  synchronized (monitor)
		  {
			  monitor.notifyAll();
		  }
	  }
      });

      frame.getRootPane().setDefaultButton(yesButton);

      JButton noButton = new JButton(Messages.getString("UnsupportedVersionComplain_No"));
      noButton.addActionListener(new ActionListener()
      {
	  public void actionPerformed(ActionEvent ae)
	  {
		  // Makes frame invisible and stores the result.
		  frame.setVisible(false);
		  result[0] = false;

		  // Re-awakens the main thread.
		  synchronized (monitor)
		  {
			  monitor.notifyAll();
		  }
	  }
      });

      // Buttons resemble the layout of a JOptionPane dialog: They have the same width and
      // try to stay close in the middle.
      PanelBuilder pb = new PanelBuilder(new FormLayout("3dlu:grow, pref, 3dlu, pref, 3dlu:grow", "pref"));
      pb.add(yesButton, cc.xy(2, 1));
      pb.add(noButton, cc.xy(4, 1));
      pb.getLayout().setColumnGroups(new int[][] { { 2, 4 } });

      cp.add(pb.getPanel(), cc.xyw(2, 14, 7));

      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      // Sleeps until awakened by action events on the buttons.
      try {
	  synchronized (monitor) {
	    monitor.wait();
	  }
      } catch (InterruptedException e)
      {
	// Expected (caused by monitor.notifyAll() ).
      }

      frame.dispose();

      return result[0];
    }
}
