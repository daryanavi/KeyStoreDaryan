/**
 * 9 de nov de 2018
 */
package keystore.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.crypto.Cipher;

import keystore.controller.Main;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

/**
 * @author Daryan Avi
 *
 */
@SuppressWarnings("restriction")
public class FuncoesKS
{
	public static String buscarCertificados() throws Exception
	{
		StringBuilder str = new StringBuilder();
		FileInputStream in = null;
		
		try
		{
	        KeyStore keyStore = KeyStore.getInstance("jks");
			
	        File arq = new File(Main.getArquivoKS());
	        
	        if (arq.exists())
		        keyStore.load(in = new FileInputStream(Main.getArquivoKS()), Main.getSenhaKS());
	        else
	        	keyStore.load(null, null);
	        
			Enumeration<String> aliases = keyStore.aliases();
			
			int count = 1;
			
			while (aliases.hasMoreElements())
			{
				String a = aliases.nextElement();
				
				str.append("Certificado " + count + " (Alias: " + a + "):\n");
				str.append(keyStore.getCertificate(a).toString() + "\n\n");
				
				count ++;
			}
		}
        finally
        {
        	if (in != null)
        		in.close();
        }
		
		return str.toString();
	}
	
	public static Enumeration<String> buscarAliases() throws Exception
	{
		FileInputStream in = null;
		
		try
		{
	        KeyStore keyStore = KeyStore.getInstance("jks");
			
	        File arq = new File(Main.getArquivoKS());
	        
	        if (arq.exists())
		        keyStore.load(in = new FileInputStream(Main.getArquivoKS()), Main.getSenhaKS());
	        else
	        	keyStore.load(null, null);
	        
			return keyStore.aliases();
		}
        finally
        {
        	if (in != null)
        		in.close();
        }
	}
	
	public static void inserirCertificado(String id, String alias, char[] senha, int validade) throws Exception
	{
		FileInputStream in = null;
		FileOutputStream out = null;
		
		try
		{
	        KeyStore keyStore = KeyStore.getInstance("jks");
			
	        File arq = new File(Main.getArquivoKS());
	        
	        if (arq.exists())
		        keyStore.load(in = new FileInputStream(Main.getArquivoKS()), Main.getSenhaKS());
	        else
	        	keyStore.load(null, null);
	        
			CertAndKeyGen keyGen = new CertAndKeyGen("RSA", "SHA1WithRSA", null);
	        keyGen.generate(2048);
	        PrivateKey privateKey = keyGen.getPrivateKey();
	         
	        X509Certificate cert = keyGen.getSelfCertificate(new X500Name("CN=" + id), (long)validade * 24 * 60 * 60);
	        
	        Principal issuer = cert.getSubjectDN();
	        String issuerSigAlg = cert.getSigAlgName();
	         
	        byte[] inCertBytes = cert.getTBSCertificate();
	        
	        X509CertInfo info = new X509CertInfo(inCertBytes);
	        info.set(X509CertInfo.ISSUER, issuer);
	        
	        X509CertImpl outCert = new X509CertImpl(info);
	        outCert.sign(privateKey, issuerSigAlg);
	        
	        keyStore.setKeyEntry(alias, privateKey, senha, new X509Certificate[] { outCert });
	        keyStore.store(out = new FileOutputStream(Main.getArquivoKS()), Main.getSenhaKS());
		}
        finally
        {
        	if (in != null)
        		in.close();
        	
        	if (out != null)
        		out.close();
        }
	}
	
	public static void excluirCertificado(String alias) throws Exception
	{
		FileInputStream in = null;
		FileOutputStream out = null;

		try
		{
			KeyStore keyStore = KeyStore.getInstance("jks");

			File arq = new File(Main.getArquivoKS());

			if (arq.exists())
				keyStore.load(in = new FileInputStream(Main.getArquivoKS()), Main.getSenhaKS());
			else
				keyStore.load(null, null);

			keyStore.deleteEntry(alias);
			keyStore.store(out = new FileOutputStream(Main.getArquivoKS()), Main.getSenhaKS());
		}
		finally
		{
			if (in != null)
				in.close();

			if (out != null)
				out.close();
		}
	}
	
	public static void importarCertificado(String alias, String caminhoCert) throws Exception
	{
		FileInputStream in = null, inCert = null;
		FileOutputStream out = null;
		
		try
		{
			KeyStore keyStore = KeyStore.getInstance("jks");
			
	        File arq = new File(Main.getArquivoKS());
	        
	        if (arq.exists())
	        	keyStore.load(in = new FileInputStream(Main.getArquivoKS()), Main.getSenhaKS());
	        else
	        	keyStore.load(null, null);
	        
	        inCert = new FileInputStream(caminhoCert);
			
	        keyStore.setCertificateEntry(alias, CertificateFactory.getInstance("X.509").generateCertificate(inCert));
	        keyStore.store(out = new FileOutputStream(Main.getArquivoKS()), Main.getSenhaKS());
		}
        finally
        {
        	if (in != null)
        		in.close();
        	
        	if (inCert != null)
        		inCert.close();
        	
        	if (out != null)
        		out.close();
        }
    }
	
