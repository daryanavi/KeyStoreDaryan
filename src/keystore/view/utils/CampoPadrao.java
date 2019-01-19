/**
 * 9 de nov de 2018
 */
package keystore.view.utils;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Daryan Avi
 *
 */
public final class CampoPadrao
{
	public static JPanel get(String titulo, JComponent... campos)
	{
		JPanel pn = new JPanel();
		
		pn.add(new JLabel(titulo));
		
		for (JComponent c : campos)
			pn.add(c);
		
		return pn;
	}
}