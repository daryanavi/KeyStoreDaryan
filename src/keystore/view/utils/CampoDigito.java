/**
 * 10 de nov de 2018
 */
package keystore.view.utils;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 * @author Daryan Avi
 *
 */
public class CampoDigito extends JTextField
{
	private static final long serialVersionUID = 6688257134982140957L;

	public CampoDigito()
	{
		super();
		
		init();
	}
	
	public CampoDigito(int columns)
	{
		super(columns);
		
		init();
	}
	
	private void init()
	{
		((PlainDocument)getDocument()).setDocumentFilter(new DocumentFilter()
    	{
    		@Override
    	    public void insertString(FilterBypass fb, int offset, String text, 
    	    						 AttributeSet attr) throws BadLocationException
    	    {
    	        super.insertString(fb, offset, revise(text), attr);
    	    }

    	    @Override
    	    public void replace(FilterBypass fb, int offset, int length, String text,
    	                        AttributeSet attrs) throws BadLocationException
    	    {
    	        super.replace(fb, offset, length, revise(text), attrs);
    	    }

    	    private String revise(String text)
    	    {
    	        StringBuilder builder = new StringBuilder(text);
    	        int index = 0;
    	        
    	        while (index < builder.length())
    	        {
    	            if (Character.isDigit(builder.charAt(index)))
    	                index ++;
    	            else
    	                builder.deleteCharAt(index);
    	        }
    	        
    	        return builder.toString();
    	    }
    	});
	}
	
	public int getValor()
	{
		return Integer.parseInt(getText());
	}
}