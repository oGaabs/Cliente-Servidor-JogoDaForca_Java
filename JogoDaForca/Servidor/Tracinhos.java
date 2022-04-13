public class Tracinhos
{
    private char texto [];

    public Tracinhos (int qtd) throws Exception
    {
        if (qtd < 1) {
            throw new Exception("Quantidade invalida");
        }
        this.texto = new char[qtd];;
        for (int i = 0; i < texto.length; i++) {
            texto[i] = '_';
        }
    }

    public void revele (int posicao, char letra) throws Exception
    {
        if (posicao < 0 || posicao >= this.texto.length) {
            throw new Exception("Posição invalida");
        }
        this.texto[posicao] = letra;
    }

    public boolean isAindaComTracinhos ()
    {
        for(int i = 0;i < texto.length-1;i++){
            if (this.texto[i] == ('_')){
                return true;
            }
        }
        return false;
    }
    @Override
    public String toString ()
    {
        String letras = String.valueOf(this.texto[0]);

        for (int i = 1; i < this.texto.length; i++) {
            letras += ' ' + String.valueOf(this.texto[i]);
        }
        return letras;
    }
    @Override
    public boolean equals (Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (this.getClass() != obj.getClass())
            return false;

        Tracinhos trac = (Tracinhos) obj;
        int lenA = this.texto.length;
        int lenB = trac.texto.length;

        if (lenA != lenB) {
            return false;
        }

        for(int i = 0; i < lenA; i++){
            if(this.texto[i] != trac.texto[i])
                return false;
        }

        return true;
    }
    @Override
    public int hashCode ()
    {
        int ret = 999;

        ret = 11 * ret + this.texto.hashCode();

        if (ret < 0)
            ret = -ret;

        return ret;
    }

    public Tracinhos (Tracinhos t) throws Exception
    {
        if (t == null)
            throw new Exception("Modelo ausente");

        this.texto = t.texto;
    }

    public Object clone ()
    {
        Tracinhos ret = null;

        try
        {
            ret = new Tracinhos(this);
        }
        catch (Exception erro)
        {}

        return ret;
    }
}
