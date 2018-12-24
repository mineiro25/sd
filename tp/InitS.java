import java.io.*;
import java.net.*;
import java.util.*;

//APLICAR CONCORRÊNCIA
class Bidder{
	String username;

	String password;

	Bidder(){
		this.username = null;
		this.password = null;
	}

	Bidder(String username, String password){
		this.username = username;
		this.password = password;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public void setPassword(String password){
		this.password = password;
	}

	/*public void setSocket(Socket socket){
		this.socket = socket;
	}*/

	public String getUsername(){
		return this.username;
	}

	public String getPassword(){
		return this.password;
	}

	/*public Socket getSocket(){
		return this.socket;
	}*/
}




class Server{
	String name;

	Double precoNominal;

	Bidder bidder;

	Server(String name, Double precoNominal, Bidder bidder){
		this.name = name;
		this.precoNominal = precoNominal;
		this.bidder = bidder;
	}
}




class DataBase{
	HashMap<String, Bidder> bidders;

	HashMap<String, Socket> connectedBidders;

	HashMap<String, Server> servers;

	HashMap<String, Server> serversInUse;
	
	DataBase(){
		this.bidders = new HashMap<String, Bidder>();
		this.connectedBidders = new HashMap<String, Socket>();
		this.servers = new HashMap<String, Server>();
	}

	public boolean isBiddersKey(String username){
		return this.bidders.containsKey(username);
	}

	public boolean isConnectedBidderKey(String username){
		return this.connectedBidders.containsKey(username);
	}

	public boolean isBidderPassword(String username, String password){
		return this.bidders.get(username).getPassword().equals(password);
	}

	public void setBidders(String username, Bidder bidder){
		this.bidders.put(username, bidder);
	}

	public void setConnectedBidders(String username, Socket socket){
		this.connectedBidders.put(username, socket);
	}

	public void initDataBase(){
		this.servers.put("t3.micro", new Server("t3.micro", 0.99, null));
		this.servers.put("m5.large", new Server("m5.large", 3.12, null));
	}

	public void removeConnectedBidders(String username){
		this.connectedBidders.remove(username);
	}

	public Bidder getBiddersBidder(String username){
		return this.bidders.get(username);
	}

	public void broadcast(String username, String message){
		try{
			for(Map.Entry<String,Socket> entry :  this.connectedBidders.entrySet()){
				if(!(username.equals(entry.getKey()))){	
					PrintWriter temporaryOut = new PrintWriter(entry.getValue().getOutputStream(),true);
					temporaryOut.println(username + message);
					//temporaryOut.close(); //este close está a fazer com que o meu prog nao funcione, porque ????
				}
			}
		}catch(IOException e){}
	}
}




class AuctionServerTHD extends Thread{
	DataBase data;

	Socket socket;

	BufferedReader in;

	PrintWriter out;

	Bidder bidder;

	AuctionServerTHD(DataBase data, Socket socket) throws IOException{
		this.data = data;
		this.socket = socket;
		this.in =  new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.out = new PrintWriter(this.socket.getOutputStream(), true);
		this.bidder = new Bidder();
	}

	public void run(){
		String incoming;

		try{
			this.login();

			while(){

				if((incoming = in.readLine()) != null){
					//mostra servidores disṕoníveis (e em uso?)

					switch
					this.demandInstance();

					this.spotInstance();		

				}
				//dali de cima saiu ou com um servidor ou sem um servidor

				if(/*conseguiu servidor, se sim, tem um identificador de reserva)*/{
					//inicia uso do servidor 
						//começa a contar-se o tempo em segundos
					while(in.readLine() != /*identificador de reserva*/);
						//para-se a contagem do tempo 

				}

				switch

			}

			this.logout();
		}catch(IOException e){}
	}

	private void login(){
		String incoming;
		
		String incoming2;
		
		try{
			this.out.println("\n--------------Welcome to Server Auction!--------------\n");
		
			while((this.bidder.getUsername()) == null){
				this.out.println("Write and enter one of the following options:"); 
				this.out.println("1. SIGN UP (New bidder)");
				this.out.println("2. SIGN IN (Registered bidder)");
				this.out.println("Option: ");
		
				incoming = in.readLine();
				switch(incoming.toUpperCase()){
					case "1":
					case "SIGN UP":
						this.out.println("\nChoose a username:");
						while(this.data.isBiddersKey(incoming = (in.readLine())) || incoming.isEmpty()){
							this.out.println("Bidder's username already exists.");
							this.out.println("\nChoose another username:");
						}

						this.out.println("Choose a password (more than two characters):");
						while(((incoming2 = in.readLine()).length())< 2){
							this.out.println("Your password is too smale.");
							this.out.println("Choose another password:");
						}

						this.bidder.setUsername(incoming);
						this.bidder.setPassword(incoming2);
						this.data.setBidders(incoming, this.bidder);
						this.data.setConnectedBidders(incoming, this.socket);
						System.out.println(incoming + " is connected.");
						//this.data.broadcast(incoming, " is connected.");
						break;
					
					case "2":
					case "SIGN IN":
						this.out.println("\nEnter your username: ");
						while(!(this.data.isBiddersKey(incoming = in.readLine()))){
							this.out.println("Bidder's username doesn't exist.");
							this.out.println("\nEnter your username:");
						}
						
						this.out.println("Enter your password: ");
						while(!(this.data.isBidderPassword(incoming, in.readLine()))  ){
							this.out.println("Wrong password.");
							this.out.println("Enter your password again:");
						}
						
						if(!(this.data.isConnectedBidderKey(incoming))){
							this.bidder = this.data.getBiddersBidder(incoming);
							this.data.setConnectedBidders(incoming, this.socket);
							System.out.println(incoming + " is connected.");
							//this.data.broadcast(incoming, " is connected.");
						}
						else{
							out.println("This user is already connected.\n");

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
			this.data.removeConnectedBidders(this.bidder.getUsername());

			System.out.println(this.bidder.getUsername() + " is disconnected.");

			socket.shutdownOutput();

			in.close();

			out.close();
		}catch(IOException e){}
	}


	private void demandInstance(){

	}
	private void spotInstance(){
		if(/*server in use*/)
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
		try{
			AuctionServer auctionServer = new AuctionServer(Integer.parseInt(args[0]));
			auctionServer.startAuctionServer();
		}catch(IOException e){}
	}
}