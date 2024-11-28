package com.print.Print.controler.business.imprimir;

import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.Loader;

import com.itextpdf.text.DocumentException;
import com.print.Print.controler.interfaces.IImprimir;
import com.print.Print.util.TrataArquivo;

public class ImprimirDesktop implements IImprimir
{
	private static final Logger logger = Logger.getLogger(ImprimirDesktop.class.getName());

	public void imprimir(String path) 
	{
		try
		{
			File docFile = null;
			PDDocument document = null;
			URI uri = new URI(path);
			URL url = uri.toURL();

			if (path.endsWith(".zip"))
				docFile = TrataArquivo.extraiArquivo(uri);
			else
			{
				String nameFile = FileUtils.getTempDirectoryPath() + "tmp" + ".pdf";

				docFile = new File(nameFile);
				docFile.deleteOnExit();
				FileUtils.copyURLToFile(url, docFile);
			}

			if (docFile == null)
				throw new IOException("Problema com arquivo de etiqueta");

			logger.info("imprimindo arquivo: " + path);

			document = Loader.loadPDF(docFile);
			PrintJob.printFile(document);
		}
		catch (URISyntaxException | IOException | PrinterException | DocumentException e) 
        {
			logger.severe(e.getMessage());
            e.printStackTrace();
        }
	}
}
