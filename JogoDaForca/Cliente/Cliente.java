import java.net.*;
import java.io.*;

public class Cliente
{
	public static final String HOST_PADRAO  = "localhost";
	public static final int    PORTA_PADRAO = 3000;

	public static void main (String[] args)
	{
        if (args.length>2)
        {
            System.err.println ("Uso esperado: java Cliente [HOST [PORTA]]");
            Teclado.getUmString();
			return;
        }

        Socket conexao=null;
        try
        {
            String host = Cliente.HOST_PADRAO;
            int    porta= Cliente.PORTA_PADRAO;

            if (args.length>0)
                host = args[0];

            if (args.length==2)
                porta = Integer.parseInt(args[1]);

            conexao = new Socket (host, porta);
        }
        catch (Exception erro)
        {
            System.err.println ("Indique o servidor e a porta corretos!");
            Teclado.getUmString();
			return;
        }

        ObjectOutputStream transmissor=null;
        try
        {
            transmissor =
            new ObjectOutputStream(
            conexao.getOutputStream());
        }
        catch (Exception erro)
        {
            System.err.println ("Indique o servidor e a porta corretos!");
            try
			{
				conexao.close();
			}
			catch (Exception Falha) // so tentando fechar antes de dar return
			{}
			Teclado.getUmString();
			return;
        }

        ObjectInputStream receptor=null;
        try
        {
            receptor =
            new ObjectInputStream(
            conexao.getInputStream());
        }
        catch (Exception erro)
        {
            System.err.println ("Indique o servidor e a porta corretos!");
			try
			{
				conexao.close();
			}
			catch (Exception Falha) // so tentando fechar antes de dar return
			{}
			Teclado.getUmString();
			return;
        }

        Parceiro servidor=null;
        try
        {
            servidor =
            new Parceiro (conexao, receptor, transmissor);
        }
        catch (Exception erro)
        {
            System.err.println ("Indique o servidor e a porta corretos!");
            Teclado.getUmString();
			return;
        }

        TratadoraDeComunicadoDeDesligamento tratadoraDeComunicadoDeDesligamento = null;
        try
        {
			tratadoraDeComunicadoDeDesligamento = new TratadoraDeComunicadoDeDesligamento (servidor);
		}
		catch (Exception erro)
		{}

        tratadoraDeComunicadoDeDesligamento.start();
		ControladorDeLetrasJaDigitadas controlDigitadas = new ControladorDeLetrasJaDigitadas();

		System.out.println ("Espere a conexão com o servidor...");
		Comunicado comunicado = null;

		try
		{
			try
			{
				do
				{
					comunicado = servidor.espie();
				}
				while (!(comunicado instanceof PedidoDeGrupoFechado));

			}
			catch (Exception e)
			{
				System.out.println(e);
			}
			System.out.println ("Conexão sucedida");
			do
			{
				System.out.println ("Espere sua vez de jogar...");

				comunicado = null;
				do
				{
					try
					{
						servidor.receba(new PedidoDeTracinhos());
					}
					catch (Exception e)
					{}
					try
					{
						comunicado = servidor.envie();
					}
					catch (Exception e)
					{}
				}
				while (!(comunicado instanceof ComunicadoTracinhos) && !(comunicado instanceof PedidoPerdedor)
						&& !(comunicado instanceof PedidoGanhador));
				if (comunicado instanceof PedidoPerdedor) {
					break;
				}
				else if (comunicado instanceof PedidoGanhador){
					System.out.println("Você sobrou e ganhou...");
					break;
				}
				System.out.println("Agora é sua vez");

				System.out.println(comunicado);
				System.out.println("Letras digitadas: "+controlDigitadas+"\n");
				System.out.print ("Escolha uma jogada\n"+
									"'P' Para digitar a Palavra\n"+
									"'L' Para tentar uma letra\n");

				char jogada = ' ';
				do {

					try
					{
						jogada = Character.toUpperCase(Teclado.getUmChar());
					}
					catch (Exception erro)
					{
						System.err.println ("Jogada invalida!\n");
						continue;
					}

					if ("PL".indexOf(jogada)==-1)
					{
						System.err.println ("Jogada invalida!\n");
						continue;
					}
				} while ("PL".indexOf(jogada)==-1);

				if (jogada == 'P'){
					String palavra = "";
					System.out.println("Digite a palavra, você tem 20 segundos:");
					palavra = Teclado.getUmString().toUpperCase();

					System.out.println("Enviando palavra ao server");

					servidor.receba(new PedidoDePalavra (palavra));
					System.out.println("Verificando se a palavra está correta!");
					do
					{
						comunicado = servidor.envie();
					}
					while (!(comunicado instanceof AcabouOTempo) && !(comunicado instanceof PedidoPerdedor) &&
						   !(comunicado instanceof PedidoGanhador));
					if (comunicado instanceof AcabouOTempo){
						System.out.println("O seu tempo acabou! PERDEU A VEZ.");
						System.out.println("Espere até ser sua vez, de novo.");
					}
					else {
						if (comunicado instanceof PedidoPerdedor){
							System.out.println("Você errou a palavra e portanto perdeu o jogo.");
							System.out.println((PedidoPerdedor) comunicado);
						}
						else if (comunicado instanceof PedidoGanhador){
							System.out.println("Acertou a palavra e Ganhou o Jogo.");
						}
					}
				}
				else
				{
					char letra = ' ';
					do
					{
						System.out.println("Digite uma letra, você tem 10 segundos:");
						letra = Character.toUpperCase(Teclado.getUmChar());
						if (controlDigitadas.isJaDigitada(letra) || letra == ' ') {
							System.out.println("Letra ja digitada.");
							letra  = ' ';
						}
					}
					while (letra == ' ');
					if (comunicado instanceof PedidoPerdedor) {
						break;
					}
					else if (comunicado instanceof PedidoGanhador){
						System.out.println("Você sobrou e ganhou...");
						break;
					}
					controlDigitadas.registre(letra);
					System.out.println("Enviando letra ao server");
					servidor.receba(new PedidoDeLetra (letra));
					do
					{
						System.out.println("Verificando se acertou");
						comunicado = servidor.envie();
					}
					while (!(comunicado instanceof AcabouOTempo) && !(comunicado instanceof Errou)
					       && !(comunicado instanceof Acertou)  && !(comunicado instanceof PedidoGanhador)
						   && !(comunicado instanceof PedidoPerdedor) );

					if (comunicado instanceof PedidoPerdedor) {
						break;
					}
					else if (comunicado instanceof PedidoGanhador){
						System.out.println("Você sobrou e ganhou...");
						break;
					}
					if (comunicado instanceof AcabouOTempo){
						System.out.println("O seu tempo acabou! PERDEU A VEZ.");
					}
					else if (comunicado instanceof Errou){
						System.out.println("Você errou a letra.");
					}
					else if(comunicado instanceof Acertou){
						System.out.println("Acertou a letra.");
					}
					else if(comunicado instanceof PedidoGanhador){
						System.out.println("Acertou a letra e ganhou parabéns.");
					}
				}
			}
			while (!(comunicado instanceof PedidoGanhador) && !(comunicado instanceof PedidoPerdedor));

			if (comunicado instanceof PedidoGanhador) {
				System.out.println("PARABÉNS VOCÊ GANHOU.\n");
			}
			else
			{
				PedidoPerdedor pp = (PedidoPerdedor)  comunicado;
				System.out.println("PARABÉNS VOCÊ PERDEU.\n"+
								   pp);
			}
		}
		catch (Exception erro)
		{
			System.err.println ("Erro de comunicacao com o servidor;");
			System.err.println ("Tente novamente!");
			System.err.println ("Caso o erro persista, termine o programa");
			System.err.println ("e volte a tentar mais tarde!");
			System.err.println ("Enter para fechar.");
			System.err.println (Teclado.getUmString());
		}

		try
		{
			servidor.receba (new PedidoParaSair ());
		}
		catch (Exception erro)
		{}

		System.out.println ("Obrigado por usar este programa!");
		System.exit(0);
	}
}

