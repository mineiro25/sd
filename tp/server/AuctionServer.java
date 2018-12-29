import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class AuctionServer{
	ServerSocket connectionSocket;

	DataBase data;

	int port;

	List<AuctionServerTHD> threads;

	AuctionServer(int port) throws IOException{
		this.port = port;
		this.data = new DataBase();
		this.connectionSocket = new ServerSocket(this.port);
		this.threads = new ArrayList<AuctionServerTHD>();
	}

	public void startAuctionServer(){
		this.data.initDataBase();

		try{	
			while(true){
				Socket socket = connectionSocket.accept();
				AuctionServerTHD auctionServerTHD = new AuctionServerTHD(
												this.data, socket);
				threads.add(auctionServerTHD);
				auctionServerTHD.start();
			}

		}catch(IOException e){}

		try{
			for(AuctionServerTHD thread : threads)
				thread.join();
		}catch(InterruptedException e){}
	}

}
