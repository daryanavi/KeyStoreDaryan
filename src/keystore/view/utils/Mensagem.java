/**
 * 9 de nov de 2018
 */
package keystore.view.utils;

import java.awt.Window;

import javax.swing.JOptionPane;

/**
 * @author Daryan Avi
 *
 */
public class Mensagem
{
    public static void sucesso(Window w)
    {
    	sucesso(w, "Operação realizada com sucesso");
    }
	
    public static void sucesso(Window w, String msg)
    {
    	JOptionPane.showMessageDialog(w, msg, "Mensagem", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void erro(Window w, String msg)
    {
		JOptionPane.showMessageDialog(w, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }
	
    public static void excecao(Window w, Exception e)
    {
		erro(w, "Ocorreu uma exceção no programa");
		e.printStackTrace();
    }
}