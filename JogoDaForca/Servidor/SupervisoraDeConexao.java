import java.util.*;

public class SupervisoraDeConexao extends Thread
{
    private ArrayList<Parceiro> usuarios;
    private ArrayList<Parceiro> grupo;
    private Parceiro            usuario;
    private boolean isFim = false;
    private int numVez = 0;

    public SupervisoraDeConexao(ArrayList<Parceiro> usuarios, ArrayList<Parceiro> grupo) throws Exception
    {
        if (usuarios==null)
            throw new Exception ("Usuarios ausentes");

        if (grupo==null) {
            throw new Exception ("Grupo ausente");
        }

        this.usuarios = usuarios;
        this.grupo = grupo;
    }

    public void run ()
    {
        Palavra palavra = BancoDePalavras.getPalavraSorteada();

        Tracinhos tracinhos = null;
        try
        {
            tracinhos = new Tracinhos (palavra.getTamanho());
        }
        catch (Exception erro)
        {}

        ControladorDeLetrasJaDigitadas controlJaDigitadas =
                    new ControladorDeLetrasJaDigitadas ();

        Temporizador temporizador = null;
        try
        {
            temporizador = new Temporizador();
        }
        catch (Exception erro)
        {}

        try
        {
            Thread.sleep(1000);
            this.grupo.get(0).receba(new PedidoDeGrupoFechado());
            this.grupo.get(1).receba(new PedidoDeGrupoFechado());
            this.grupo.get(2).receba(new PedidoDeGrupoFechado());
        }
        catch (Exception e) {
            System.err.println("Erro ao mandar grupos.");
        }

        do {

            try
            {
                synchronized (this.grupo){
                    if(this.grupo.size() <= 1){
                        this.grupo.get(0).receba(new PedidoGanhador());
                        this.grupo.get(0).adeus();
                        this.usuarios.remove(this.grupo.get(0));
                        break;
                    }
                }
                this.usuario = this.grupo.get(this.numVez);
                Comunicado comunicado = null;
                comunicado = this.usuario.envie();
                if (comunicado==null)
                    return;
                else if (comunicado instanceof PedidoDeTracinhos)
                {
                    this.usuario.receba(new ComunicadoTracinhos(tracinhos.toString()));
                    temporizador.iniciarTemporizador(100000);
                }
                else if (comunicado instanceof PedidoDeLetra)
                {
                    if (temporizador.isTerminado()){
                        this.usuario.receba(new AcabouOTempo());
                        passarVez();
                        continue;
                    }

                    PedidoDeLetra pedidoDeLetra = (PedidoDeLetra) comunicado;

                    char letra = Character.toUpperCase(pedidoDeLetra.getLetra());

                    if (!controlJaDigitadas.isJaDigitada (letra))
                    {
                        controlJaDigitadas.registre (letra);

                        int qtd = palavra.getQuantidade (letra);

                        if (qtd==0)
                        {
                            this.usuario.receba(new Errou());
                            passarVez();
                        }
                        else
                        {
                            for (int i=0; i<qtd; i++)
                            {
                                int posicao = palavra.getPosicaoDaIezimaOcorrencia (i,letra);
                                tracinhos.revele (posicao, letra);
                            }
                            if (!tracinhos.isAindaComTracinhos()) {
                                this.usuario.receba(new PedidoGanhador());
                                this.usuario.adeus();
                                this.grupo.remove(this.usuario);
                                this.isFim = true;
                                for (Parceiro parceiro : this.grupo) {
                                    parceiro.receba(new PedidoPerdedor(true, palavra.toString()));;
                                }
                                break;
                            }
                            this.usuario.receba(new Acertou());
                            passarVez();
                        }
                    }
                    else{
                        this.usuario.receba(new Errou());
                        passarVez();
                    }
                }
                else if (comunicado instanceof PedidoDePalavra)
                {
                    if (temporizador.isTerminado()) {
                        this.usuario.receba(new AcabouOTempo());
                        passarVez();
                        continue;
                    }

                    Palavra pedidoPalavra = new Palavra(comunicado.
                                                        toString().toUpperCase());

                    if (palavra.equals(pedidoPalavra))
                    {
                        this.usuario.receba (new PedidoGanhador());
                        this.isFim = true;
                        this.usuario.adeus();
                        this.grupo.remove(this.usuario);
                        for (Parceiro parceiro : this.grupo) {
                            parceiro.receba(new PedidoPerdedor(true, palavra.toString()));;
                        }
                        this.grupo.clear();
                    }
                    else{
                        this.usuario.receba (new PedidoPerdedor(false, palavra.toString()));
                        this.usuario.adeus();
                        this.grupo.remove(this.usuario);
                        passarVez();
                    }
                }
                else if (comunicado instanceof PedidoParaSair)
                {
                    synchronized (this.usuarios)
                    {
                        this.usuarios.remove (this.usuario);
                    }
                    this.usuario.adeus();
                    this.grupo.remove(this.usuario);
                    passarVez();
                }
            }
            catch (Exception erro)
            {
                try
                {
                    synchronized (this.usuarios)
                    {
                        for (Parceiro parceiro : this.grupo) {
                            parceiro.adeus();
                            this.usuarios.remove(parceiro);
                        }
                        this.usuarios.remove (this.usuario);
                    }
                }
                catch (Exception falha)
                {} // so tentando fechar antes de acabar a thread

                return;
            }
        }
        while (!isFim);
    }

    private void passarVez(){
        if (this.numVez >= this.grupo.size()-1) {
            this.numVez = -1;
        }
        this.numVez++;
        this.usuario = this.grupo.get(this.numVez);
    }
}
