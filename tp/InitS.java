import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;//ReentrantLock;

//APLICAR CONCORRÊNCIA
class Bidder{
	String username;

	String password;

	double debt;

	ReentrantLock lock;

	Bidder(){
		this.username = null;
		this.password = null;
		this.debt = 0;
		this.lock = new ReentrantLock();
	}

	Bidder(String username, String password){
		this.username = username;
		this.password = password;
		this.debt = 0;
		this.lock = new ReentrantLock();
	}

	public void setUsername(String username){
		this.lock.lock();
		this.username = username;
		this.lock.unlock();
	}

	public void setPassword(String password){
		this.lock.lock();
		this.password = password;
		this.lock.unlock();
	}

	public void setDebt(double debt){
		this.lock.lock();
		this.debt = debt;
		this.lock.unlock();
	}

	public String getUsername(){
		this.lock.lock();
		String result = this.username;
		this.lock.unlock();
		return result;
	}

	public String getPassword(){
		this.lock.lock();
		String result = this.password;
		this.lock.unlock();
		return result;
	}

	public double getDebt(){
		this.lock.lock();
		double result = this.debt;
		this.lock.unlock();
		return result;
	}

	public void sumDebt(double debt){
		this.lock.lock();
		this.debt += Math.round(debt*100.0) / 100.0;
		this.debt = Math.round(this.debt*100.0) / 100.0;
		this.lock.unlock(); 
	}
}

class CounterDown extends Thread{
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

class Server{
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




class DataBase{
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
		this.servers.put("m5.large", new Server("m5.large", 15, 2));
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
		if(i == 0){ out.println("-> There are free servers.");}

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




class AuctionServerTHD extends Thread{
	DataBase data;

	Socket socket;

	BufferedReader in;

	PrintWriter out;

	String bidderName;

	String purchasedServer;

	AuctionServerTHD(DataBase data, Socket socket) throws IOException{
		this.data = data;
		this.socket = socket;
		this.in =  new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.out = new PrintWriter(this.socket.getOutputStream(), true);
		this.bidderName = null;
		this.purchasedServer = null;
	}

	public void run(){
		String incoming;

		try{
			this.login();

			loop1: while(true){
				
				this.data.printServers(this.out);


				loop3: while(true){
					this.out.println("\nChoose one of the following purchase options: ");
					this.out.println("1. Direct purchase (demand instance)");
					this.out.println("2. Auction purchase (spot instance)");
					this.out.println("3. Check debt");

					incoming = this.in.readLine();

					switch(incoming.toUpperCase()){
						case "1":
						case "DIRECT":
						case "DIRECT PURCHASE":
							this.demandInstance();
							break loop3;
						case "2":
						case "AUCTION":
						case "AUCTION PURCHASE":
							this.spotInstance();
							break loop3;
						case "3":
						case "DEBT":
						case "CHECK DEBT":
						case "CHECK":
							this.out.println("Debt value: " + this.data.getBidderDebt(this.bidderName));
							break loop3;
						default:
							this.out.println("Invalid option.");
							break;
						
					}

				}

				
				out.println("\nDo you want to pick a server? If not, you will be logged out!");
				loop2: while((incoming = this.in.readLine()) != null){
					switch(incoming.toUpperCase()){ 
						case "YES":
						case "Y":
							break loop2;

						case "NO":
						case "N":
						case "NOT":
							break loop1;

						default:
							this.out.println("Invalid option. It is a \"yes\" or \"no\" question.");
							break;
					}
				}
			}

			this.logout();
		}catch(IOException e){}
	}

	private void login(){
		String incoming;
		
		String incoming2;
		
		try{
			this.out.println("\n-------------- Welcome to SERVERUM! --------------\n");
			
			while((this.bidderName) == null){
				this.out.println("Write and enter one of the following options:"); 
				this.out.println("1. SIGN UP (New bidder)");
				this.out.println("2. SIGN IN (Registered bidder)");
				this.out.println("Option: ");
		
				incoming = this.in.readLine();
				switch(incoming.toUpperCase()){
					case "1":
					case "SIGN UP":
						this.out.println("\nChoose a username:");
						while(this.data.isBiddersKey(incoming = (this.in.readLine())) || incoming.isEmpty()){
							this.out.println("Bidder's username already exists.");
							this.out.println("\nChoose another username:");
						}

						this.out.println("Choose a password (more than two characters):");
						while(((incoming2 = this.in.readLine()).length())< 2){
							this.out.println("Your password is too smale.");
							this.out.println("Choose another password:");
						}

						this.bidderName = incoming;
						this.data.setBidders(incoming, new Bidder(incoming, incoming2));
						this.data.setConnectedBidders(incoming, this.socket);
						System.out.println(incoming + " is connected.");
						this.out.println("You are connected.");
						//this.data.broadcast(incoming, " is connected.");
						break;
					
					case "2":
					case "SIGN IN":
						this.out.println("\nEnter your username: ");
						while(!(this.data.isBiddersKey(incoming = this.in.readLine()))){
							this.out.println("Bidder's username doesn't exist.");
							this.out.println("\nEnter your username:");
						}
						
						this.out.println("Enter your password: ");
						while(!(this.data.isBidderPassword(incoming, this.in.readLine()))  ){
							this.out.println("Wrong password.");
							this.out.println("Enter your password again:");
						}
						
						if(!(this.data.isConnectedBidderKey(incoming))){
							this.bidderName = incoming;
							this.data.setConnectedBidders(incoming, this.socket);
							System.out.println(incoming + " is connected.");
							this.out.println("You are connected.");
							//this.data.broadcast(incoming, " is connected.");
						}
						else{
							this.out.println("This user is already connected.\n");

						}
						break;

						default:
							this.out.println("Invalid option.\n");
							break;
				}
			}
		}catch(IOException e){}
	}

