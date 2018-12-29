import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;


public class InitS{
	public static void main(String[] args){
		System.out.print("\033[H\033[2J");  
    	System.out.flush();  
    	System.out.println("\n-------------- Welcome to SERVERUM! --------------\n");
		try{
			AuctionServer auctionServer = new AuctionServer(Integer.parseInt(args[0]));
			auctionServer.startAuctionServer();
		}catch(IOException e){}
	}
}