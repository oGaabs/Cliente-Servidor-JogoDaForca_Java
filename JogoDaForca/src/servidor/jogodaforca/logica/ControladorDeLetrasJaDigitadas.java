package servidor.jogodaforca.logica;

public class ControladorDeLetrasJaDigitadas implements Cloneable
{
    private String letrasJaDigitadas;

    public ControladorDeLetrasJaDigitadas ()
    {
        this.letrasJaDigitadas = "";
    }

    public boolean isJaDigitada (char letra)
    {
        return this.letrasJaDigitadas.indexOf(letra) != -1;
    }

    public void registre (char letra) throws Exception
    {
        if (this.isJaDigitada(letra))
            throw new Exception("Letra ja digitada");

        this.letrasJaDigitadas = this.letrasJaDigitadas.concat(String.valueOf(letra));
    }

    public String toString ()
    {
        if (this.letrasJaDigitadas.length() == 0)
            return "";

        String letras = String.valueOf(this.letrasJaDigitadas.charAt(0)) ;

        for (int i = 1; i < this.letrasJaDigitadas.length(); i++) {
            letras += ", " + this.letrasJaDigitadas.charAt(i);
        }
        return letras;
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

        if (!this.letrasJaDigitadas.equals(dig.letrasJaDigitadas))
            return false;

        return true;
    }

    public int hashCode ()
    {
        int ret = 999;

        ret = 11 * ret + this.letrasJaDigitadas.hashCode();

        if (ret < 0)
            ret = -ret;

        return ret;
    }
    // construtor de c�pia
    public ControladorDeLetrasJaDigitadas(ControladorDeLetrasJaDigitadas c) throws Exception
    {
        if (c == null)
            throw new Exception("Modelo ausente");

        this.letrasJaDigitadas = c.letrasJaDigitadas;
    }

    public Object clone ()
    {
        ControladorDeLetrasJaDigitadas ret = null;

        try
        {
            ret = new ControladorDeLetrasJaDigitadas(this);
        }
        catch (Exception ignored)
        {}

        return ret;
    }
}
