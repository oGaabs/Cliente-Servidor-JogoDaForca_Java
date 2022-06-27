package servidor.jogodaforca.logica;

public class Palavra implements Comparable<Palavra>
{
    private final String texto;

    public Palavra (String texto) throws Exception
    {
        if (texto == null || texto.equals(""))
            throw new Exception("Texto inexistente");

        this.texto = texto;
    }

    public int getQuantidade (char letra)
    {
        int qtd = 0;

        for(int i = 0;i < getTamanho();i++){
            if (this.texto.charAt(i) == letra)
                qtd++;
        }
        return qtd;
    }

    public int getPosicaoDaIezimaOcorrencia (int i, char letra) throws Exception
    {
        int qtdOcorrenciasIgnoradas = i;
        int posOcorrenciaAtual = -1;
        // Encontra a ocorrencia desejada, indo de ocorrencia a ocorrencia da letra fornecida
        while(qtdOcorrenciasIgnoradas > -1) {
            // Encontra a proxima ocorrencia da letra
            posOcorrenciaAtual = this.texto.indexOf(letra, posOcorrenciaAtual + 1);
            qtdOcorrenciasIgnoradas--;
        }

        if(posOcorrenciaAtual == -1)
            throw new Exception("Posição não encontrada!");

        return posOcorrenciaAtual;
    }

    public int getTamanho ()
    {
        return this.texto.length();
    }

    public String toString ()
    {
        return this.texto;
    }

    public boolean equals (Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (this.getClass() != obj.getClass())
            return false;

        Palavra txt = (Palavra) obj;

        if (!this.texto.equals(txt.texto))
            return false;

        return true;
    }

    public int hashCode ()
    {
        int ret = 999;

        if(this.texto != null)
            ret = 11 * ret + this.texto.hashCode();

        if (ret < 0)
            ret = -ret;

        return ret;
    }

    public int compareTo (Palavra palavra)
    {
        return this.texto.compareTo(palavra.texto);
    }
}

