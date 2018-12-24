import java.io.*;
import java.net.*;
import java.util.*;

class ClientListener extends Thread{
	Socket clientSocket;

	ClientListener(Socket clientSocket){
		this.clientSocket = clientSocket; 
	}

	public void run(){
		String incoming;

		try{
			BufferedReader in = new BufferedReader(
						new InputStreamReader(this
											.clientSocket.getInputStream()));
		
		while((incoming = in.readLine()) != null){
			System.out.println(incoming);
		}


		in.close();
		this.clientSocket.shutdownOutput();
		}catch(IOException e){}
	}	
}

class Client{
	Socket clientSocket;

	ClientListener ClientListener;

	String ip;

	int port;

	Client(String ip, int port){
		this.ip = ip;
		this.port = port;
	}

	public void startClient(){
		String incoming;

		try{
			Socket clientSocket = new Socket(this.ip, this.port);

			ClientListener clientListener = new ClientListener(clientSocket);

			clientListener.start();

			PrintWriter out = new PrintWriter(clientSocket
												.getOutputStream(),true);

			BufferedReader stdin = new BufferedReader(
										new InputStreamReader(System.in));
			
			while((incoming = stdin.readLine()) != null)
				out.println(incoming);

			clientSocket.shutdownOutput();

			out.close();
			
			stdin.close();
			
			clientSocket.close();

		}catch(UnknownHostException e){}catch(IOException e){}
	}
}

class Ex2c{
	public static void main(String[] args){
		Client client = new Client(args[0], 12345);
		client.startClient();
	} 
}