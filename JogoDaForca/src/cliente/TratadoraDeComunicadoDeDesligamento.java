package cliente;

import socket.comunicado.resposta.ComunicadoDePerdedor;
import socket.*;

public class TratadoraDeComunicadoDeDesligamento extends Thread
{
    private final Parceiro servidor;

    public TratadoraDeComunicadoDeDesligamento (Parceiro servidor) throws Exception
    {
        if (servidor == null)
            throw new Exception ("Porta invalida");

        this.servidor = servidor;
    }

    public void run ()
    {
        while (true)
        {
			try
			{
                // Servidor foi desligado
				if (this.servidor.espie() instanceof ComunicadoDeDesligamento)
				{
					System.out.println ("\nO servidor vai ser desligado agora;\n" +
                                        "volte mais tarde!");
				    System.exit(0);
				}

                // Perdeu o jogo, então mostra o motivo/resultado
                Comunicado comunicadoDoServidor = this.servidor.espie();
                if (comunicadoDoServidor instanceof ComunicadoDePerdedor)
                    System.out.println ("Você perdeu!\n" + comunicadoDoServidor);

                Thread.yield();
			}
			catch (Exception ignored){}
        }
    }
}
