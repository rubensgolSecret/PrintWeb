package com.print.Print.controler.business.comunicaTiny;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.google.gson.Gson;
import com.print.Print.model.EnumRetorno;
import com.print.Print.model.Expedica;
import com.print.Print.model.LinkEtiqueta;
import com.print.Print.model.RetornoEtiqueta;
import com.print.Print.model.RetornoSeparacao;
import com.print.Print.model.Separacao;
import com.print.Print.util.UrlsTiny;
import com.print.Print.util.Util;

public class Comunica
{
	private Logger logger = Logger.getLogger(Comunica.class.getName());

	private List<Integer> separacoesLida;

	public Comunica(List<Integer> separacoesLida)
	{
		try 
		{
			FileHandler fh = new FileHandler("Log-" + Util.getDataFormatadaSemBarra() + ".log");
			logger.addHandler(fh);
			logger.setUseParentHandlers(false);
			fh.setFormatter(new SimpleFormatter());	
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			logger.severe("erro" + e.getMessage());
		}

		this.separacoesLida = separacoesLida;
	}

	public List<LinkEtiqueta> getEtiquetas(String token) throws IOException, URISyntaxException
	{
		List<LinkEtiqueta> links = new ArrayList<>();

		List<Separacao> separacaos = getSeparacaos(token);
		
		if (separacaos != null)
		{
			for (Separacao separa : separacaos)
			{
				Expedica retornoExpedica = null;

				if (separa.getIdOrigemVinc() > 0)
					retornoExpedica = getExpedicao(token, separa.getIdOrigemVinc(), separa.getObjOrigemVinc());
				
				if (retornoExpedica != null && retornoExpedica.getRetorno() != null &&
					retornoExpedica.getRetorno().getExpedicao() != null)
				{
					RetornoEtiqueta retornEtiqueta = getEtiqueta(token, retornoExpedica.getRetorno().getExpedicao().getId());

					if (retornEtiqueta != null && retornEtiqueta.getRetorno() != null)
					{
						retornEtiqueta = retornEtiqueta.getRetorno();

						if (retornEtiqueta.getLinks() != null)
						{
							for (LinkEtiqueta link : retornEtiqueta.getLinks())
							{							
								links.add(link);
								separacoesLida.add(separa.getId());
							}
						}
					}
				}
			}
		}

        return links;
	}
	
	private RetornoEtiqueta getEtiqueta(String token, int idExpedi) throws IOException, URISyntaxException
	{
		logger.info("buscando etiqueta");
		Gson gson = new Gson();
		URI uri = new URI(UrlsTiny.getEtiqueta(token, idExpedi));
		URL url = uri.toURL();

		HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

        if (conexao.getResponseCode() != 200)
		{
			logger.info("problema de conexcao" + conexao.getResponseMessage());
			return null;
		}

        BufferedReader resposta = new BufferedReader(new InputStreamReader((conexao.getInputStream())));
        String jsonEmString = Util.converteJsonEmString(resposta);
        
		logger.info("retorno da busca" + jsonEmString);
        RetornoEtiqueta retorno = gson.fromJson(jsonEmString, RetornoEtiqueta.class);
        
        return retorno;
	}
	
	private Expedica getExpedicao(String token, int idNota, String tipoObjeto) throws IOException, URISyntaxException
	{
		logger.info("Buscando expedicao");

		URI uri = new URI(UrlsTiny.getExpedicao(token, idNota, tipoObjeto));

        URL url = uri.toURL();
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

        if (conexao.getResponseCode() != 200)
		{
			logger.severe("problema de conexao");
        	return null;
		}

        BufferedReader resposta = new BufferedReader(new InputStreamReader((conexao.getInputStream())));
        String jsonEmString = Util.converteJsonEmString(resposta);

		logger.info("Busca de expedicao concluida");

        Gson gson = new Gson();
        Expedica retorno = gson.fromJson(jsonEmString, Expedica.class);

       return retorno;
	}
	
