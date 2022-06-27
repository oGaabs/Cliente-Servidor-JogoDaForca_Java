package servidor.jogodaforca;

import servidor.ComunicadoDeDesligamento;
import socket.Parceiro;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;

public class AceitadoraDeConexao extends Thread
{
    private final ServerSocket        pedido;
    private final ArrayList<Parceiro> jogadores;
    private ArrayList<Parceiro> grupos;
    private boolean exit = false;
    Parceiro novoJogador;

    public AceitadoraDeConexao (String porta, ArrayList<Parceiro> jogadores) throws Exception
    {
        if (porta==null)
            throw new Exception ("Porta ausente");

        try
        {
            this.pedido = new ServerSocket (Integer.parseInt(porta));
        }
        catch (Exception  erro)
        {
            throw new Exception ("Porta invalida");
        }

        if (jogadores == null)
            throw new Exception ("Usuarios ausentes");

        this.jogadores = jogadores;
        this.grupos = new ArrayList<>(3);
    }

    public synchronized void desligarConexoes(){
        // Encerra todas as conexões e jogos com um
        // comando de desligamento
        ComunicadoDeDesligamento comunicadoDeDesligamento =
                new ComunicadoDeDesligamento();

        for (Parceiro jogador : jogadores) {
            try {
                jogador.receba(comunicadoDeDesligamento);
                jogador.adeus();
            } catch (Exception ignored) {}
        }

        this.grupos = null;
        this.exit = true;
    }

    public void run ()
    {
        while (!exit)
        {
            Socket conexao;
            try
            {
                // Aceita um novo jogador
                conexao = this.pedido.accept();
            }
            catch (Exception erro)
            {
                continue;
            }

            // Cria seu transmissor e receptor
            ObjectOutputStream transmissor;
            try
            {
                transmissor =new ObjectOutputStream(conexao.getOutputStream());
            }
            catch (Exception erro)
            {
                return;
            }

            ObjectInputStream receptor;
            try
            {
                receptor= new ObjectInputStream(conexao.getInputStream());
            }
            catch (Exception erro)
            {
                try
                {
                    transmissor.close();
                }
                catch (Exception ignored)
                {}

                return;
            }

            try
            {
                novoJogador = new Parceiro(conexao,
                                receptor,
                                transmissor);
            }
            catch (Exception ignored)
            {}

            // Adiciona esse jogador a jogadores
            synchronized (this.jogadores)
            {
                this.jogadores.add(novoJogador);
            }
            // Adiciona ele a um grupo de jogo
            this.grupos.add(novoJogador);

            // Caso esse grupo chegue a 3 pessoa, cria uma nova partida e
            // cria um novo grupo para os proximos jogadores
            if (this.grupos.size() == 3){
                criarPartida();
                this.grupos = new ArrayList<>();
            }
        }
    }

    private void criarPartida(){
        SupervisoraDeConexao supervisoraDeConexao=null;
        try
        {
            supervisoraDeConexao = new SupervisoraDeConexao(jogadores, grupos);
        }
        catch (Exception ignored)
        {}
        supervisoraDeConexao.start();
    }
}