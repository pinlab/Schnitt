package info.pinlab.schnitt.gui.swing;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class SwingUtils {

	
	
	/**
	 * Sets look and feel to Nimbus.
	 * 
	 * @return
	 */
	public static boolean setNimbusLF(){
		try {
		    for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(laf.getName())) {
		            UIManager.setLookAndFeel(laf.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {

	}

}
