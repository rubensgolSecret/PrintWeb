package com.print.Print.controler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import com.print.Print.controler.business.comunicaTiny.Comunica;
import com.print.Print.controler.interfaces.IImprimir;
import com.print.Print.controler.interfaces.ITrataArquivo;
import com.print.Print.model.LinkEtiqueta;

public class Busca 
{
    private static volatile Busca instance;

    private List<LinkEtiqueta> links;
    private Comunica comunica;
    private String token;
    private IImprimir imprimir;
    private ITrataArquivo aTrataArquivo;
    private boolean busca = false;

    private Busca(Comunica comunica, String token, 
                 IImprimir imprimir, ITrataArquivo aTrataArquivo) 
    {
        this.comunica = comunica;
        this.token = token;
        this.imprimir = imprimir;
        this.aTrataArquivo = aTrataArquivo;
    }

    public void buscar() throws InterruptedException, IOException, URISyntaxException
    {
        busca = true;
        
        while (busca) 
        {
            links = comunica.getEtiquetas(token);

            for (LinkEtiqueta link : links)
                imprimir.imprimir(link.getLink());
    
            aTrataArquivo.salvaTxt(comunica.getSeparacoesLidas());
    
            Thread.sleep(5000);
        }
    }

    public void parar()
    {
        busca = false;
    }

    public static Busca getInstance(Comunica comunica, String token, 
                                   IImprimir imprimir, ITrataArquivo aTrataArquivo)
    {
        Busca result = instance;

        if (result != null) 
        {
            return result;
        }

        synchronized(Busca.class) 
        {
            if (instance == null) 
            {
                instance = new Busca(comunica, token, imprimir, aTrataArquivo);
            }

            return instance;
        }
    }
}
