public class PedidoDeLetra extends Comunicado
{
    private char letra;

    public PedidoDeLetra (char letra)
    {
        this.letra = letra;
    }

    public char getLetra ()
    {
        return this.letra;
    }

    public String toString ()
    {
        return (""+this.letra);
    }
}
