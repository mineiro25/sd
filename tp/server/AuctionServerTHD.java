import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class AuctionServerTHD extends Thread{
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
						this.data.setBidders(incoming, null);

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
			this.data.unlockServersServer(incoming);
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