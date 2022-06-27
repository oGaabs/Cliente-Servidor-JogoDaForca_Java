package servidor;

import servidor.jogodaforca.AceitadoraDeConexao;
import socket.*;

import java.util.*;

public class Servidor {
    public static String PORTA_PADRAO = "3000";

    public static void main(String[] args) {
        // Foi passado mais de um argumento para o programa
        if (args.length > 1) {
            System.err.println("Uso esperado: java servidor.Servidor [PORTA]\n");
            return;
        }

        String porta = Servidor.PORTA_PADRAO;
        // Se for passado uma porta
        if (args.length == 1)
            porta = args[0];

        // Array com os endereços de todos os clientes conectados
        ArrayList<Parceiro> jogadores = new ArrayList<>();

        // Será a responsavel por aceitar as conexões
        // e redimensiona-las para nosso jogo da forca

        AceitadoraDeConexao aceitadoraDeConexao;
        try {
            aceitadoraDeConexao = new AceitadoraDeConexao(porta, jogadores);
            aceitadoraDeConexao.start();
        } catch (Exception erro) {
            System.err.println("Escolha uma porta apropriada e liberada para uso!\n");
            return;
        }

        // Mantém o programa rodando até que o administrador digite desativar
        // encerrando o programa e todas as conexões/jogos
        while (true) {
            System.out.println("O servidor esta ativo! Para desativa-lo,");
            System.out.println("use o comando \"desativar\"\n");
            System.out.print("> ");

            String comando = null;
            try {
                comando = Teclado.getUmString();
            }
            catch (Exception ignored) {}

            if (comando == null)
                continue;

            if (comando.equalsIgnoreCase("desativar")) {
                // Desliga todos os jogadores
                aceitadoraDeConexao.desligarConexoes();

                // Encerra o programa
                System.out.println("O servidor foi desativado!\n");
                System.exit(0);
            } else
                System.err.println("Comando invalido!\n");
        }
    }
}
