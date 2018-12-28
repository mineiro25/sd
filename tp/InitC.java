import java.io.*;
import java.net.*;
//import java.util.*;

class BidderClientListener extends Thread{
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

class BidderClient{
	String ip;

	int port;

	Socket socket;

	BidderClientListener bidderClientListener;

	BufferedReader stdin;

	PrintWriter out;

	BidderClient(String ip, int port) 
								throws UnknownHostException, IOException{
		this.ip = ip;
		this.port = port;
		this.socket = new Socket(this.ip, this.port);
		this.bidderClientListener = new BidderClientListener(this.socket);
		this.stdin = new BufferedReader(new InputStreamReader(System.in));
		this.out = new PrintWriter(this.socket.getOutputStream(), true);
	}

	public void startBidderClient(){
		String incoming;

		bidderClientListener.start();

		try{
			while((incoming = stdin.readLine()) != null)
				out.println(incoming);

			socket.shutdownOutput();

			stdin.close();

			out.close();
		}catch(IOException e){}

	}
}

class InitC{
	public static void main(String[] args) throws UnknownHostException, IOException{
		System.out.print("\033[H\033[2J");  
    	System.out.flush();  
		BidderClient bidderClient = new BidderClient(args[0], Integer.parseInt(args[1]));
		bidderClient.startBidderClient();

	}	
}