import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class Banco{

	Conta conta[];

	Banco(){
		conta = new Conta[10];
		for(int i=0; i<10; i++)
			this.conta[i] = new Conta(0);
	}

	Banco(Conta conta[]){
		this.conta = conta;
	}

	public double consulta(int nrConta){
		return conta[nrConta].consulta();
	}

	public void credito(int nrConta, double c){
		conta[nrConta].credito(c);
	}

	public void debito(int nrConta, double d){
		conta[nrConta].debito(d);
	}

	public void transfere(int contaOrig, int contaDest, 
										double valor){	
		int min = Math.min(contaOrig, contaDest);
		int max = Math.max(contaOrig, contaDest);

		conta[min].cLock();
		conta[max].cLock();
		conta[contaOrig].debito(valor);
		conta[contaDest].credito(valor);
		conta[max].cUnlock();
		conta[min].cUnlock();
	}

}

class Conta{
	double saldo;

	private ReentrantLock lock = new ReentrantLock();

	Conta(double saldo){
		this.saldo = saldo;
	}

	public double consulta(){
		lock.lock();
		double s = saldo; 
		lock.unlock();
		return s;
	}

	public void credito(double valor){
		lock.lock();
		this.saldo += valor;
		lock.unlock();
	}

	public void debito(double valor){
		lock.lock();
		this.saldo -= valor;
		lock.unlock();
	}

	public void cLock(){
		lock.lock();
	}

	public void cUnlock(){
		lock.unlock();
	}	
	

}

class ClienteThd1 extends Thread{
	Banco b; 

	ClienteThd1(Banco b){
		this.b = b;
	}

	public void run(){
		for(int i = 0; i<100000; i++)
			b.transfere(0, 1, 5);
	}
}

class ClienteThd2 extends Thread{
	Banco b; 

	ClienteThd2(Banco b){
		this.b = b;
	}

	public void run(){		
		for(int i = 0; i<100000; i++)
			b.transfere(1, 0, 5);
	}
}

class Ex4c{
	public static void main(String args[]){
		
		Conta conta[] = new Conta[10];

		for(int i=0; i<10; i++)
			conta[i] = new Conta(0);

		Banco b = new Banco(conta);
		
		ClienteThd1 t1 = new ClienteThd1(b);
		ClienteThd2 t2 = new ClienteThd2(b);
		
		t1.start();
		t2.start();
		
		try{
			t1.join();
			t2.join();
		}
		catch(InterruptedException e){}
		
		System.out.println("Saldo final da conta 0: "+ b.consulta(0));
		System.out.println("Saldo final da conta 1: "+ b.consulta(1));
	}
}

