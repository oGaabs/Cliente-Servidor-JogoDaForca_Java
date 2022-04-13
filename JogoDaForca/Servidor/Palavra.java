public class Palavra implements Comparable<Palavra>
{
    private String texto;

    public Palavra (String texto) throws Exception
    {
        if (texto == null || texto == "")
        throw new Exception("Texto inexistente");

        this.texto = texto;
		// verifica se o texto recebido � nulo ou ent�o vazio,
		// ou seja, sem nenhum caractere, lan�ando exce��o.
		// armazena o texto recebido em this.texto.
    }

    public int getQuantidade (char letra)
    {
        int qtd = 0;

        for(int i = 0;i < getTamanho();i++){
            if (this.texto.charAt(i) == letra){
                qtd++;
            }
        }
        return qtd;
        // percorre o String this.texto, conta e retorna
        // quantas letras existem nele iguais a letra fornecida
    }

    public int getPosicaoDaIezimaOcorrencia (int i, char letra) throws Exception
    {
        int qtd = i;
        int pos = -1;
        while(qtd > -1) {
            pos = this.texto.indexOf(letra, pos + 1);
            qtd--;
        }
        if(pos == -1)
            throw new Exception("Posição não encontrada!");
        return pos;
        // se i==0, retorna a posicao em que ocorre a primeira
        // aparicao de letra fornecida em this.texto;
        // se i==1, retorna a posicao em que ocorre a segunda
        // aparicao de letra fornecida em this.texto;
        // se i==2, retorna a posicao em que ocorre a terceira
        // aparicao de letra fornecida em this.texto;
        // e assim por diante.
        // lan�ar excecao caso nao encontre em this.texto
        // a I�zima apari��o da letra fornecida.
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
        // verificar se this e obj possuem o mesmo conte�do, retornando
        // true no caso afirmativo ou false no caso negativo
    }

    public int hashCode ()
    {
        int ret = 999;

        if(this.texto != null)
        ret = 11 * ret + this.texto.hashCode();

        if (ret < 0)
            ret = -ret;

        return ret;
        // calcular e retornar o hashcode de this
    }

    public int compareTo (Palavra palavra)
    {
        return this.texto.compareTo(palavra.texto);
    }
}
