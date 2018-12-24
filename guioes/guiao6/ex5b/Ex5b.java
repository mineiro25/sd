import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;


class ServerTHD extends Thread{
	private Socket clientSocket;
	

	public ServerTHD(Socket clientSocket){
		this.clientSocket = clientSocket;
	}

	public void run(){
		try{
			String incoming;

			int incomingsSum = 0;

			int incomingsQty = 0;

			int incomingsAvg;

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

			clientSocket.close();
		}catch(IOException e){}
	}
}

class Server{
	int porto;

	ServerSocket newSocket;

	Server(int porto){
		this.porto = porto;
	}
	
	public void initServer() throws IOException{
		newSocket = new ServerSocket(this.porto);
		
		while(true){
			Socket clientSocket = newSocket.accept();
			ServerTHD serverTHD = new ServerTHD(clientSocket);
			serverTHD.start();
		}

	}
}

class Ex5b{
	public static void main(String[] args) throws IOException{
		Server server = new Server(12345);
		server.initServer();
	}
} 