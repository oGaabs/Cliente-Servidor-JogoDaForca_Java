public class ControladorDeLetrasJaDigitadas implements Cloneable
{
    private String letrasJaDigitadas;

    public ControladorDeLetrasJaDigitadas ()
    {
        this.letrasJaDigitadas = "";
        // torna this.letrasJaDigitadas igual ao String vazio
    }

    public boolean isJaDigitada (char letra)
    {
        if (this.letrasJaDigitadas.indexOf(letra) != -1)
        {
            return true;
        }

        return false;
        // percorrer o String this.letrasJaDigitadas e verificar se ele
        // possui a letra fornecida, retornando true em caso afirmativo
        // ou false em caso negativo
    }

    public void registre (char letra) throws Exception
    {
        if (this.isJaDigitada(letra)) {
            throw new Exception("Letra ja digitada");
        }
        this.letrasJaDigitadas = this.letrasJaDigitadas.concat(String.valueOf(letra));
		// verifica se a letra fornecida ja foi digitada (pode usar
		// o m�todo this.isJaDigitada, para isso), lancando uma exce��o
		// em caso afirmativo.
		// concatena a letra fornecida a this.letrasJaDigitadas.
    }

    public String toString ()
    {
        if (this.letrasJaDigitadas.length() == 0) {
            return "";
        }
        String letras = String.valueOf(this.letrasJaDigitadas.charAt(0)) ;

        for (int i = 1; i < this.letrasJaDigitadas.length(); i++) {
            letras += ", " + this.letrasJaDigitadas.charAt(i);
        }
        return letras;
		// retorna um String com TODAS as letras presentes em
		// this.letrasJaDigitadas separadas por v�rgula (,).
    }

    public boolean equals (Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (this.getClass() != obj.getClass())
            return false;

        ControladorDeLetrasJaDigitadas dig = (ControladorDeLetrasJaDigitadas) obj;

        if (this.letrasJaDigitadas != dig.letrasJaDigitadas)
            return false;

        return true;
        // verificar se this e obj s�o iguais
    }

    public int hashCode ()
    {
        int ret = 999;

        ret = 11 * ret + this.letrasJaDigitadas.hashCode();

        if (ret < 0)
            ret = -ret;

        return ret;
        // calcular e retornar o hashcode de this
    }
    // construtor de c�pia
    public ControladorDeLetrasJaDigitadas(ControladorDeLetrasJaDigitadas c) throws Exception
    {
        if (c == null)
            throw new Exception("Modelo ausente");

        this.letrasJaDigitadas = c.letrasJaDigitadas;
        // copiar c.letrasJaDigitadas em this.letrasJaDigitadas
    }

    public Object clone ()
    {
        ControladorDeLetrasJaDigitadas ret = null;

        try
        {
            ret = new ControladorDeLetrasJaDigitadas(this);
        }
        catch (Exception erro)
        {}

        return ret;
        // criar uma c�pia do this com o construtor de c�pia e retornar
    }
}
