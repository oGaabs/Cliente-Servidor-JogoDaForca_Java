package socket.comunicado.requisicao;

import socket.Comunicado;

public class PedidoDeLetra extends Comunicado
{
    private final char letra;

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
