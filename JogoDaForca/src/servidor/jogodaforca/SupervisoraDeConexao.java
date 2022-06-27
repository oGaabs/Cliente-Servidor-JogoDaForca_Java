package servidor.jogodaforca;

import servidor.jogodaforca.logica.*;
import socket.comunicado.requisicao.*;
import socket.comunicado.resposta.*;
import socket.*;

import java.util.*;

public class SupervisoraDeConexao extends Thread
{
    private final ArrayList<Parceiro> usuarios;
    private final ArrayList<Parceiro> grupo;
    private Parceiro conexaoJogadorAtual;
    private boolean isFim = false;
    private int numeroDoJogador = 0;

    public SupervisoraDeConexao(ArrayList<Parceiro> jogadores, ArrayList<Parceiro> grupo) throws Exception
    {
        if (jogadores==null)
            throw new Exception ("Jogadores ausentes");

        if (grupo==null)
            throw new Exception ("Grupo ausente");

        this.usuarios = jogadores;
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
        catch (Exception ignored)
        {}

        ControladorDeLetrasJaDigitadas controlJaDigitadas =
                    new ControladorDeLetrasJaDigitadas ();

        Temporizador temporizador = null;
        try
        {
            temporizador = new Temporizador();
        }
        catch (Exception ignored)
        {}

        try
        {
            Thread.sleep(1000);
            for (Parceiro jogador : this.grupo)
                jogador.receba(new GrupoFoiCompletado());
        }
        catch (Exception e) {
            System.err.println("Erro ao mandar grupos.");
        }

        while (true)
        {
            try
            {
                // Caso reste apenas um jogador, então ele será o ganhador pelos outros perderem
                if(this.grupo.size() == 1){
                    this.grupo.get(0).receba(new ComunicadoDeGanhador());
                    this.grupo.get(0).adeus();

                    // Remove o jogador de usuarios
                    synchronized (this.usuarios){
                        this.usuarios.remove(this.grupo.get(0));
                    }

                    break;
                }


                this.conexaoJogadorAtual = this.grupo.get(this.numeroDoJogador);

                Comunicado comunicado = this.conexaoJogadorAtual.envie();

                if (comunicado==null)
                    return;
                    // O jogador atual pediu a para receber a palavra escondida
                else if (comunicado instanceof PedidoDeTracinhos)
                {
                    this.conexaoJogadorAtual.receba(new ComunicadoDeTracinhos(tracinhos.toString()));
                    temporizador.iniciarTemporizador(100000);
                }
                // O jogador atual decidiu tentar uma letra
                else if (comunicado instanceof PedidoDeLetra)
                {
                    // Verifica se o jogador digitou a letra no tempo determinado
                    // caso não, ele perde a vez
                    if (temporizador.isTerminado()){
                        this.conexaoJogadorAtual.receba(new AcabouOTempo());
                        passarVez();
                        continue;
                    }

                    PedidoDeLetra pedidoDeLetra = (PedidoDeLetra) comunicado;

                    char letra = Character.toUpperCase(pedidoDeLetra.getLetra());

                    // Caso a letra não tenha sido digitada, verifica se é correta
                    if (!controlJaDigitadas.isJaDigitada (letra))
                    {
                        controlJaDigitadas.registre (letra);

                        int qtd = palavra.getQuantidade (letra);

                        // Errou a letra e perdeu a vez
                        if (qtd==0)
                        {
                            this.conexaoJogadorAtual.receba(new Errou());
                            passarVez();
                        }
                        else
                        {
                            // Revela a letra em todas as posições que ela aparece
                            for (int i=0; i<qtd; i++)
                            {
                                int posicao = palavra.getPosicaoDaIezimaOcorrencia (i,letra);
                                tracinhos.revele (posicao, letra);
                            }
                            // Verifica se ainda restam letras escondidas
                            if (!tracinhos.isAindaComTracinhos()) {
                                // Caso não reste letras, então a pessoa ganha e então
                                // as outras perdem, finalizando o jogo
                                this.conexaoJogadorAtual.receba(new ComunicadoDeGanhador());
                                this.conexaoJogadorAtual.adeus();

                                this.grupo.remove(this.conexaoJogadorAtual);
                                this.isFim = true;
                                // Revela que ouve outro ganhador e por isso perderam
                                // mostrando a palavra escondida
                                for (Parceiro parceiro : this.grupo) {
                                    parceiro.receba(new ComunicadoDePerdedor(true, palavra.toString()));
                                }
                                break;
                            }
                            // O jogador acertou a letra e assim, passou a vez
                            this.conexaoJogadorAtual.receba(new Acertou());
                            passarVez();
                        }
                    }
                    else{
                        // Errou a letra e perdeu a vez
                        this.conexaoJogadorAtual.receba(new Errou());
                        passarVez();
                    }
                }
                // O jogador atual decidiu tentar uma palavra
                else if (comunicado instanceof PedidoDePalavra)
                {
                    if (temporizador.isTerminado()) {
                        this.conexaoJogadorAtual.receba(new AcabouOTempo());
                        passarVez();
                        continue;
                    }

                    Palavra pedidoPalavra = new Palavra(comunicado.
                            toString().toUpperCase());

                    if (palavra.equals(pedidoPalavra))
                    {
                        // Caso ele acerte a palavra, ele vira o ganhador e é despedido do jogo
                        // finalizando assim o jogo
                        this.conexaoJogadorAtual.receba (new ComunicadoDeGanhador());
                        this.conexaoJogadorAtual.adeus();

                        this.grupo.remove(this.conexaoJogadorAtual);
                        this.isFim = true;

                        // Removendo assim, os outros jogadores com a condição de perdedor
                        for (Parceiro parceiro : this.grupo) {
                            parceiro.receba(new ComunicadoDePerdedor(true, palavra.toString()));
                        }
                        this.grupo.clear();
                    }
                    else{
                        // Caso ele erre, ele perde o jogo e os outros jogadores continuam jogando
                        // saindo assim na condição de perdedor e passando a vez
                        this.conexaoJogadorAtual.receba (new ComunicadoDePerdedor(false, palavra.toString()));
                        this.conexaoJogadorAtual.adeus();
                        this.grupo.remove(this.conexaoJogadorAtual);
                        passarVez();
                    }
                }
                // Caso algum jogador peça para sair, ele é removido da partida e dos jogadores
                // passando então a vez
                else if (comunicado instanceof PedidoParaSair)
                {
                    synchronized (this.usuarios)
                    {
                        this.usuarios.remove (this.conexaoJogadorAtual);
                    }
                    this.conexaoJogadorAtual.adeus();
                    this.grupo.remove(this.conexaoJogadorAtual);
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
                    }
                }
                catch (Exception ignored)
                {}

                return;
            }
        }


    }

    private void passarVez(){
        // Diminui o numero de jogadores, caso um tenha saida
        if (this.numeroDoJogador >= this.grupo.size()-1)
            this.numeroDoJogador = -1;
        // Passa a vez para o proximo jogador
        this.numeroDoJogador++;
        this.conexaoJogadorAtual = this.grupo.get(this.numeroDoJogador);
    }
}
