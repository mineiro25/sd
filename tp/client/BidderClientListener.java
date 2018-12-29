import java.io.*;
import java.net.*;

public class BidderClientListener extends Thread{
	Socket socket;

	BufferedReader in;

	PrintWriter out;

	BidderClientListener(Socket socket) throws IOException{
		this.socket = socket;
		this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.out = new PrintWriter(this.socket.getOutputStream(), true);
	}

	public void run(){
		String incoming;

		try{
			while((incoming = in.readLine()) != null){
				if(incoming.equals("CLOSEREADLINE")){
					this.out.println("CLOSEREADLINE");	
				}
				else System.out.println(incoming);
			}

			socket.shutdownOutput();

			in.close();
		}catch(IOException e){}
	}
}