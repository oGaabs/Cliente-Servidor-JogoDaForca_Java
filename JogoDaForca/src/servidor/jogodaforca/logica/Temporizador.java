package servidor.jogodaforca.logica;

public class Temporizador {

    private long valorInicial = 0;
    private long valorDeParada = 0;
    private long tempoDecorrido = 0;

    public Temporizador () {}

    public void iniciarTemporizador(long valorDeParada) {
        this.valorInicial = System.currentTimeMillis();
        this.valorDeParada = valorDeParada;
        tempoDecorrido = 0;
    }

    public boolean isTerminado() {
        tempoDecorrido = calcularDiferenca();

        return tempoDecorrido >= valorDeParada;
    }

    private long calcularDiferenca(){
        return System.currentTimeMillis() - valorInicial;
    }

}
