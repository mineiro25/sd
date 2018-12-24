import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
//import java.lang.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;

class Conta{

	Integer id;

	double saldo;

	private ReentrantLock lock = new ReentrantLock();

	Conta(Integer id, double saldo){
		this.id = id; 
		this.saldo = saldo;;
	}

	public double consultar(){
		this.lock.lock();
		double saldo = this.saldo;
		this.lock.unlock();
		return saldo;
	}

	public void credito(double valor){
		this.lock.lock();
		this.saldo += valor;
		this.lock.unlock();
	}

	public void debito(double valor) throws SaldoInsuficiente{
		if((this.saldo - valor) < 0){
			String msg = "O saldo da conta " + 
								this.id + " não pode ser negativo.";
			throw new SaldoInsuficiente(msg);
		}
		this.lock.lock();
		this.saldo -= valor;
		this.lock.unlock();
	}

	public void contaLock(){
		this.lock.lock();
	}

	public void contaUnlock(){
		this.lock.unlock();
	}

}

class ContaInvalida extends Exception{
	ContaInvalida(String msg){
		super(msg);
	}
}

class SaldoInsuficiente extends Exception{
	SaldoInsuficiente (String msg){
		super(msg);
	}
}


class Banco{

	private HashMap<Integer,Conta> contas;

	private int novoID;

	private ReentrantLock lock = new ReentrantLock();

	Banco(){
		contas = new HashMap<Integer,Conta>();
		novoID = 0;
	}

	public void transfere(Integer idOrigem, Integer idDestino, 
				double valor) throws ContaInvalida, SaldoInsuficiente{
		int min = Math.min(idOrigem, idDestino);
		int max = Math.max(idOrigem, idDestino);

		this.contas.get(min).contaLock();
		this.contas.get(max).contaLock();
		try{
			this.contas.get(idOrigem).debito(valor);
			this.contas.get(idDestino).credito(valor);
		}catch(SaldoInsuficiente e){
			System.out.println(e.getMessage());
			System.out.println("Transferência não realizada.");
		}
		this.contas.get(max).contaUnlock();
		this.contas.get(min).contaUnlock();

	}

	public Integer criarConta(double saldoInicial) 
												throws SaldoInsuficiente {
		if(saldoInicial < 0){
			String msg = "O saldo da conta não pode ser negativo." ;
			throw new SaldoInsuficiente(msg);
		}
	
		lock.lock();
		Integer id = this.novoID++;
		Conta c = new Conta(id, saldoInicial);
		this.contas.put(id, c);
		lock.unlock();
		return id;
	}

	public double fecharConta(Integer id) throws ContaInvalida{
		lock.lock();
		if(!(contas.containsKey(id))){
			throw new ContaInvalida("A conta nao existe: ");
		}
		Conta c = this.contas.get(id);
		c.contaLock();
		double saldo = c.consultar();
		this.contas.remove(id);
		c.contaUnlock();
		lock.unlock();
		return saldo;
	}
	public double saldoNContas(Integer ids []){
		double saldoTotal = 0;
		ArrayList<Integer> contasLocked = new ArrayList(ids.length);
		lock.lock();
		for(int i = 0; i < ids.length; i++){
			if(this.contas.containsKey(ids[i])){
				this.contas.get(ids[i]).contaLock();
				contasLocked.add(ids[i]);		
			}
		}
		lock.unlock();
		for(int id : contasLocked){
			saldoTotal += this.contas.get(id).consultar();
			this.contas.get(id).contaUnlock();
		}
		return saldoTotal;
	}

	public HashMap<Integer,Conta> getContas(){
		return this.contas;
	}

}

class BancoServerTHD extends Thread{
	Banco banco;

	Socket clientSocket;

	BancoServerTHD(Banco banco, Socket clientSocket){
		this.banco = banco;
		this.clientSocket = clientSocket;
	}

	public void run(){
		try{
			String incoming;

			String outgoing;

			PrintWriter out = new PrintWriter(clientSocket
													.getOutputStream(),true);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));

			while((incoming = in.readLine()) != null){
				outgoing = interpreteRequest(incoming);
				out.println(outgoing);
			}

			in.close();
			out.close();
			clientSocket.close();

		}catch(IOException | SaldoInsuficiente | ContaInvalida e){}
	}

	private String interpreteRequest(String request) 
		throws SaldoInsuficiente, ContaInvalida{
		String[] keywords = request.split(" ");
		String r;

		switch(keywords[0].toUpperCase()) {
			case "CREATE":
				r = "Success! Account id: " + this.banco
								.criarConta(Integer.parseInt(keywords[1]));
				break;
			case "CLOSE":
				r = "Success! Amount in account: " + this.banco
								.fecharConta(Integer.parseInt(keywords[1]));
				break;
			case "TRANSFER":
			Integer o = Integer.parseInt(keywords[1]);
			Integer d = Integer.parseInt(keywords[2]);
			Double s = Double.parseDouble(keywords[3]);
				this.banco.transfere(o, d, s);
				r = "Saldo conta " + o + ": " + 
					banco.getContas().get(o).consultar() +
					"Saldo conta " + d + ": " + 
					banco.getContas().get(d).consultar();
				break;
			case "BALANCE":
				Integer ids[] = new Integer[keywords.length-1];
				for(int i = 0; i < keywords.length-1; i++)
					ids[i] = Integer.parseInt(keywords[i+1]);
				r = "Total balance: " + banco.saldoNContas(ids);
				break;
			default:
				r = "InvalidCommand";
				break;
		}

		return r;
	}
}

class BancoServer{
	Banco banco;

	int porto;

	ServerSocket connectionSocket;

	BancoServer(Banco banco, int porto){
		this.banco = banco;
		this.porto = porto;
	}

	public void bancoServerStart(){
		try{
			connectionSocket = new ServerSocket(this.porto);
			while(true){
				Socket clientSocket = connectionSocket.accept();
				BancoServerTHD bancoServerTHD = 
								new BancoServerTHD(banco, clientSocket);
				bancoServerTHD.start();
			}
		}catch(IOException e){}
	}
}

class Ex1s{

	public static void main(String args[]) throws SaldoInsuficiente{
		//criação do banco
		Banco banco = new Banco();
		double saldo = 10;
		for(int i = 0; i<2; i++){
			try{
				banco.criarConta(saldo);
			}catch(SaldoInsuficiente e){
				System.out.println(e.getMessage() + saldo);
			}
		}

		/*criacao do server que vai receber as operações que o banco 
		deve fazer*/
		BancoServer bancoServer = new BancoServer(banco, 12345);
		bancoServer.bancoServerStart();

	}

}


/*
Dúvidas:
R.:

*/