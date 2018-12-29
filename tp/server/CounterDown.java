import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class CounterDown extends Thread{
	Server server;

	int initialTime;

	CounterDown(Server server, int initialTime){
		this.server = server;
		this.initialTime = initialTime;
	}

	public void run(){
		this.server.setAuctionTime(initialTime);
		this.server.timeCanInit();
		while(this.initialTime > 0){
			this.server.printCounterDown(this.initialTime);	
			this.server.setAuctionTime(--this.initialTime);
			try{
				sleep(1000);
			}catch(InterruptedException e){}
		}
		this.server.printCounterDown(this.initialTime);
		this.server.broadcastBiddingers("CLOSEREADLINE");
		this.server.setAuctionTime(--this.initialTime);
	}
}