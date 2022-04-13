public class TratadoraDeComunicadoDeDesligamento extends Thread
{
    private Parceiro servidor;

    public TratadoraDeComunicadoDeDesligamento (Parceiro servidor) throws Exception
    {
        if (servidor==null)
            throw new Exception ("Porta invalida");

        this.servidor = servidor;
    }

    public void run ()
    {
        for(;;)
        {
			try
			{
                Thread.sleep(10);
				if (this.servidor.espie() instanceof ComunicadoDeDesligamento)
				{
					System.out.println ("\nO servidor vai ser desligado agora;");
				    System.err.println ("volte mais tarde!\n");
				    System.exit(0);
				}
                Comunicado c = this.servidor.espie();
                if (c instanceof PedidoPerdedor) {
                    System.out.println ("Você perdeu!");
				    System.out.println ((PedidoPerdedor) c);
                }
                Thread.yield();
			}
			catch (Exception erro)
			{}
        }
    }
}
