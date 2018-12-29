import java.io.*;
import java.net.*;

public class BidderClient{
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