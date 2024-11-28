function clickIniciar() 
{
    if($( "#token" ).val() == '')
    {
            $('#erroTokenBranco').fadeIn(1000);
        setTimeout(function() { 
           $('#erroTokenBranco').fadeOut(1000); 
          }, 2000);
    }
    else 
    {
        $( "#iniciar" ).addClass("disabled");
        $( "#parar" ).removeClass("disabled");

        $.ajax({
            url: "/buscar",

            data: {
                token: $( "#token" ).val()
            },
        
            type: "GET",
            dataType : "json",
        })

        .done(function( json ) 
        {
            if (json = 'UNAUTHORIZED')
            {
                $( "#iniciar" ).removeClass("disabled");
                $( "#parar" ).addClass("disabled");

                $('#erroTokenInvalido').fadeIn(1000);
                setTimeout(function() { 
                   $('#erroTokenInvalido').fadeOut(1000); 
                  }, 2000);
            }
        })
    }
}

function clickParar() 
{
    $( "#iniciar" ).removeClass("disabled");
    $( "#parar" ).addClass("disabled");

    $.ajax({
        url: "/buscar",

        data: {
            buscar: false,
            token: $( "#token" ).val()
        },
    
        type: "GET",
        dataType : "json",
    })
}
