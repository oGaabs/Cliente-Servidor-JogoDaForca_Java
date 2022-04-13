public class Temporizador {

    private long valorInicial = 0;
    private long valorDeParada = 0;
    private long diferencaTempo = 0;

    public Temporizador () throws Exception
    {}

    public void iniciarTemporizador(long valorDeParada) {
        this.valorInicial = System.currentTimeMillis();
        this.valorDeParada = valorDeParada;
        diferencaTempo = 0;
    }

    public boolean isTerminado() {
        diferencaTempo = calcularDiferenca();
        if (diferencaTempo >= valorDeParada)
            return true;

        return false;
    }

    private long calcularDiferenca(){
        return System.currentTimeMillis() - valorInicial;
    }

}
