/**
 * 9 de nov de 2018
 */
package keystore.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import keystore.model.FuncoesKS;
import keystore.view.utils.CampoDigito;
import keystore.view.utils.CampoPadrao;
import keystore.view.utils.Mensagem;
import net.miginfocom.swing.MigLayout;

/**
 * @author Daryan Avi
 *
 */
public class TelaKS extends JFrame
{
	private static final long serialVersionUID = -5054938787948919578L;
	
	private JButton btAtualizar;
	private Vector<JComboBox<String>> camposAlias;
	
    public TelaKS()
    {
    	super("Key Store");
    
    	try
    	{
    		camposAlias = new Vector<>();
    		
    		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    	setResizable(true);
	    	setExtendedState(MAXIMIZED_BOTH);
	    	
	    	setLayout(new MigLayout());
	    	
	    	add(lista(), "dock center, span");
	    	add(inserir(), "dock center");
	    	add(excluir(), "dock center, span");
	    	add(importar(), "dock center");
	    	add(exportar(), "dock center, span");
	    	add(criptografar(), "dock center, span");
	    	add(descriptografar(), "dock center, span");
	    	add(assinar(), "dock center, span");
	    	add(verificarAssinatura(), "dock center, span");
	    	
	    	atualizar();
	    	
	    	revalidate();
	    	repaint();
	    	
	    	pack();
	    	setLocationRelativeTo(null);
	        setVisible(true);
    	}
    	catch (Exception e)
    	{
    		Mensagem.excecao(TelaKS.this, e);
    	}
    }
    
