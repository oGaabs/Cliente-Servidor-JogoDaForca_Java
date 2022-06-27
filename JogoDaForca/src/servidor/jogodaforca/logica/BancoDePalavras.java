package servidor.jogodaforca.logica;

public class BancoDePalavras {
    private static final String[] palavras =
            {
                    "JAVA",
                    "MALIGNO",
                    "CLASSE",
                    "OBJETO",
                    "INSTANCIA",
                    "PUBLICO",
                    "PRIVATIVO",
                    "METODO",
                    "CONSTRUTOR",
                    "SETTER",
                    "GETTER",
                    "PRAZER"
            };

    public static Palavra getPalavraSorteada() {
        Palavra palavra = null;

        try {
            palavra =
                    new Palavra(BancoDePalavras.palavras[
                            (int) (Math.random() * BancoDePalavras.palavras.length)]);
        } catch (Exception ignored) {
        }

        return palavra;
    }
}
