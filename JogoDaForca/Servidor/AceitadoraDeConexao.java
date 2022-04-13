import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;

public class AceitadoraDeConexao extends Thread
{
    private ServerSocket        pedido;
    private ArrayList<Parceiro> usuarios;
    private ArrayList<Parceiro> grupos;
    Parceiro usuario;

    public AceitadoraDeConexao (String porta, ArrayList<Parceiro> usuarios) throws Exception
    {
        if (porta==null)
            throw new Exception ("Porta ausente");

        try
        {
            this.pedido =
                new ServerSocket (Integer.parseInt(porta));
        }
        catch (Exception  erro)
        {
            throw new Exception ("Porta invalida");
        }

        if (usuarios==null)
            throw new Exception ("Usuarios ausentes");

        this.usuarios = usuarios;
        this.grupos = new ArrayList<Parceiro>();
    }

    public void run ()
    {
        for(;;)
        {
            Socket conexao=null;
            try
            {
                conexao = this.pedido.accept();
            }
            catch (Exception erro)
            {
                continue;
            }

            ObjectOutputStream transmissor;
            try
            {
                transmissor =
                new ObjectOutputStream(
                    conexao.getOutputStream());
            }
            catch (Exception erro)
            {
                return;
            }

            ObjectInputStream receptor=null;
            try
            {
                receptor=
                new ObjectInputStream(
                    conexao.getInputStream());
            }
            catch (Exception err0)
            {
                try
                {
                    transmissor.close();
                }
                catch (Exception falha)
                {} // so tentando fechar antes de acabar a thread

                return;
            }

            try
            {
                usuario = new Parceiro (conexao,
                                receptor,
                                transmissor);
            }
            catch (Exception erro)
            {} // sei que passei os parametros corretos

            synchronized (this.usuarios)
            {
                this.usuarios.add(this.usuario);
            }
            synchronized (this.grupos)
            {
                this.grupos.add(this.usuario);

                if (this.grupos.size() == 3)
                {
                    SupervisoraDeConexao supervisoraDeConexao=null;
                    try
                    {
                        supervisoraDeConexao =
                        new SupervisoraDeConexao (usuarios, grupos);
                    }
                    catch (Exception erro)
                    {} // sei que passei parametros corretos para o construtor
                    supervisoraDeConexao.start();
                    this.grupos = new ArrayList<Parceiro>();
                }
            }
        }
    }
}