	private List<Separacao> getSeparacaos(String token) throws IOException, URISyntaxException
	{
		RetornoSeparacao retornoSepara = getSeparacao(token,1);
		List<Separacao> separacaos = null;
		int numeroPag = 0;

		if (retornoSepara != null && retornoSepara.getRetorno() != null)
		{
			retornoSepara = retornoSepara.getRetorno();
			numeroPag = retornoSepara.getNumeroPaginas();

			if (retornoSepara.getStatusProcessamento() == 3 && retornoSepara.getCodigoErro() == 0)
			{
				separacaos = new ArrayList<>();

				if (numeroPag > 1)
				{
					for (Separacao separacao : retornoSepara.getSeparacoes()) 
					{
						if (! Util.comaparaData(separacao.getDataCheckOut()) )
							continue;

						if (separacoesLida.contains(separacao.getId()))
							continue;

						separacaos.add(separacao);
					}

					retornoSepara = null;

					for (int i = 2; i <= numeroPag; i++) 
					{
						retornoSepara =	getSeparacao(token, i);
						
						if (retornoSepara != null && retornoSepara.getRetorno() != null)
						{
							retornoSepara = retornoSepara.getRetorno();

							if (retornoSepara.getStatusProcessamento() == 3 && retornoSepara.getCodigoErro() == 0)
							{
								for (Separacao separacao : retornoSepara.getSeparacoes()) 
								{
									if (! Util.comaparaData(separacao.getDataCheckOut()) )
										continue;
			
									if (separacoesLida.contains(separacao.getId()))
										continue;
			
									separacaos.add(separacao);
								}
							}
						}
						
					}
				}
				else if (retornoSepara.getNumeroPaginas() == 1)
				{
					for (Separacao separacao : retornoSepara.getSeparacoes()) 
					{
						if (! Util.comaparaData(separacao.getDataCheckOut()) )
							continue;

						if (separacoesLida.contains(separacao.getId()))
							continue;

						separacaos.add(separacao);
					}
				}
			}
		}

		return separacaos;
	}

	private RetornoSeparacao getSeparacao(String token, int nummeroPag) throws IOException, URISyntaxException
	{
		logger.info("Buscando separacao");

		URI uri = new URI(UrlsTiny.getSeparacao(token, nummeroPag));

        URL url = uri.toURL();
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

        if (conexao.getResponseCode() != 200)
		{
			logger.severe("problema de conexao");
			return null;
		}

        BufferedReader resposta = new BufferedReader(new InputStreamReader((conexao.getInputStream())));
        String jsonEmString = Util.converteJsonEmString(resposta);

		logger.info("busca de separacao concluida");

        Gson gson = new Gson();
        RetornoSeparacao retorno = gson.fromJson(jsonEmString, RetornoSeparacao.class);

       return retorno;
	}
	
    public EnumRetorno verificaConexao(String token) throws Exception
    {
		logger.info("verificando conexao");
    	RetornoSeparacao retorno = getSeparacao(token, 1);
    	
    	if (retorno == null || retorno.getRetorno() == null)
    		return EnumRetorno.ERROR_404;
    	
		logger.info("Retorno da busca" + retorno.getStatus());
	
    	retorno = retorno.getRetorno();
    	
    	if (retorno.getStatusProcessamento() == 2 && retorno.getCodigoErro() == 31)
    		return EnumRetorno.ERRO_TOKEN;
    	else if (retorno.getStatusProcessamento() == 3)
    		return EnumRetorno.SUCESSO;
		else if (retorno.getStatusProcessamento() == 1 && retorno.getCodigoErro() == 32)
			return EnumRetorno.SUCESSO_EM_BRANCO;
    	else
    		return EnumRetorno.ERRO_HEADER;
    }

	public List<Integer> getSeparacoesLidas()
	{
		return separacoesLida;
	}
}
