import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/*Abrimos dois terminais. Num deles, executamos este programa. Noutro, 
escrevemos "telnet localhost 1234". Após escrevermos e corrermos isso,
no terminal do nosso programa, aparecerá uma mensagem a informar que o 
telnet se ligou ao porto que tinhamos dispobilizado para os clientes
se ligarem. Posto isto no terminnal do telnet devemos escrever o que 
quisermos. Assim que enviarmos para o servidor, o servidor informa que 
recebeu e volta a enviar essa mesma mensagem para o telnet.*/
class Server{
	private ServerSocket newSocket;
	
	private int porto;

	public Server(int porto){
		this.porto = porto;
	}

	public void initServer() throws IOException{
		/*para que possamos continuar a ter o servidor mesmo que um
		cliente o abandone, vamos criar aqui um loop infinito; 
		eis o que vai acontecer, por alto:
		são criadas as ligaçoes, os imput's e output's, sao feitas 
		escritas e leituras, o server recebe um EOF e fecha todas as 
		ligacoes com aquele cliente; volta a repetir o mesmo processo
		até que se faça um control^c*/
		while(true){	
			/*criacao de um socket no servidor, com o identificador que está em
			this.porto*/
			newSocket = new ServerSocket(this.porto);
			
			/*assim que um cliente estabelecer ligação com o socket newSocket 
			do servidor, é guardado em clienteSocket um novo porto do cliente
			por onde o servidor poderá, a partir de agora, comunicar com esse
			cliente (o método accept() bloqueia o servidor até que algum 
			cliente estabeleça ligação)*/
			Socket clientSocket = newSocket.accept();

			/*objeto que nos vai permitir enviar informação para o socket do
			cliente, entre outras coisas, fazendo uso dos seus métodos*/
			PrintWriter out = new PrintWriter(clientSocket
													.getOutputStream(),true);

			/*objeto que nos vai permitir receber informação a partir do socket 
			do cliente, entre outras coisas, fazendo uso dos seus métodos*/
			BufferedReader in = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));

			/*String onde vai ser colocado o que é lido do socket do cliente*/
			String incoming;

			/*a condicao do while lê uma string do socket do cliente e guarda 
			em 'incoming', se  for lido um EOF, quer dizer que o cliente 
			fechou a conexão*/
			while((incoming = in.readLine()) != null){
				//escrita no socket do cliente
				out.println(incoming);
			}

			/*fecho dos "descritores" que se abriram para fazer as 
			comunicacões*/
			in.close();
			out.close();
			newSocket.close();
			clientSocket.close();
		}
	}
}

class Ex1{
	public static void main(String[] args) throws IOException{
		Server server = new Server(12345);
		server.initServer();
	}
} 