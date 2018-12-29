import java.io.*;
import java.net.*;

public class InitC{
	public static void main(String[] args) throws UnknownHostException, IOException{
		System.out.print("\033[H\033[2J");  
    	System.out.flush();  
		BidderClient bidderClient = new BidderClient(args[0], Integer.parseInt(args[1]));
		bidderClient.startBidderClient();

	}	
}