	private void logout(){
		try{
			this.data.removeConnectedBidders(this.bidderName);

			this.out.println("\nYou were disconnected.");

			System.out.println(this.bidderName + " is disconnected.");			

			this.socket.shutdownOutput();

			this.in.close();

			this.out.close();
		}catch(IOException e){}
	}


	private void demandInstance(){
		String incoming = null;
		String incoming2 = null;
		double res;

		this.data.printServers(this.out);
		this.out.println("\nChoose the server you want to purchase:");

		try{
			while(!(this.data.isServersKey(incoming = this.in.readLine()))){
				this.out.println("The choosen server doesn't exist. Try again:");
			}
		}catch(IOException e){}

		this.data.lockServersServer(incoming);
		if(this.data.isServersServerConnected(incoming) && this.data.getServersServerAuctionTime(incoming) == -1){
			out.println("The server is being used by a bidder, but you have priority! Do you still want to purchase it?");
			loop4: while(true){
				try{	
					switch((incoming2 = this.in.readLine()).toUpperCase()){
						case "YES":
						case "Y":
							/*para garantir que não existem mais threads a querem tirar o bidder que lá está, assim passam para o próximo if e aguardam que esta thread 
							ganhe a exclusao mutua do server */
							this.data.setServersServerAuctionTime(incoming, 0);
							this.data.unlockServersServer(incoming);

							out.println("We're disconnecting the other user.");
							this.purchasedServer = this.data.serverEstablishConnectionSpot(this.bidderName, incoming);
							res = (double) this.data.serverUsingServer(incoming, this.in, this.out);
							this.purchasedServer = null;
							this.data.serverDisestablishConnectionDemand(incoming);
							this.data.biddersBidderSumDebt(this.bidderName, res);

							break loop4;
						case "NO":
						case "N":
							this.data.unlockServersServer(incoming);
							break loop4;
						default:
							this.out.println("Invalid option. Enter \"yes\" or \"no\":\n");
							break;
					}
				}catch(IOException e){}
			}
		}
		else if(this.data.isServersServerConnected(incoming) && this.data.isServersServerBidded(incoming) == false){
			this.data.unlockServersServer(incoming);
			out.println("The server is already in use. Do you still want to purchase it (it may take a while)?");
			loop4: while(true){
				try{	
					switch((incoming2 = this.in.readLine()).toUpperCase()){
						case "YES":
						case "Y":
							out.println("Please, wait for the connection.");
							this.purchasedServer = this.data.serverEstablishConnectionDemand(this.bidderName, incoming);
							res = (double) this.data.serverUsingServer(incoming, this.in, this.out);
							this.purchasedServer = null;
							this.data.serverDisestablishConnectionDemand(incoming);
							this.data.biddersBidderSumDebt(this.bidderName, res);
							break loop4;
						case "NO":
						case "N":
							break loop4;
						default:
							this.out.println("Invalid option. Enter \"yes\" or \"no\":\n");
							break;
					}
				}catch(IOException e){}
			}
		}
		else if(this.data.isServersServerBidded(incoming) == false){
			this.data.unlockServersServer(incoming);
			this.purchasedServer = this.data.serverEstablishConnectionDemand(this.bidderName, incoming);
			res = (double) this.data.serverUsingServer(incoming, this.in, this.out);
			this.purchasedServer = null;
			this.data.serverDisestablishConnectionDemand(incoming);
			this.data.biddersBidderSumDebt(this.bidderName, res);
		}
		else{
			this.out.println("An auction to get this server is happening.");
			return;
		}
	}