    private JPanel lista()
    {
    	JPanel pn = new JPanel(new MigLayout("nogrid"));
    	pn.setBorder(new TitledBorder("Lista de Certificados"));
    	
    	btAtualizar = new JButton("Atualizar");
    	pn.add(btAtualizar);
    	
    	JButton btExpandir = new JButton("Expandir");
    	pn.add(btExpandir, "wrap");
    	
    	JTextArea edLista = new JTextArea(1000, 1000);
    	edLista.setEditable(false);
    	edLista.setLineWrap(true);
    	edLista.setPreferredSize(new Dimension(1000, 1000));
    	
    	JScrollPane scLista = new JScrollPane(edLista);
    	
    	pn.add(scLista, "dock center, span");
    	
    	btAtualizar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					edLista.setText(FuncoesKS.buscarCertificados());
					
					SwingUtilities.invokeLater(() ->
					{
						scLista.getVerticalScrollBar().setValue(0);
					});
				}
				catch (Exception ex)
				{
					Mensagem.excecao(TelaKS.this, ex);
				}
			}
		});
    	
    	btExpandir.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					JDialog dialog = new JDialog(TelaKS.this, "Lista de Certificados");
					dialog.setSize(new Dimension(600, 400));
					dialog.getContentPane().setLayout(new BorderLayout());
					
					JTextArea edMsg = new JTextArea();
					edMsg.setBorder(new EmptyBorder(5, 5, 5, 5));
					edMsg.setEditable(false);
					edMsg.setLineWrap(true);
					edMsg.setText(edLista.getText());
					dialog.add(new JScrollPane(edMsg));
					
					dialog.setLocationRelativeTo(TelaKS.this);
					dialog.setVisible(true);
				}
				catch (Exception ex)
				{
					Mensagem.excecao(TelaKS.this, ex);
				}
			}
		});
    	
    	return pn;
    }
    
    private JPanel inserir()
    {
    	JPanel pn = new JPanel(new MigLayout("gapx 20"));
    	pn.setBorder(new TitledBorder("Inserir Certificado Auto-Assinado"));
    	
    	JTextField edIdentificacao = new JTextField(10);
    	pn.add(CampoPadrao.get("Emissor/Requerente", edIdentificacao));
    	
    	JTextField edAlias = new JTextField(10);
    	pn.add(CampoPadrao.get("Alias", edAlias));
    	
    	JPasswordField edSenha = new JPasswordField(10);
    	pn.add(CampoPadrao.get("Senha Chave Privada", edSenha));
    	
    	CampoDigito edValidade = new CampoDigito(5);
    	pn.add(CampoPadrao.get("Validade (dias)", edValidade));
    	
    	JButton btInserir = new JButton("Inserir");
    	btInserir.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					FuncoesKS.inserirCertificado(edIdentificacao.getText(), 
							edAlias.getText(),
							edSenha.getPassword(),
							edValidade.getValor());
					
					Mensagem.sucesso(TelaKS.this);
					
					atualizar();
				}
				catch (Exception e)
				{
					Mensagem.excecao(TelaKS.this, e);
				}
				finally
				{
					edIdentificacao.setText("");
					edAlias.setText("");
					edSenha.setText("");
					edValidade.setText("");
					
					edIdentificacao.requestFocus();
				}
			}
		});
    	pn.add(btInserir);
    	
    	return pn;
    }
    
    private JPanel excluir() throws Exception
    {
    	JPanel pn = new JPanel(new MigLayout("gapx 20"));
    	pn.setBorder(new TitledBorder("Excluir Certificado"));
    	
    	JComboBox<String> edAlias = new JComboBox<>();
    	pn.add(CampoPadrao.get("Alias", edAlias));
    	
    	JButton btExcluir = new JButton("Excluir");
    	btExcluir.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					FuncoesKS.excluirCertificado((String)edAlias.getSelectedItem());
					
					Mensagem.sucesso(TelaKS.this);
					
					atualizar();
				}
				catch (Exception e)
				{
					Mensagem.excecao(TelaKS.this, e);
				}
				finally
				{
					if (edAlias.getItemCount() > 0)
						edAlias.setSelectedIndex(0);
					
					edAlias.requestFocus();
				}
			}
		});
    	pn.add(btExcluir);
    	
    	camposAlias.add(edAlias);
    	
    	return pn;
    }
    
    private JPanel importar()
    {
    	JPanel pn = new JPanel(new MigLayout("gapx 20"));
    	pn.setBorder(new TitledBorder("Importar Certificado"));
    	
    	JTextField edArquivo = new JTextField(30);
    	
    	JButton btProcurar = new JButton("Procurar...");
    	btProcurar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					JFileChooser seletor = new JFileChooser(edArquivo.getText());
					seletor.showOpenDialog(TelaKS.this);
					
					String caminho = seletor.getSelectedFile() == null ? "" : seletor.getSelectedFile().getPath();
					
					edArquivo.setText(caminho);
				}
				catch (Exception ex)
				{
		    		Mensagem.excecao(TelaKS.this, ex);
				}
			}
		});
    	
    	pn.add(CampoPadrao.get("Arquivo", edArquivo, btProcurar));
    	
    	JTextField edAlias = new JTextField(10);
    	pn.add(CampoPadrao.get("Alias", edAlias));
    	
    	JButton btImportar = new JButton("Importar");
    	btImportar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					FuncoesKS.importarCertificado(edAlias.getText(), edArquivo.getText());
					
					Mensagem.sucesso(TelaKS.this);
					
					atualizar();
				}
				catch (Exception e)
				{
					Mensagem.excecao(TelaKS.this, e);
				}
				finally
				{
					edArquivo.setText("");
					edAlias.setText("");
					
					edArquivo.requestFocus();
				}
			}
		});
    	pn.add(btImportar);
    	
    	return pn;
    }
    
    private JPanel exportar()
    {
    	JPanel pn = new JPanel(new MigLayout("gapx 20"));
    	pn.setBorder(new TitledBorder("Exportar Certificado"));
    	
    	JComboBox<String> edAlias = new JComboBox<String>();
    	pn.add(CampoPadrao.get("Alias", edAlias));
    	
    	JButton btExportar = new JButton("Exportar");
    	btExportar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					JFileChooser seletor = new JFileChooser();
					
					if (seletor.showSaveDialog(TelaKS.this) == JFileChooser.APPROVE_OPTION)
					{
						FuncoesKS.exportarCertificado((String)edAlias.getSelectedItem(), 
								seletor.getSelectedFile().getAbsolutePath() + ".cer" );
						
						Mensagem.sucesso(TelaKS.this);
					}
				}
				catch (Exception e)
				{
					Mensagem.excecao(TelaKS.this, e);
				}
				finally
				{
					if (edAlias.getItemCount() > 0)
						edAlias.setSelectedIndex(0);
					
					edAlias.requestFocus();
				}
			}
		});
    	pn.add(btExportar);
    	
    	camposAlias.add(edAlias);
    	
    	return pn;
    }
    
    private JPanel criptografar()
    {
    	JPanel pn = new JPanel(new MigLayout("gapx 20"));
    	pn.setBorder(new TitledBorder("Criptografar Arquivo"));
    	
    	JComboBox<String> edAlias = new JComboBox<String>();
    	pn.add(CampoPadrao.get("Alias", edAlias));
    	
    	JTextField edArquivo = new JTextField(30);
    	
    	JButton btProcurar = new JButton("Procurar...");
    	btProcurar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					JFileChooser seletor = new JFileChooser(edArquivo.getText());
					seletor.showOpenDialog(TelaKS.this);
					
					String caminho = seletor.getSelectedFile() == null ? "" : seletor.getSelectedFile().getPath();
					
					edArquivo.setText(caminho);
				}
				catch (Exception ex)
				{
		    		Mensagem.excecao(TelaKS.this, ex);
				}
			}
		});
    	
    	pn.add(CampoPadrao.get("Arquivo", edArquivo, btProcurar));
    	
    	JButton btCriptografar = new JButton("Salvar Cópia Criptografada");
    	btCriptografar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					JFileChooser seletor = new JFileChooser();
					
					if (seletor.showSaveDialog(TelaKS.this) == JFileChooser.APPROVE_OPTION)
					{
						FuncoesKS.criptografarArquivo((String)edAlias.getSelectedItem(), 
								edArquivo.getText(),
								seletor.getSelectedFile().getAbsolutePath());
						
						Mensagem.sucesso(TelaKS.this);
					}
				}
				catch (Exception e)
				{
					Mensagem.excecao(TelaKS.this, e);
				}
				finally
				{
					if (edAlias.getItemCount() > 0)
						edAlias.setSelectedIndex(0);
					
					edAlias.requestFocus();
				}
			}
		});
    	pn.add(btCriptografar);
    	
    	camposAlias.add(edAlias);
    	
    	return pn;
    }
    
    private JPanel descriptografar()
    {
    	JPanel pn = new JPanel(new MigLayout("gapx 20"));
    	pn.setBorder(new TitledBorder("Descriptografar Arquivo"));
    	
    	JComboBox<String> edAlias = new JComboBox<String>();
    	pn.add(CampoPadrao.get("Alias", edAlias));
    	
    	JPasswordField edSenha = new JPasswordField(10);
    	pn.add(CampoPadrao.get("Senha Chave Privada", edSenha));
    	
    	JTextField edArquivo = new JTextField(30);
    	
    	JButton btProcurar = new JButton("Procurar...");
    	btProcurar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					JFileChooser seletor = new JFileChooser(edArquivo.getText());
					seletor.showOpenDialog(TelaKS.this);
					
					String caminho = seletor.getSelectedFile() == null ? "" : seletor.getSelectedFile().getPath();
					
					edArquivo.setText(caminho);
				}
				catch (Exception ex)
				{
		    		Mensagem.excecao(TelaKS.this, ex);
				}
			}
		});
    	
    	pn.add(CampoPadrao.get("Arquivo", edArquivo, btProcurar));
    	
    	JButton btDescriptografar = new JButton("Mostrar Texto Descriptografado");
    	btDescriptografar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					JDialog dialog = new JDialog(TelaKS.this, "Texto Descriptografado");
					dialog.setSize(new Dimension(600, 400));
					dialog.getContentPane().setLayout(new BorderLayout());
					
					JTextArea edMsg = new JTextArea();
					edMsg.setBorder(new EmptyBorder(5, 5, 5, 5));
					edMsg.setEditable(false);
					edMsg.setLineWrap(true);
					edMsg.setText(FuncoesKS.descriptografarArquivo((String)edAlias.getSelectedItem(), 
							edSenha.getPassword(), 
							edArquivo.getText()));
					dialog.add(new JScrollPane(edMsg));
					
					dialog.setLocationRelativeTo(TelaKS.this);
					dialog.setVisible(true);
				}
				catch (Exception e)
				{
					Mensagem.excecao(TelaKS.this, e);
				}
				finally
				{
					if (edAlias.getItemCount() > 0)
						edAlias.setSelectedIndex(0);
					
					edAlias.requestFocus();
				}
			}
		});
    	pn.add(btDescriptografar);
    	
    	camposAlias.add(edAlias);
    	
    	return pn;
    }
    
    private JPanel assinar()
    {
    	JPanel pn = new JPanel(new MigLayout("gapx 20"));
    	pn.setBorder(new TitledBorder("Assinar Arquivo"));
    	
    	JComboBox<String> edAlias = new JComboBox<String>();
    	pn.add(CampoPadrao.get("Alias", edAlias));
    	
    	JPasswordField edSenha = new JPasswordField(10);
    	pn.add(CampoPadrao.get("Senha Chave Privada", edSenha));
    	
    	JTextField edArquivo = new JTextField(30);
    	
    	JButton btProcurar = new JButton("Procurar...");
    	btProcurar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					JFileChooser seletor = new JFileChooser(edArquivo.getText());
					seletor.showOpenDialog(TelaKS.this);
					
					String caminho = seletor.getSelectedFile() == null ? "" : seletor.getSelectedFile().getPath();
					
					edArquivo.setText(caminho);
				}
				catch (Exception ex)
				{
		    		Mensagem.excecao(TelaKS.this, ex);
				}
			}
		});
    	
    	pn.add(CampoPadrao.get("Arquivo", edArquivo, btProcurar));
    	
    	JButton btAssinar = new JButton("Assinar");
    	btAssinar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					FuncoesKS.assinarArquivo((String)edAlias.getSelectedItem(), 
							edSenha.getPassword(), 
							edArquivo.getText());
					
					Mensagem.sucesso(TelaKS.this);
				}
				catch (Exception e)
				{
					Mensagem.excecao(TelaKS.this, e);
				}
				finally
				{
					if (edAlias.getItemCount() > 0)
						edAlias.setSelectedIndex(0);
					
					edAlias.requestFocus();
				}
			}
		});
    	pn.add(btAssinar);
    	
    	camposAlias.add(edAlias);
    	
    	return pn;
    }
    
    private JPanel verificarAssinatura()
    {
    	JPanel pn = new JPanel(new MigLayout("gapx 20"));
    	pn.setBorder(new TitledBorder("Verificar Assinatura de Arquivo"));
    	
    	JComboBox<String> edAlias = new JComboBox<String>();
    	pn.add(CampoPadrao.get("Alias", edAlias));
    	
    	JTextField edArquivo = new JTextField(30);
    	
    	JButton btProcurar = new JButton("Procurar...");
    	btProcurar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					JFileChooser seletor = new JFileChooser(edArquivo.getText());
					seletor.showOpenDialog(TelaKS.this);
					
					String caminho = seletor.getSelectedFile() == null ? "" : seletor.getSelectedFile().getPath();
					
					edArquivo.setText(caminho);
				}
				catch (Exception ex)
				{
		    		Mensagem.excecao(TelaKS.this, ex);
				}
			}
		});
    	
    	pn.add(CampoPadrao.get("Arquivo", edArquivo, btProcurar));
    	
    	JButton btVerificar = new JButton("Verificar");
    	btVerificar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					if (FuncoesKS.verificarAssinaturaArquivo((String)edAlias.getSelectedItem(),
							edArquivo.getText()))
						Mensagem.sucesso(TelaKS.this, "Assinatura válida!");
					else
						Mensagem.erro(TelaKS.this, "Assinatura não válida!");
				}
				catch (Exception e)
				{
					Mensagem.excecao(TelaKS.this, e);
				}
				finally
				{
					if (edAlias.getItemCount() > 0)
						edAlias.setSelectedIndex(0);
					
					edAlias.requestFocus();
				}
			}
		});
    	pn.add(btVerificar);
    	
    	camposAlias.add(edAlias);
    	
    	return pn;
    }
    
    private void atualizar() throws Exception
    {
    	btAtualizar.doClick();
    	
    	for (JComboBox<String> c : camposAlias)
    		c.removeAllItems();
    		
    	Enumeration<String> aliases = FuncoesKS.buscarAliases();
    	
    	while (aliases.hasMoreElements())
    	{
    		String a = aliases.nextElement();
    		
        	for (JComboBox<String> c : camposAlias)
        		c.addItem(a);
    	}
    }
}