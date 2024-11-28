package com.print.Print.controler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.print.Print.controler.business.comunicaTiny.Comunica;
import com.print.Print.controler.business.imprimir.ImprimirDesktop;
import com.print.Print.controler.interfaces.ITrataArquivo;
import com.print.Print.model.EnumRetorno;
import com.print.Print.util.TrataArquivo;

@RestController
public class EndPointers
{
     ITrataArquivo aTrataArquivo;
     Comunica comunica;
     List<Integer> separaLida;
     Busca busca;

    @GetMapping("/irpaginaPrint")
	  public ModelAndView irpaginaPrint() 
    {
		  return new ModelAndView("print_home.html");
	  }

    @GetMapping("/buscar")
	  public HttpStatus buscar(@RequestParam(value = "token") String token, 
                             @RequestParam(value = "buscar", defaultValue = "true") boolean buscar) 
    {
      aTrataArquivo = new TrataArquivo();
      separaLida =  aTrataArquivo.carregaArquivo();
      comunica = new Comunica(separaLida);

      if (buscar)
      {
        try 
        {
           EnumRetorno retorno = comunica.verificaConexao(token);

            if (retorno == EnumRetorno.SUCESSO || retorno == EnumRetorno.SUCESSO_EM_BRANCO)
            {
                busca = Busca.getInstance(comunica, token, new ImprimirDesktop(), aTrataArquivo);
                busca.buscar();
            }
            else
            {
              return HttpStatus.UNAUTHORIZED;
            }

            Thread.sleep(5000);
        } 
        catch (Exception e)
        {
          return HttpStatus.EXPECTATION_FAILED;
        }
      }
      else
      {
        busca.parar();
      }

      return HttpStatus.OK;
  }
}