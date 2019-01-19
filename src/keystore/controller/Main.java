/**
 * 23 de out de 2018
 */
package keystore.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import keystore.view.TelaKS;

/**
 * @author Daryan Avi
 *
 */
public class Main
{
	private static String arquivoKS;
	private static char[] senhaKS;
	
	public static void main(String[] args) throws Exception
	{
		FileWriter writer = null;
		FileReader reader = null;
		BufferedReader bufReader = null;
		
		try
		{
			File conf = new File("keystore.conf");
			
			if (! conf.exists())
			{
				conf.createNewFile();
				
				writer = new FileWriter(conf);
				
				writer.write("caminho=keystore.jks\n");
				writer.write("senha=123");
				
				writer.close();
			}
			
			bufReader = new BufferedReader(reader = new FileReader(conf));
			
			arquivoKS = bufReader.readLine().split("=")[1].trim();
			senhaKS = bufReader.readLine().split("=")[1].trim().toCharArray();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (writer != null)
				try {	writer.close();	} catch (Exception e) {}
			
			if (reader != null)
				try {	reader.close();	} catch (Exception e) {}
			
			if (bufReader != null)
				try {	bufReader.close();	} catch (Exception e) {}
		}
		
		new TelaKS();
	}
	
	public static String getArquivoKS()
	{
		return arquivoKS;
	}
	
	public static char[] getSenhaKS()
	{
		return senhaKS;
	}
}