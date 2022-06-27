package socket.comunicado.requisicao;

import socket.Comunicado;

public class PedidoDePalavra extends Comunicado
{
    private final String palavra;

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
