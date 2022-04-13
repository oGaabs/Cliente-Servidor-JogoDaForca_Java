public class PedidoPerdedor extends Comunicado
{
    private String palavra;
    private boolean outroGanhador = false;

    public PedidoPerdedor (boolean outroGanhador, String palavra)
    {
        this.outroGanhador = outroGanhador;
        this.palavra = palavra;
    }

    public String toString ()
    {
        return (outroGanhador ? "Outra pessoa Ganhou! A palavra era "+this.palavra: "A palavra era: "+this.palavra);
    }
}
