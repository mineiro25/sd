import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class Server{
	String name;

	double nominalPrice;

	String bidderName;

	double baseAuctionPrice; 

	double auctionPrice; 

	int auctionTime;//se adiconarmos isto, alterar os construtores, e o clone 

	//nome do bidder, e a sua socket 
	HashMap<String,Socket> biddingers; //fazer o clone

	ReentrantLock lock;

	Condition inConnectionDemand;

	Condition inConnectionSpot;

	Condition countDownInit;

	Condition startBidders;

	Server(String name, double nominalPrice, double baseAuctionPrice){
		this.name = name;
		this.nominalPrice = nominalPrice;
		this.bidderName = null;
		this.baseAuctionPrice = baseAuctionPrice;
		this.auctionPrice = 0;
		this.auctionTime = 0;
		this.biddingers = new HashMap<String, Socket>();
		this.lock = new ReentrantLock();
		this.inConnectionDemand = this.lock.newCondition();
		this.inConnectionSpot = this.lock.newCondition();
		this.countDownInit = this.lock.newCondition();
		this.startBidders = this.lock.newCondition();
	}

	Server(String name, double nominalPrice, String bidderName, double baseAuctionPrice, double auctionPrice, int auctionTime, HashMap<String,Socket> biddingers){
		this.name = name;
		this.nominalPrice = nominalPrice;
		this.bidderName = bidderName;
		this.baseAuctionPrice = baseAuctionPrice;
		this.auctionPrice = auctionPrice;
		this.auctionTime = auctionTime;
		this.biddingers = biddingers;
		this.lock = new ReentrantLock();
		this.inConnectionDemand = this.lock.newCondition();
		this.inConnectionSpot = this.lock.newCondition();
		this.countDownInit = this.lock.newCondition();
		this.startBidders = this.lock.newCondition();
	}

	public String getName(){
		this.lock.lock();
		String result = this.name;
		this.lock.unlock();
		return result;
	}

	public double getNominalPrice(){
		this.lock.lock();
		double result = this.nominalPrice;
		this.lock.unlock();
		return result;
	}

	public double getBaseAuctionPrice(){
		this.lock.lock();
		double result = this.baseAuctionPrice;
		this.lock.unlock();
		return result;
	}

	public double getAuctionPrice(){
		this.lock.lock();
		double result = this.auctionPrice;
		this.lock.unlock();
		return result;
	}

	public String getBidderName(){
		this.lock.lock();
		String result = this.bidderName;
		this.lock.unlock();
		return result;
	}

	public void setBidderName(String bidderName){
		this.lock.lock();
		this.bidderName = bidderName;
		this.lock.unlock();
	}

	public void setAuctionTime(int auctionTime){
		this.lock.lock();
		this.auctionTime = auctionTime;
		this.lock.unlock();
	}

	public void setAuctionPrice(double auctionPrice){
		this.lock.lock();
		this.auctionPrice = auctionPrice;
		this.lock.unlock();
	}

	public void updateBiddingers(String username, Socket socket){
		this.lock.lock();
		this.biddingers.put(username, socket);
		this.lock.unlock();
	}

	public Server cloneServer(){
		this.lock.lock();
		Server server = new Server(this.name, this.nominalPrice, this.bidderName, this.baseAuctionPrice, this.auctionPrice, this.auctionTime, this.cloneBiddingers()); 
		this.lock.unlock();
		return server;
	}

	public HashMap<String,Socket> cloneBiddingers(){
		HashMap<String,Socket> biddingersCopy = new HashMap<String,Socket>();
		this.lock.lock();
		for(Map.Entry<String,Socket>  entry : this.biddingers.entrySet())
			biddingersCopy.put(entry.getKey(), entry.getValue());
		this.lock.unlock();
		return biddingersCopy; 
	}

	public boolean isConnected(){
		this.lock.lock();
		if(this.bidderName == null){
			this.lock.unlock();
			return false;
		}
		else{ 
			this.lock.unlock();
			return true;
		}
	}

	public boolean isBidded(){
		this.lock.lock();
		if(this.auctionPrice == 0){
			this.lock.unlock();
			return false;
		}
		this.lock.unlock();
		return true;

	}

	public void waitTimeInit(){
		this.lock.lock();
		try{	
			this.countDownInit.await();
		}catch(InterruptedException e){}
		this.lock.unlock();	
	}

	public void timeCanInit(){
		this.lock.lock();
		this.countDownInit.signal();
		this.lock.unlock();	
	}

	public void awaitStartBidders(){
		this.lock.lock();
		try{
			this.startBidders.await();
		}catch(InterruptedException e){}
		this.lock.unlock();
	}

	public void signalAllStartBidders(){
		this.lock.lock();
		this.startBidders.signalAll();
		this.lock.unlock();
	}

	public void lockServer(){
		this.lock.lock();
	}

	public void unlockServer(){
		this.lock.unlock();
	}

	public void disestablishConnectionDemand(){
		this.lock.lock();
		this.bidderName = null;
		this.inConnectionDemand.signal();
		this.lock.unlock();
	}

	public void disestablishConnectionSpot(){
		this.lock.lock();
		this.bidderName = null;
		this.auctionPrice = 0;
		this.auctionTime = 0;
		this.biddingers.clear();
		this.inConnectionSpot.signal();
		this.lock.unlock();
	}

	public String establishConnectionDemand(String username){
		this.lock.lock();
		if(this.bidderName != null){
			try{
				this.inConnectionDemand.await();
			}catch(InterruptedException e){}
		}
		this.bidderName = username;
		String temporary = this.name;
		this.lock.unlock();
		return temporary;
	}

	public String establishConnectionSpot(String username, Socket socket){
		this.lock.lock();
		if(this.bidderName != null){
			try{
				System.out.println("antes do await");
				PrintWriter temporaryOut = new PrintWriter(socket.getOutputStream(), true);
				System.out.println("CHEGUEI AQUI");
				temporaryOut.println("CLOSEREADLINE");
				this.inConnectionSpot.await();
			}catch(IOException e){}catch(InterruptedException e){}
		}
		this.bidderName = username;
		String temporary = this.name;
		this.lock.unlock();
		return temporary;
	}

	public void initCountdown(){
		this.lock.lock();
		CounterDown counterDown = new CounterDown(this, 15);
		counterDown.start();
		this.waitTimeInit();
		this.lock.unlock();
	}

	public int getAuctionTime(){
		this.lock.lock();
		int result = this.auctionTime;
		this.lock.unlock();
		return result;
	}

	public double usingServer(BufferedReader in, PrintWriter out){
		double result;

		this.lock.lock();
		out.println("You are now connected to server " + this.name + ".");
		System.out.println(this.bidderName + " just connected to server " + this.name + ".");
		out.println("When you no longer want to use this server, type \"abandon\" to leave it.");
		this.lock.unlock();
		
		double start = (double) System.currentTimeMillis();
		try{
			while(!((in.readLine().toUpperCase()).matches("ABANDON|CLOSEREADLINE")))
				out.println("You typed something else. Type \"abandon\" to leave the server.");
		}catch(IOException e){}
		double finish = (double) System.currentTimeMillis();

		this.lock.lock();
		
		result = ((finish - start)/1000);
		result = Math.round(result*100.0) / 100.0;
		
		out.println("You are now disconnected from server " + this.name + "after use it for " + result + " hours.");
		System.out.println(this.bidderName + " just disconnected from server " + this.name + ".");

		if(this.auctionPrice == 0)
			result = (this.nominalPrice*((finish - start)/1000));
		else 
			result = (this.auctionPrice*((finish - start)/1000));
		
		this.lock.unlock();

		return result;
	}

	public void printCounterDown(int auctionTimeCopy){
		HashMap<String,Socket> biddingersCopy;
		this.lock.lock();
		biddingersCopy = this.cloneBiddingers();
		this.lock.unlock();
		
		try{
			for(Map.Entry<String,Socket> entry :  biddingersCopy.entrySet()){
					PrintWriter temporaryOut = new PrintWriter(entry.getValue().getOutputStream(), true);
					if((auctionTimeCopy % 5) == 0 && auctionTimeCopy > 9){
						temporaryOut.println("                                                  Clock: " + auctionTimeCopy + "s left");
					}
					else if(auctionTimeCopy < 6){
						temporaryOut.println("                                                  Clock: 0" + auctionTimeCopy + "s left");
					}
			}
		}catch(IOException e){}
	}

	public void broadcastBiddingers(String message){
		HashMap<String,Socket> biddingersCopy;
		this.lock.lock();
		biddingersCopy = this.cloneBiddingers();
		this.lock.unlock();

		try{
			for(Map.Entry<String,Socket> entry :  biddingersCopy.entrySet()){
					PrintWriter temporaryOut = new PrintWriter(entry.getValue().getOutputStream(), true);
					temporaryOut.println(message);
			}
		}catch(IOException e){}
	}

}