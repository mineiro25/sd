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
		String incoming;

		try{
			Socket serverSocket = new Socket(this.ip, this.porto);

			PrintWriter out = new PrintWriter(serverSocket
												.getOutputStream(),true);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(serverSocket.getInputStream()));

			BufferedReader stdin = new BufferedReader(
										new InputStreamReader(System.in));
			
			while((incoming = stdin.readLine()) != null){
				out.println(incoming);
				System.out.println(in.readLine());
			}

			serverSocket.shutdownOutput();

			System.out.println(in.readLine());

			out.close();

			in.close();
			
			stdin.close();
			
			serverSocket.close();
		}catch(UnknownHostException e){}catch(IOException e){}
	}
}

class Ex4{
	public static void main(String[] args){
		Client client = new Client("localhost", 12345);
		client.initClient();
	}
}