	private void spotInstance(){
		String incoming = null;
		
		String incoming2 = null;
		
		double value = 0;

		double res;

		this.data.printServers(this.out);
		this.out.println("\nChoose the server you want to bid:");

		try{
			while(!(this.data.isServersKey(incoming = this.in.readLine()))){
				this.out.println("The choosen server doesn't exist. Try again:");
			}
		}catch(IOException e){}


		if(this.data.isServersServerConnected(incoming) == true && this.data.isServersServerBidded(incoming) == false){
			this.out.println("The server was rented for the nominal price.");
			return;
		}

		if(this.data.isServersServerConnected(incoming) == true && this.data.isServersServerBidded(incoming)){
			this.out.println("The server was bidded and is now being used.");
			return;
		}

		this.data.lockServersServer(incoming);
		if(this.data.getServersServerAuctionPrice(incoming)  == 0)
			this.data.setServersServerAuctionPrice(incoming, -1);
		else if(this.data.getServersServerAuctionPrice(incoming)  == -1){
			this.out.println("Someone is about to start the auction. Please, wait!");
			this.data.awaitServersServerStartBidders(incoming);
		}
		this.data.unlockServersServer(incoming);


		if(this.data.isServersServerConnected(incoming) == false && this.data.getServersServerAuctionPrice(incoming)  == -1){
			value = this.initBidding(incoming);
			
			value = this.bidding(incoming, value);
		}
		else if(this.data.isServersServerConnected(incoming) == false && this.data.isServersServerBidded(incoming) == true){
			this.out.println("You are in the auction. Your bid must be higher than " + this.data.getServersServerAuctionPrice(incoming) + "!");
			
			this.data.updateServersServerBiddingers(incoming, this.bidderName, this.socket);
			
			if(this.data.getServersServerAuctionTime(incoming) > 9)
				this.out.println("                                                  Clock: " + this.data.getServersServerAuctionTime(incoming) + "s left");
			else this.out.println("                                                  Clock: 0" + this.data.getServersServerAuctionTime(incoming) + "s left");

			value = this.bidding(incoming, value);
		}

		if(value == this.data.getServersServerAuctionPrice(incoming)){
			this.out.println("You won the auction!\n");
			
			this.purchasedServer = this.data.serverEstablishConnectionSpot(this.bidderName, incoming);
			res = (double) this.data.serverUsingServer(incoming, this.in, this.out);
			this.purchasedServer = null;
			this.data.serverDisestablishConnectionSpot(incoming);
			this.data.biddersBidderSumDebt(this.bidderName, res);

		}

		else this.out.println("You didn't win the auction!\n");
	}


	private double initBidding(String incoming){
		String incoming2 = null;

		double value = this.data.getServersServerBaseAuctionPrice(incoming);

		this.out.println("You will start the auction. Enter a value equal or higher than the base price to bid:");
		
		this.data.updateServersServerBiddingers(incoming, this.bidderName, this.socket);

		//a licitação tem que ocorrer primeiro que o início do relógio
		while(true){
			try{
				incoming2 = this.in.readLine();
				
				if((incoming2.matches("-?\\d+(\\.\\d+)?")) == false ){
					this.out.println("Only numbers! Please, bid again: ");
				}
				else if(Double.parseDouble(incoming2) < this.data.getServersServerBaseAuctionPrice(incoming)){
					this.out.println("Low value! Please, bid again: ");
				}
				else if(Double.parseDouble(incoming2) > this.data.getServersServerNominalPrice(incoming)){
					this.out.println("Excessive value! Please, bid again: ");
				}
				else{
					value = Double.parseDouble(incoming2);
					incoming2 = "                                                  "+this.bidderName + " bid " +  value + "." ;
					this.data.broadcastServersServerBiddingers(incoming, incoming2);
					this.data.setServersServerAuctionPrice(incoming, value);
					break;						
				}
			}catch(IOException e){}
		}

		this.out.println("You just started the auction!");
		this.data.serversServerInitCountdown(incoming);
		this.data.signalAllServersServerStartBidders(incoming);
		return value;
	}

	private double bidding(String incoming, double value){
		String incoming2 = null;

		while(true){
			try{
				if((incoming2 = this.in.readLine()).equals("CLOSEREADLINE")){
					this.out.println("The auction has ended.");
					break;
				}

				this.data.lockServersServer(incoming);
				if((incoming2.matches("-?\\d+(\\.\\d+)?")) == false ){
					this.out.println("Only numbers! Please, bid again: " + incoming2);
					this.data.unlockServersServer(incoming);
				}
				else if(Double.parseDouble(incoming2) <= this.data.getServersServerAuctionPrice(incoming)){
					this.out.println("Low value! Please, bid again: "+ incoming2);
					this.data.unlockServersServer(incoming);
				}
				else if(Double.parseDouble(incoming2) == this.data.getServersServerAuctionPrice(incoming)){
					this.out.println("Equal value! Please, bid again: "+ incoming2);
					this.data.unlockServersServer(incoming);
				}
				else if(Double.parseDouble(incoming2) > this.data.getServersServerNominalPrice(incoming)){
					this.out.println("Excessive value! Please, bid again: "+ incoming2);
					this.data.unlockServersServer(incoming);
				}
				else if(this.data.getServersServerAuctionTime(incoming) > 0){
					value = Double.parseDouble(incoming2);
					incoming2 = "                                                  "+this.bidderName + " bid " +  value + "." ;
					this.data.broadcastServersServerBiddingers(incoming, incoming2);
					this.data.setServersServerAuctionPrice(incoming, value);
					this.data.unlockServersServer(incoming);
				}
			}catch(IOException e){}
		}

		return value;
	}



}





class AuctionServer{
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





class InitS{
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