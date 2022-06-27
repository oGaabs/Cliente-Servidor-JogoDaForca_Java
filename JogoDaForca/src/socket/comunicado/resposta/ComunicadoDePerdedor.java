package socket.comunicado.resposta;

import socket.Comunicado;

public class ComunicadoDePerdedor extends Comunicado {
    private final String palavra;
    private final boolean outroGanhador;

    public ComunicadoDePerdedor(boolean outroGanhador, String palavra) {
        this.outroGanhador = outroGanhador;
        this.palavra = palavra;
    }

    public String toString() {
        return (outroGanhador ? "Outra pessoa Ganhou! A palavra era " + this.palavra : "A palavra era: " + this.palavra);
    }
}
