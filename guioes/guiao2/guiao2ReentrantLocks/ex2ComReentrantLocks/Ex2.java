import java.util.concurrent.locks.ReentrantLock;

class Banco{

	double saldos[] = new double[10]; 

	private ReentrantLock lock = new ReentrantLock(); 

	Banco(){
		for(int i=0; i<10; i++)
			this.saldos[i]=0;
	}

	public double consulta(int conta){
		lock.lock();
		double r = saldos[conta];
		lock.unlock();
		return r;
	}

	public void credito(int conta, double c){
		lock.lock();
		saldos[conta] += c; 
		lock.unlock();
	}

	public void debito(int conta, double d){
		lock.lock();
		saldos[conta] -= d;
		lock.unlock();
	}

}

class ClienteThd1 extends Thread{
	Banco b; 

	ClienteThd1(Banco b){
		this.b = b;
	}

	public void run(){
		System.out.println("Saldo da conta 0: "+ b.consulta(0));
		for(int i=0; i<1000; i++)
			b.credito(0, 5);
		System.out.println("Saldo da conta 0: "+ b.consulta(0));
	}
}

class ClienteThd2 extends Thread{
	Banco b; 

	ClienteThd2(Banco b){
		this.b = b;
	}

	public void run(){
		System.out.println("Saldo da conta 0: "+ b.consulta(0));
		for(int i=0; i<1000; i++)
			b.debito(0, 5);
		System.out.println("Saldo da conta 0: "+ b.consulta(0));
	}
}

class Ex2{
	public static void main(String args[]){
		Banco b = new Banco();
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
	}
}

/*
Dúvidas:
->Podemos fazer isto?
public double consulta(int conta){
	lock.lock();
	return saldos[conta];
}
R.: Não podemos. Temos que guardar numa variável auxiliar e dar o unlock!
*/