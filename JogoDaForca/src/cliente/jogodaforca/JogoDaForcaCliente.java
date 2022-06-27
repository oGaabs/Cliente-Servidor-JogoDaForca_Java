package cliente.jogodaforca;

import cliente.Teclado;
import socket.comunicado.resposta.*;
import socket.comunicado.requisicao.*;
import cliente.jogodaforca.logica.*;
import socket.*;

public class JogoDaForcaCliente {
    private final Parceiro servidor;
    private boolean isGanhador = false;
    private boolean isPerdedor = false;

    public JogoDaForcaCliente(Parceiro servidor) throws Exception {
        if (servidor == null)
            throw new Exception ("Servidor não fornecido");

        this.servidor = servidor;
    }

    private boolean isComunicadoDeVitoriaInesperada(Comunicado comunicado){
        if (comunicado instanceof ComunicadoDeGanhador) {
            this.isGanhador = true;
            return true;
        }
        if (comunicado instanceof ComunicadoDePerdedor) {
            this.isPerdedor = true;
            return true;
        }

        return false;
    }


    public void inicializar() {
        // Controla as letras ja digitadas pelo usuario
        ControladorDeLetrasJaDigitadas controlDigitadas = new ControladorDeLetrasJaDigitadas();

        System.out.println("Você está conectado ao servidor.");
        System.out.println("Esperando o seu grupo completar três jogadores...");

        Comunicado comunicado;

        try {

            // Espera o servidor indicar que o grupo foi completado
            do {
                comunicado = servidor.espie();
            }
            while (!(comunicado instanceof GrupoFoiCompletado));

            System.out.println("Grupo formado!\n");

            while (true) {
                System.out.println("Espere sua vez de jogar...");

                // Espera o servidor enviar uma palavra escondida ou
                // um aviso que o jogador ganhou ou perdeu
                comunicado = null;

                while (true) {
                    try {
                        servidor.receba(new PedidoDeTracinhos());
                        comunicado = servidor.envie();
                    }
                    catch (Exception ignored) {}

                    if (comunicado instanceof ComunicadoDeTracinhos) break;
                    if (isComunicadoDeVitoriaInesperada(comunicado)) break;
                }

                // Outros jogadores tentaram a palavra e erraram, logo somente você sobrou e Ganhou
                if (isGanhador) {
                    System.out.println("Outros jogadores erraram a palavra e perderam o jogo, assim você GANHOU...");
                    break;
                }
                // Outro jogador ganhou, logo o jogo acaba e você perde
                if (isPerdedor)
                    break;

                // Mostra a palavra escondida, com as letras ja preenchidas
                System.out.println("Agora é sua vez\n\n" + comunicado);

                System.out.println("Letras digitadas: " + controlDigitadas + "\n");
                System.out.println("Escolha uma jogada " +
                        "\n'P' Para digitar a palavra" +
                        "\n'L' Para tentar uma letra");

                // Recebe a jogada do usuario
                char jogada = ' ';
                while ("PL".indexOf(jogada) == -1) {
                    jogada = Character.toUpperCase(Teclado.getUmChar());

                    if ("PL".indexOf(jogada) == -1)
                        System.err.println("Jogada invalida!\n");
                }

                // Tentativa de uma palavra
                // Se acertar, ganha o jogo
                // Se errar, perde o jogo
                System.out.println();
                if (jogada == 'P') {
                    System.out.println("Digite a palavra, você tem 10 segundos:");
                    String palavra = Teclado.getUmString().toUpperCase();

                    // Envia a palavra ao servidor
                    System.out.println("\nVerificando se a palavra está correta!");
                    servidor.receba(new PedidoDePalavra(palavra));

                    // Espera o servidor enviar se a palavra está correta ou não
                    while (true) {
                        comunicado = servidor.envie();

                        if (comunicado instanceof AcabouOTempo) {
                            System.out.println("O seu tempo acabou! PERDEU A VEZ!");
                            System.out.println("Espere até ser sua vez, de novo.");
                        }

                        if (comunicado instanceof ComunicadoDeGanhador) {
                            System.out.println("Acertou a palavra e Ganhou o Jogo.");
                            isGanhador = true;
                            break;
                        }
                        if (comunicado instanceof ComunicadoDePerdedor) {
                            System.out.println("Você errou a palavra e portanto perdeu o jogo.\n");
                            isPerdedor = true;
                            break;
                        }
                    }
                }
                // Tentativa de letra
                // Se acertar, continua ou ganha o jogo por completar a palavra
                // Se errar, perde a vez
                else {
                    char letra = ' ';
                    while (letra == ' ') {
                        System.out.println("Digite uma letra, você tem 10 segundos:");
                        letra = Character.toUpperCase(Teclado.getUmChar());

                        if (controlDigitadas.isJaDigitada(letra) || letra == ' ') {
                            System.out.println("Letra ja digitada.");
                            letra = ' ';
                        }
                    }
                    controlDigitadas.registre(letra);

                    // Envia a letra ao servidor
                    System.out.println("Verificando se acertou");
                    servidor.receba(new PedidoDeLetra(letra));

                    // Espera o servidor enviar se a letra está correta ou não
                    while (true) {
                        comunicado = servidor.envie();

                        // Verifica se outro jogador acertou a palavra
                        if (comunicado instanceof ComunicadoDePerdedor){
                            isPerdedor = true;
                            break;
                        }

                        if (comunicado instanceof AcabouOTempo) {
                            System.out.println("O seu tempo acabou! PERDEU A VEZ!");
                            System.out.println("Espere até ser sua vez, de novo.");
                            break;
                        }
                        // Verifica se acertou/errou a letra
                        if (comunicado instanceof Errou) {
                            System.out.println("Você errou a letra.");
                            break;
                        }
                        if (comunicado instanceof Acertou) {
                            System.out.println("Acertou a letra.");
                            break;
                        }
                        // Verifica se acertou a ultima letra que faltava para completar a palavra
                        if (comunicado instanceof ComunicadoDeGanhador) {
                            System.out.println("Acertou a letra e ganhou parabéns.");
                            isGanhador = true;
                            break;
                        }
                    }


                }
                // Em caso da tentativa de letra  'L':
                // Ganha a partida por acertar a ultima letra ou
                // Perde por outro jogador ter acertado a palavra

                // Em caso de tentativa de palavra 'P':
                // Ganha se tiver acertado a palavra ou
                // Perde por ter errado a palavra
                if (isGanhador || isPerdedor)
                    break;
            }

            // Verifica se o jogador ganhou ou perdeu após o fim do jogo
            if (isGanhador){
                System.out.println("PARABÉNS VOCÊ GANHOU.\n");
            }
            else {
                System.out.println("PARABÉNS VOCÊ PERDEU.\n" + comunicado);
            }
        } catch (Exception erro) {
            System.err.println("Erro de comunicacao com o servidor;"+
                    "\nTente novamente!"+
                    "\nCaso o erro persista, termine o programa"+
                    "\ne volte a tentar mais tarde!"+
                    "\nEnter para fechar.");
            Teclado.getUmString();
        }

        // Finaliza o jogo e pede para o servidor o retira-lo da fila
        try {
            servidor.receba(new PedidoParaSair());
            servidor.adeus();
        }
        catch (Exception ignored) {}
    }
}
