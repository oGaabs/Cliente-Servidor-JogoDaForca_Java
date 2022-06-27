package cliente;

import java.io.*;
import java.net.Socket;

import cliente.jogodaforca.JogoDaForcaCliente;
import socket.Parceiro;

public class Cliente {
    public static final String HOST_PADRAO = "localhost";
    public static final int PORTA_PADRAO = 3000;

    public static void main(String[] args) {
        // Foi passado mais de dois argumentos para o programa
        if (args.length > 2) {
            System.err.println("Uso esperado: java Cliente [HOST [PORTA]]");
            Teclado.getUmString();
            return;
        }

        // Estabelece uma conexão com o servidor via endpoint
        Socket conexao;
        try {
            String host = Cliente.HOST_PADRAO;
            int porta = Cliente.PORTA_PADRAO;

            // Se for passado um host
            if (args.length > 0) host = args[0];

            // Se for passado uma porta
            if (args.length == 2) porta = Integer.parseInt(args[1]);

            conexao = new Socket(host, porta);
        } catch (Exception erro) {
            System.err.println("Indique o servidor e a porta corretos!");
            Teclado.getUmString();
            return;
        }

        // Cria dois objetos para receber e enviar mensagens(streams)
        ObjectOutputStream transmissor;
        ObjectInputStream receptor;
        try {
            transmissor = new ObjectOutputStream(conexao.getOutputStream());
            receptor = new ObjectInputStream(conexao.getInputStream());
        }
        catch (Exception erro) {
            System.err.println("Indique o servidor e a porta corretos!");
            try {
                conexao.close();
            } catch (Exception ignored)
            {}
            Teclado.getUmString();
            return;
        }

        // Instancia um parceiro que controlara as conexões e mensagens
        Parceiro servidor;
        try {
            servidor = new Parceiro(conexao, receptor, transmissor);
        } catch (Exception erro) {
            System.err.println("Indique o servidor e a porta corretos!");
            Teclado.getUmString();
            return;
        }

        // Inicializa uma tratadora para parar o cliente, caso o servidor seja desligado
        TratadoraDeComunicadoDeDesligamento tratadoraDeComunicadoDeDesligamento;
        try {
            tratadoraDeComunicadoDeDesligamento = new TratadoraDeComunicadoDeDesligamento(servidor);
            tratadoraDeComunicadoDeDesligamento.start();
        }
        catch (Exception ignored) {}

        // Inicializa o Jogo da Força do lado do Cliente
        JogoDaForcaCliente jogoDaForcaCliente;
        try {
            jogoDaForcaCliente = new JogoDaForcaCliente(servidor);
        } catch (Exception e) {
            System.err.println("Indique o servidor e a porta corretos!");
            Teclado.getUmString();
            return;
        }

        jogoDaForcaCliente.inicializar();

        System.out.println("Obrigado por usar este programa!");
        System.exit(0);
    }
}

