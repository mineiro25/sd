import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

class Client{
	private String ip;
	
	private int porto;
	
	private Socket serverSocket;

	public Client(String ip, int porto){
		this.ip = ip;
		this.porto = porto;
	}

	public void initClient(){
		try{
			/*o cliente conecta-se ao servidor que tem o endereço de ip
			'this.ip' no porto 'this.porto'; depois da conexão ser 
			estabelecida, é colocado em serverSocket um novo socket que 
			será o local do servidor para/de onde o cliente deverá 
			enviar/receber informação!*/
			Socket serverSocket = new Socket(this.ip, this.porto);

			/*para escrita de informação destinada ao socket do servidor*/
			PrintWriter out = new PrintWriter(serverSocket
												.getOutputStream(),true);

			/*para leitura de informação proveniente do socket do servidor*/
			BufferedReader in = new BufferedReader(
					new InputStreamReader(serverSocket.getInputStream()));

			String incoming;

			/*para leitura de informação do standard imput*/
			BufferedReader stdin = new BufferedReader(
										new InputStreamReader(System.in));

			/*dentro da condição do while é lida a mensagem que escrevemos
			no terminal e guardada em 'incoming'; se enviarmos um EOF 
			(control^d) o cliente termina*/
			while((incoming = stdin.readLine()) != null){
				//envio do que foi lido para o servidor
				out.println(incoming);
				/*leitura e impressão do que foi enviado pelo servidor; 
				este readLine() bloqueia a execução do programa até ter 
				alguma coisa para ler*/
				System.out.println(in.readLine());
			}

			/*fecho dos "descritores" que se abriram para fazer as 
			comunicacões*/
			in.close();
			out.close();
			stdin.close();
			serverSocket.close();
		}catch(UnknownHostException e){}catch(IOException e){}
	}
}

class Ex2{
	public static void main(String[] args){
		Client client = new Client("localhost", 12345);
		client.initClient();
	}
}
