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

	private int port;

	Socket bankServerSocket;

	Client(String ip, int port){
		this.ip = ip;
		this.port = port;
	}

	public void clientStart() throws UnknownHostException, IOException{
		String incoming;

		bankServerSocket = new Socket(this.ip, this.port);

		PrintWriter out = new PrintWriter(bankServerSocket
				.getOutputStream(),true);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(bankServerSocket
					.getInputStream()));

		BufferedReader stdin = new BufferedReader(
				new InputStreamReader(System.in));

		while((incoming = stdin.readLine()) != null){
			out.println(incoming);
			System.out.println(in.readLine());
		}

		bankServerSocket.shutdownOutput();

		out.close();

		in.close();

		stdin.close();

	}

}

class Ex1c{

	public static void main(String args[])
								throws UnknownHostException, IOException{
		Client client = new Client("localhost", 12345);
		client.clientStart();
	}

}