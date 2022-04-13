public class BancoDePalavras
{
    private static String[] palavras =
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

    public static Palavra getPalavraSorteada ()
    {
        Palavra palavra = null;

        try
        {
            palavra =
            new Palavra (BancoDePalavras.palavras[
            (int)(Math.random() * BancoDePalavras.palavras.length)]);
        }
        catch (Exception e)
        {}

        return palavra;
    }
}
