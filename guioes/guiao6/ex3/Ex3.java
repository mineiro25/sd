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

		while(true){	

			String incoming;
			
			int incomingsSum = 0;

			int incomingsQty = 0;

			int incomingsAvg;

			newSocket = new ServerSocket(this.porto);
			
			Socket clientSocket = newSocket.accept();

			PrintWriter out = new PrintWriter(clientSocket
													.getOutputStream(),true);

			BufferedReader in = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));

			while((incoming = in.readLine()) != null){
				System.out.println(incoming);
				incomingsQty++;
				incomingsSum += Integer.parseInt(incoming);
				out.println(incomingsSum);
			}

			incomingsAvg = incomingsSum/incomingsQty;

			out.println("average: "+ incomingsAvg);

			in.close();

			out.close();

			newSocket.close();

			clientSocket.close();
		}
	}
}

class Ex3{
	public static void main(String[] args) throws IOException{
		Server server = new Server(12345);
		server.initServer();
	}
} 

/*
Dúvidas, por que razão nao podemos por o "in.close()" antes do 
out.println()? Se pusermos vemos que a unica coisa que o cliente lê depois 
de detetar EOF, é um NULL, mas acho que não é suposto porque 
*/