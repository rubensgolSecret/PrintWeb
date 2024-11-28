package com.print.Print;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.print.Print.controler.interfaces.ITrataArquivo;
import com.print.Print.util.TrataArquivo;
import com.print.Print.view.Tela;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
public class PrintApplication 
{
	public static void main(String[] args) 
	{
		SpringApplication.run(PrintApplication.class, args);

        List<Integer> nfsLidas = new ArrayList<>();
        ITrataArquivo arqv = new TrataArquivo();

        nfsLidas = arqv.carregaArquivo();

		System.setProperty("java.awt.headless", "false");

		Tela.getInstance(nfsLidas);
	}
}
