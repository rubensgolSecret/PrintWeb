package com.print.Print.controler.interfaces;

import java.util.List;

public interface ITrataArquivo 
{
    public void salvaTxt(List<Integer> nfLidas);

    public List<Integer> carregaArquivo();
}
