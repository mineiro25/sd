import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class DataBase{
	//bidder's name, bidder
	HashMap<String, Bidder> bidders;

	//bidder's name, bidder's socket
	HashMap<String, Socket> connectedBidders;

	//server's name, server 
	HashMap<String, Server> servers;

	ReentrantLock lockBidders;

	ReentrantLock lockConnectedBidders;

	ReentrantLock lockServers;
	
	DataBase(){
		this.bidders = new HashMap<String, Bidder>();
		this.connectedBidders = new HashMap<String, Socket>();
		this.servers = new HashMap<String, Server>();
		this.lockBidders = new ReentrantLock();
		this.lockConnectedBidders = new ReentrantLock();
		this.lockServers = new ReentrantLock();
	}

	public boolean isBiddersKey(String username){
		this.lockBidders.lock();
		boolean result = this.bidders.containsKey(username);
		this.lockBidders.unlock();
		return result;
	}

	public boolean isConnectedBidderKey(String username){
		this.lockConnectedBidders.lock();
		boolean result = this.connectedBidders.containsKey(username);
		this.lockConnectedBidders.unlock();
		return result;
	}

	public boolean isServersKey(String servername){
		this.lockServers.lock();
		boolean result = this.servers.containsKey(servername);
		this.lockServers.unlock();
		return result;
	}

	public boolean isServersServerConnected(String servername){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		return server.isConnected();
	}

	public boolean isServersServerBidded(String servername){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		return server.isBidded();
	}

	public void serversServerInitCountdown(String servername){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		server.initCountdown();
	}

	public boolean isBidderPassword(String username, String password){
		this.lockBidders.lock();
		Bidder bidder = this.bidders.get(username);
		this.lockBidders.unlock();
		return bidder.getPassword().equals(password);
	}

	public void setBidders(String username, Bidder bidder){
		this.lockBidders.lock();
		this.bidders.put(username, bidder);
		this.lockBidders.unlock();
	}

	public void setConnectedBidders(String username, Socket socket){
		this.lockConnectedBidders.lock();
		this.connectedBidders.put(username, socket);
		this.lockConnectedBidders.unlock();
	}

	public void removeConnectedBidders(String username){
		this.lockBidders.lock();
		this.connectedBidders.remove(username);
		this.lockBidders.unlock();
	}

	public HashMap<String,Server> cloneServers(){
		HashMap<String,Server> serversCopy = new HashMap<String,Server>();
		for(Map.Entry<String,Server>  entry : this.servers.entrySet())
			serversCopy.put(entry.getKey(), entry.getValue().cloneServer());
		return serversCopy; 
	}

	public HashMap<String,Socket> cloneConnectedBidders(){
		HashMap<String,Socket> connectedBiddersCopy = new HashMap<String,Socket>();
		//CUIDADO, ESTAMOS AQUI A FAZER UM SHALLOW CLONE DA SOCKET!
		for(Map.Entry<String,Socket>  entry : this.connectedBidders.entrySet())
			connectedBiddersCopy.put(entry.getKey(), entry.getValue());
		return connectedBiddersCopy;
	}

	public void initDataBase(){
		//aqui talvez precisemos de aplicar mais locks pq na mesma funcao mexemos com vários maps, acho eu
		this.lockServers.lock();
		this.servers.put("t3.micro", new Server("t3.micro", 10, 1));
		this.servers.put("r4.great", new Server("r4.great", 15, 2));
		this.servers.put("4t.large", new Server("4t.large", 20, 2));
		this.servers.put("13.medium", new Server("13.medium", 15, 2));
		this.servers.put("pot.large", new Server("pot.large", 7, 5));
		this.servers.put("ad.large", new Server("ad.large", 13, 5));
		this.servers.put("sd.big", new Server("sd.big", 15, 2));
		this.servers.put("dss.large", new Server("dss.large", 15, 2.4));
		this.servers.put("bd.small", new Server("bd.small", 15, 1));
		this.servers.put("md.large", new Server("md.large", 14, 2.4));
		this.bidders.put("ricardo", new Bidder("ricardo", "12345678"));
		this.bidders.put("guest", new Bidder("guest", "123"));
		this.lockServers.unlock();
	}

	public double getBidderDebt(String username){
		this.lockBidders.lock();
		Bidder bidder = this.bidders.get(username);
		this.lockBidders.unlock();
		return bidder.getDebt();
	}

	public Bidder getBiddersBidder(String username){
		this.lockBidders.lock();
		Bidder bidder = this.bidders.get(username);
		this.lockBidders.unlock();
		return bidder;
	}

	public void setServersServer(String username){
		this.lockServers.lock();
		Server server = this.servers.get(username);
		this.lockServers.unlock();
		server.setBidderName(username);
	}

	public void setServersServerAuctionPrice(String servername, double auctionPrice){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		server.setAuctionPrice(auctionPrice);
	}

	public void setServersServerAuctionTime(String servername, int auctionTime){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		server.setAuctionTime(auctionTime);
	}

	public void updateServersServerBiddingers(String servername, String username, Socket socket){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		server.updateBiddingers(username, socket);
	}
	
	public double getServersServerNominalPrice(String servername){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		return server.getNominalPrice();
	}

	public int getServersServerAuctionTime(String servername){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		return server.getAuctionTime();
	}

	public double getServersServerBaseAuctionPrice(String servername){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		return server.getBaseAuctionPrice();
	}

	public double getServersServerAuctionPrice(String servername){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		return server.getAuctionPrice();
	}

	public void serverDisestablishConnectionDemand(String username){
		this.lockServers.lock();
		Server server = this.servers.get(username);
		this.lockServers.unlock();
		server.disestablishConnectionDemand();
	}

	public void serverDisestablishConnectionSpot(String username){
		this.lockServers.lock();
		Server server = this.servers.get(username);
		this.lockServers.unlock();
		server.disestablishConnectionSpot();
	}

	public String serverEstablishConnectionDemand(String username, String servername){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		return server.establishConnectionDemand(username);
	}

	public String serverEstablishConnectionSpot(String username, String servername){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		this.lockConnectedBidders.lock();
		Socket socket = this.connectedBidders.get(server.getBidderName());
		this.lockConnectedBidders.unlock();
		return server.establishConnectionSpot(username, socket);
	}

	public double serverUsingServer(String username, BufferedReader in, PrintWriter out){
		this.lockServers.lock();
		Server server = this.servers.get(username); 
		this.lockServers.unlock();
		return server.usingServer(in, out);
	}

	public void serversServerWaitTimeInit(String servername){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		server.waitTimeInit();
	}

	public void printServers(PrintWriter out){
		int i = 0;
		String bool;
		HashMap<String, Server> serversCopy;

		this.lockServers.lock();
		serversCopy = this.cloneServers();
		this.lockServers.unlock();

		out.println("\nAvailable servers:");
		for(Server server : serversCopy.values()){
			if((server.getBidderName()) == null){	
				out.println("-> Name: " + server.getName() + "    Price: " + server.getNominalPrice() + "    Base price: " + server.getBaseAuctionPrice());
				i++;
			}
		}
		if(i == 0){ out.println("-> There are no free servers.");}

		i = 0;
		out.println("\nOccupied servers:");
		for(Server server :  serversCopy.values()){
			if((server.getBidderName()) != null){
				if(server.getAuctionPrice() == 0) 
					bool = "no"; 
				else 
					bool = "yes";	
				out.println("-> Name: " + server.getName() + "    Price: " + server.getNominalPrice() + "    Base price: " + server.getBaseAuctionPrice() + "    Bidded: " + bool);
				i++;
			}
		}
		if(i == 0){ out.println("-> There are no occupied servers.");}
	}

	public void biddersBidderSumDebt(String username, double debt){
		this.lockBidders.lock();
		Bidder bidder = this.bidders.get(username);
		this.lockBidders.unlock();
		bidder.sumDebt(debt);
	}

	public void broadcast(String username, String message){
		HashMap<String,Socket> connectedBiddersCopy;
		this.lockConnectedBidders.lock();
		connectedBiddersCopy = this.cloneConnectedBidders();
		this.lockConnectedBidders.unlock();

		try{
			for(Map.Entry<String,Socket> entry :  connectedBiddersCopy.entrySet()){
				if(!(username.equals(entry.getKey()))){	
					PrintWriter temporaryOut = new PrintWriter(entry.getValue().getOutputStream(), true);
					temporaryOut.println(username + message);
					//temporaryOut.close(); //este close está a fazer com que o meu prog nao funcione, porque ????
				}
			}
		}catch(IOException e){}
	}

	public void broadcastServersServerBiddingers(String servername, String message){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		server.broadcastBiddingers(message);
	}

	public void lockData(){
		this.lockServers.lock();
		this.lockBidders.lock();
		this.lockConnectedBidders.lock();
	}

	public void unlockData(){
		this.lockConnectedBidders.unlock();
		this.lockBidders.unlock();
		this.lockServers.unlock();
	}

	public void lockServersServer(String servername){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		server.lockServer();
	}

	public void unlockServersServer(String servername){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		server.unlockServer();
	}

	public void awaitServersServerStartBidders(String servername){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		server.awaitStartBidders();
	}

	public void signalAllServersServerStartBidders(String servername){
		this.lockServers.lock();
		Server server = this.servers.get(servername);
		this.lockServers.unlock();
		server.signalAllStartBidders();
	}
}