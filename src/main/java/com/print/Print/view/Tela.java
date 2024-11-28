package com.print.Print.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.print.Print.controler.interfaces.IImprimir;
import com.print.Print.controler.interfaces.ITrataArquivo;
import com.print.Print.controler.business.imprimir.ImprimirDesktop;
import com.print.Print.controler.Busca;
import com.print.Print.controler.business.comunicaTiny.Comunica;
import com.print.Print.model.EnumRetorno;
import com.print.Print.model.LinkEtiqueta;
import com.print.Print.util.TrataArquivo;

public class Tela extends JFrame
{
	private static final long serialVersionUID = 7163765538988263870L;

	private static volatile Tela instance;

	private JPanel panel, panelBotao;
	private JTextField tToken;
	private JLabel lToken;
	private JButton bIniciar, bParar;
	private Container c;
	private Comunica comunica;
	private IImprimir imprimir;
	private List<LinkEtiqueta> links;
	private boolean buscando = false;
	private ITrataArquivo aTrataArquivo;

	private Tela(List<Integer> lidas)
	{
		try 
		{
			c = getContentPane();
			comunica = new Comunica(lidas);
			imprimir = new ImprimirDesktop();
			aTrataArquivo = new TrataArquivo();
 
			panel = new JPanel(new FlowLayout());
            panel.setSize(new Dimension(300,300));

            lToken = new JLabel("Digite o Token:");
            panel.add(lToken);

            tToken = new JTextField(20);
            panel.add(tToken);

            c.add(panel, BorderLayout.WEST);

			bIniciar = new JButton("Iniciar");
			bIniciar.setVisible(true);
			bIniciar.addActionListener(e ->
			{
				try
				{
					buscando = true;

					if (tToken.getText() == null || tToken.getText().trim().equals(""))
					{
						new TelaErro(EnumRetorno.ERRO_EM_BRANCO);
						buscando = false;
						return;
					}

					while (buscando)
					{
						EnumRetorno retorno = comunica.verificaConexao(tToken.getText());

						if (retorno == EnumRetorno.SUCESSO || retorno == EnumRetorno.SUCESSO_EM_BRANCO)
						{
							tToken.setEnabled(! buscando);
							bIniciar.setEnabled(! buscando);
							bParar.setEnabled(buscando);		

							links = comunica.getEtiquetas(tToken.getText());

							for (LinkEtiqueta link : links)
								imprimir.imprimir(link.getLink());
							
							aTrataArquivo.salvaTxt(comunica.getSeparacoesLidas());
						}
						else
						{
							new TelaErro(EnumRetorno.ERRO_TOKEN);
							buscando = false;
							break;
						}

						Thread.sleep(5000);
					}
				}
				catch (Exception e1) 
				{
				  e1.printStackTrace();
				}
			});
			
			panelBotao = new JPanel(new FlowLayout());
			panelBotao.add(bIniciar);

			bParar = new JButton("Parar");
			bParar.addActionListener(e ->
			{
				buscando = false;
				tToken.setEnabled(! buscando);
				bIniciar.setEnabled(! buscando);
				bParar.setEnabled(buscando);
				aTrataArquivo.salvaTxt(comunica.getSeparacoesLidas());
			});

			bParar.setEnabled(buscando);
			panelBotao.add(bParar);
	   
			c.add(panelBotao, BorderLayout.SOUTH);

			setTitle("Imprimir etiquetas");
			setIconImage(getIcone());
			setSize(new Dimension(340,338));
			setLocationRelativeTo(null);
			setResizable(false);
			setVisible(true);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} 
		catch (Exception e) 
		{
		  e.printStackTrace();
		}
	}
	
	private Image getIcone()
	{
		ImageIcon icon = new ImageIcon("src/print_ico.png");

		return icon.getImage();
	}


	public static Tela getInstance(List<Integer> lidas)
    {
        Tela result = instance;

        if (result != null) 
        {
            return result;
        }

        synchronized(Busca.class) 
        {
            if (instance == null) 
            {
                instance = new Tela(lidas);
            }

            return instance;
        }
    }
}