	public static void exportarCertificado(String alias, String caminho) throws Exception
    {
		FileInputStream in = null;
		FileOutputStream outCert = null;
		
		try
		{
	        KeyStore keyStore = KeyStore.getInstance("jks");
			
	        File arq = new File(Main.getArquivoKS());
	        
	        if (arq.exists())
		        keyStore.load(in = new FileInputStream(Main.getArquivoKS()), Main.getSenhaKS());
	        else
	        	keyStore.load(null, null);
	        
			File file = new File(caminho);
			
			if (! file.exists())
			{
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			
			Certificate cer = keyStore.getCertificate(alias);
			
			outCert = new FileOutputStream(file);
			outCert.write(cer.getEncoded());
		}
        finally
        {
        	if (in != null)
        		in.close();
        	
			if (outCert != null)
				outCert.close();
        }
    }
	
	public static void criptografarArquivo(String alias, String caminhoArquivo, String caminhoCopia) throws Exception
    {
		FileInputStream in = null;
		FileOutputStream out = null;
		
		try
		{
	        KeyStore keyStore = KeyStore.getInstance("jks");
			
	        File arq = new File(Main.getArquivoKS());
	        
	        if (arq.exists())
		        keyStore.load(in = new FileInputStream(Main.getArquivoKS()), Main.getSenhaKS());
	        else
	        	keyStore.load(null, null);
	        
			Certificate cer = keyStore.getCertificate(alias);
			
			Cipher cipher = Cipher.getInstance(cer.getPublicKey().getAlgorithm());
			
            byte[] text = Files.readAllBytes(new File(caminhoArquivo).toPath());
			
            cipher.init(Cipher.ENCRYPT_MODE, cer);
			byte[] textEncrypted = cipher.doFinal(text);
			
			File copia = new File(caminhoCopia + caminhoArquivo.substring(caminhoArquivo.lastIndexOf('.')));
			copia.createNewFile();
			
			out = new FileOutputStream(copia);
			out.write(textEncrypted);
		}
        finally
        {
        	if (in != null)
        		in.close();
        	
			if (out != null)
				out.close();
        }
    }
	
	public static String descriptografarArquivo(String alias, char[] senha, String caminho) throws Exception
    {
		FileInputStream in = null;
		
		try
		{
	        KeyStore keyStore = KeyStore.getInstance("jks");
			
	        File arq = new File(Main.getArquivoKS());
	        
	        if (arq.exists())
		        keyStore.load(in = new FileInputStream(Main.getArquivoKS()), Main.getSenhaKS());
	        else
	        	keyStore.load(null, null);
	        
			PrivateKey key = (PrivateKey)keyStore.getKey(alias, senha);
			
			Cipher cipher = Cipher.getInstance(key.getAlgorithm());
			
            byte[] text = Files.readAllBytes(new File(caminho).toPath());
			
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        byte[] textDecrypted = cipher.doFinal(text);
	        
	        return new String(textDecrypted);
		}
        finally
        {
        	if (in != null)
        		in.close();
        }
    }
	
	public static void assinarArquivo(String alias, char[] senha, String caminho) throws Exception
    {
		FileInputStream in = null;
		FileOutputStream out = null;
		ObjectOutputStream objOut = null;
		
		try
		{
	        KeyStore keyStore = KeyStore.getInstance("jks");
			
	        File arq = new File(Main.getArquivoKS());
	        
	        if (arq.exists())
		        keyStore.load(in = new FileInputStream(Main.getArquivoKS()), Main.getSenhaKS());
	        else
	        	keyStore.load(null, null);
	        
			PrivateKey key = (PrivateKey)keyStore.getKey(alias, senha);
			
            byte[] text = Files.readAllBytes(new File(caminho).toPath());
            
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(key);
            sig.update(text);
            
			objOut = new ObjectOutputStream(out = new FileOutputStream(caminho));
			objOut.writeObject(new byte[][] { text, sig.sign() });
		}
        finally
        {
        	if (in != null)
        		in.close();
        	
        	if (out != null)
        		out.close();
        	
        	if (objOut != null)
        		objOut.close();
        }
    }
	
	public static boolean verificarAssinaturaArquivo(String alias, String caminho) throws Exception
    {
		FileInputStream in = null, inArq = null;
		ObjectInputStream objIn = null;
		
		try
		{
	        KeyStore keyStore = KeyStore.getInstance("jks");
			
	        File arq = new File(Main.getArquivoKS());
	        
	        if (arq.exists())
		        keyStore.load(in = new FileInputStream(Main.getArquivoKS()), Main.getSenhaKS());
	        else
	        	keyStore.load(null, null);
	        
	        Certificate cer = keyStore.getCertificate(alias);
	        
            objIn = new ObjectInputStream(inArq = new FileInputStream(caminho));
            byte[][] obj = (byte[][])objIn.readObject();
            
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(cer);
            sig.update(obj[0]);
            
			return sig.verify(obj[1]);
		}
        finally
        {
        	if (in != null)
        		in.close();
        	
        	if (inArq != null)
        		inArq.close();
        	
        	if (objIn != null)
        		objIn.close();
        }
    }
}