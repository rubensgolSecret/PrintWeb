package com.print.Print.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.print.Print.controler.interfaces.ITrataArquivo;

public class TrataArquivo implements ITrataArquivo
{
    private static final Logger logger = Logger.getLogger(TrataArquivo.class.getName());

    public void salvaTxt(List<Integer> nfLidas)
    {
        try
        {
            logger.info("abrindo arquivo com as separacoes lidas para salvar");
            FileWriter myWriter = new FileWriter(Util.getDataFormatadaSemBarra() + ".txt");

            logger.info("Salvando arquivo com as separacoes lidas");
            for (Integer integer : nfLidas)
            {
                logger.info("Salvando a separacao: " + integer);
                myWriter.write(integer.toString());
                myWriter.write("\n");
            }

            myWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public List<Integer> carregaArquivo()
    {
        List<Integer> nfs = new ArrayList<>();

        logger.info("Carregando arquivo das separacoes");
        String fileName = Util.getDataFormatadaSemBarra();
        File myObj = new File(fileName + ".txt");

        logger.info("Carregado arquivo: " + fileName + ".txt");

        if (myObj.exists())
        {
            try 
            {
                Scanner myReader = new Scanner(myObj);
                String data;
    
                while (myReader.hasNextLine())
                {
                    data = myReader.nextLine();
                    nfs.add(Integer.parseInt(data));
                    logger.info("Carregado separacoes: " + data);
                }
    
                myReader.close();
            }
            catch (FileNotFoundException e) 
            {
                logger.severe(e.getMessage());
                e.printStackTrace();
            }
        }
        else
        {
            try 
            {
                myObj.createNewFile();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }

        return nfs;
    }

    public static File extraiArquivo(URI uri) throws IOException, DocumentException
    {
        File arquivoPdf = null, arquivoTxt = null;

        File pastaFile = new File(FileUtils.getTempDirectoryPath() + File.separator + "pastaEtiqueta");

        FileUtils.copyURLToFile(uri.toURL(), pastaFile);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(pastaFile.getPath()))) 
        {
            ZipEntry entry;
            byte[] buffer = new byte[1024];

            while ((entry = zis.getNextEntry()) != null) 
            {
                arquivoTxt = new File(FileUtils.getTempDirectoryPath() + File.separator + entry.getName());

                if (entry.isDirectory()) 
                {
                    arquivoTxt.mkdirs();
                }
                else
                {
                    new File(arquivoTxt.getParent()).mkdirs();
                
                    try (FileOutputStream fos = new FileOutputStream(arquivoTxt)) 
                    {
                        int length;

                        while ((length = zis.read(buffer)) > 0) 
                        {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
            }
        }

        if (arquivoTxt != null)
            arquivoPdf = txtToPdf(arquivoTxt);

        return arquivoPdf;
    }


    private static File txtToPdf(File file) throws IOException, DocumentException
    {
        Document pdfDoc = new Document(PageSize.A4);
        PdfWriter.getInstance(pdfDoc, new FileOutputStream("txt.pdf"))
                 .setPdfVersion(PdfWriter.PDF_VERSION_1_7);
        pdfDoc.open();

        Font myfont = new Font();
        myfont.setStyle(Font.NORMAL);
        myfont.setSize(11);
        pdfDoc.add(new Paragraph("\n"));

        BufferedReader br = new BufferedReader(new FileReader(file));
        String strLine;

        while ((strLine = br.readLine()) != null) 
        {
            Paragraph para = new Paragraph(strLine + "\n", myfont);
            para.setAlignment(Element.ALIGN_JUSTIFIED);
            pdfDoc.add(para);
        }

        pdfDoc.close();
        br.close();

        return new File("txt.pdf");
    }
}
