public class PedidoDePalavra extends Comunicado
{
    private String palavra;

    public PedidoDePalavra (String palavra)
    {
        this.palavra = palavra;
    }

    public String getPalavra ()
    {
        return this.palavra;
    }

    public String toString ()
    {
        return this.palavra;
    }
}
