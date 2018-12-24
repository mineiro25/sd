import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;

class EchoServer{

	public static void main(String args[]) throws Exception{
		int port = Integer.parseInt(args[0]);
		ServerSocket ss = new ServerSocket(port);
		while(true){
			Socket cs = ss.accept();
			PrintWriter pw = new PrintWriter(cs.getOutputStream());
			BufferedReader br = new BufferedReader(
				new InputStreamReader(cs.getInputStream()));
			while(true){
				String line = br.readLine();
				if(line == null) break;
				pw.println(line);
				pw.flush();
			}
			pw.close();
			cs.close();
		}

	}
}