package socket.comunicado.resposta;

import socket.Comunicado;

public class ComunicadoDeTracinhos extends Comunicado
{
    private final String tracinhostxt;

    public ComunicadoDeTracinhos(String tracinhotxt){
        this.tracinhostxt = tracinhotxt;
    }

    @Override
    public String toString() {
        return this.tracinhostxt;
    }
}
