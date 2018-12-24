import java.io.*;
import java.net.*;
import java.util.*;

class Users{
	HashMap<String, Socket> usersSockets;

	Users(){
		usersSockets = new HashMap<String, Socket>();
	}

	public synchronized void setUsersSockets(String username, 
															Socket socket){
		this.usersSockets.put(username, socket);
	}

	public synchronized HashMap<String,Socket> getUsersSockets(){
		return this.usersSockets;
	}

	public synchronized void broadCast(String name, String message){
		try{
			for(Map.Entry<String,Socket> entry :  this
												.usersSockets.entrySet()){
				if(!(name.equals(entry.getKey()))){	
					PrintWriter temporaryOut 
								= new PrintWriter(entry.getValue()
													.getOutputStream(),true);
					temporaryOut.println(name + message);
					//temporaryOut.close(); //este close está a fazer com que o meu prog nao funcione, porque ????
				}
			}
		}catch(IOException e){}
	}
}

class ChatServerTHD extends Thread{
	Users users;

	Socket serverSocket;

	ChatServerTHD(Users users, Socket serverSocket){
		this.users = users;
		this.serverSocket = serverSocket;
	}	

	public void run(){
		try{
			String incoming;

			PrintWriter out = new PrintWriter(serverSocket
												.getOutputStream(),true);

			BufferedReader in = new BufferedReader(
						new InputStreamReader(serverSocket
													.getInputStream()));

			out.println("Welcome! Choose your username: ");

			while(this.users.getUsersSockets()
									.containsKey(incoming = in.readLine())){
				out.println(
					"Username is already taken. Choose another one.");
				out.println("Choose your username: ");
			}
				
			this.users.setUsersSockets(incoming, this.serverSocket);

			this.users.broadCast(incoming, " is connected.");
			
			String name = incoming; 

			while((incoming = in.readLine()) != null)
				this.users.broadCast(name, (": " + incoming));

			this.users.getUsersSockets().remove(name);

			this.users.broadCast(name, " is disconnected.");

			out.close();

			in.close();
			
			serverSocket.close();

		}catch(IOException e){}
	}
}

class ChatServer{

	Users users;

	int port;

	ChatServer(int port){
		this.port = port;
		this.users = new Users();
	}

	public void startChatServer() throws IOException{

		ServerSocket connectionSocket = new ServerSocket(this.port);

		while(true){
			Socket serverSocket = connectionSocket.accept();
			ChatServerTHD chatServerTHD = new ChatServerTHD(this.users, 
															serverSocket);
			chatServerTHD.start();
		}
	}

}

class Ex2s{
	public static void main(String[] args) throws IOException{
		ChatServer chatServer = new ChatServer(12345); 
		chatServer.startChatServer();
	}
}