import java.util.concurrent.TimeUnit;


class Banco{

	//um array de inteiros com o saldo de 10 contas
	double saldos[] = new double[10]; 

	private ReentrantLock lock = new ReentrantLock();

	Banco(){
		for(int i=0; i<10; i++)
			this.saldos[i]=0;
	}

	Banco(double saldos[]){
		for(int i=0; i<10; i++){
			this.saldos[i] = saldos[i];
		}
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

	public void transfere(int contaOrig, int contaDest, 
											double valor){
		lock.lock();
		this.debito(contaOrig, valor);
		this.credito(contaDest, valor);
		lock.unlock();
	}

}



class ClienteThd1 extends Thread{
	Banco b; 

	ClienteThd1(Banco b){
		this.b = b;
	}

	public void run(){
		System.out.println(Thread.currentThread().getName()+ " ANT Saldo da conta 0: "+ b.consulta(0));
		System.out.println(Thread.currentThread().getName()+ " ANT Saldo da conta 1: "+ b.consulta(1));
		b.transfere(0, 1, 1000);
		System.out.println(Thread.currentThread().getName()+ " DEP Saldo da conta 0: "+ b.consulta(0));
		System.out.println(Thread.currentThread().getName()+ " DEP Saldo da conta 1: "+ b.consulta(1));
	}
}

class ClienteThd2 extends Thread{
	Banco b; 

	ClienteThd2(Banco b){
		this.b = b;
	}

	public void run(){

		/*sleep para que a thread 2 não comece a executar primeiro que a
		thread 1*/
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){}
		System.out.println(Thread.currentThread().getName()+ "                                    ANT Saldo da conta 0: "+ b.consulta(0));
		System.out.println(Thread.currentThread().getName()+ "                                    ANT Saldo da conta 1: "+ b.consulta(1));
		b.debito(1, 1000);
		System.out.println(Thread.currentThread().getName()+ "                                    DEP Saldo da conta 0: "+ b.consulta(0));
		System.out.println(Thread.currentThread().getName()+ "                                    DEP Saldo da conta 1: "+ b.consulta(1));
	}
}

class Ex3{
	public static void main(String args[]){
		Banco b = new Banco(new double[] {1000,0,0,0,0,0,0,0,0,0});
		System.out.println("Saldo da conta 0: "+ b.consulta(0));
		System.out.println("Saldo da conta 1: "+ b.consulta(1));
		ClienteThd1 t1 = new ClienteThd1(b);
		ClienteThd2 t2 = new ClienteThd2(b);
		t1.start();
		t2.start();
		try{
			t1.join();
			t2.join();
		}
		catch(InterruptedException e){}
		System.out.println("------------------");
		System.out.println("Saldo da conta 0: "+ b.consulta(0));
		System.out.println("Saldo da conta 1: "+ b.consulta(1));
	}
}

/*DÚVIDAS:
-> Se eu tiver o método transfere como synchronized, decidir fazer um 
outro método que não seja synchronized, que modifique as mesmas variáveis 
de instância que o transfere e se tiver duas threads, uma a executar um, e 
outra, outro, esses métodos vão poder estar a executar em simultâneo?  
R.:

->Quando uma thread começa a executar um método que é synchronized, essa
thread adquire o lock daquela instância certo? Isto é o mesmo que dizer 
que, apenas qualquer outro método que seja synchronized, não poderá ser 
executado ou significa que toda a instância fica "paralizada", podendo 
apenas ser acedida pela thread que tem o lock?
R.: 

->Nos slides do prof Neves temos um caso que mostra que o saldo do banco 
teria que ser sempre 1000, e nos slides, tinhamos o caso em que uma 
operação de débito era feita entre as duas operações que compõem 
a operação saldo e por isso o saldo ficava negativo, ou seja, não 
ficava a 1000. No entanto, se formos consultar o saldo do banco entre a
ocorrência daquelas duas operações que compõem a operação transferir o 
saldo do banco tbm não vai ser 1000, porque na verdade são subtraidos 1000
euros e naquele momento ainda não foram somados os outros mil na outra 
conta. Esta dúvida prende-se apenas com uma explicação que o prof deu que
eu acho que entendi mal; perguntar qual é o objetivo de por o synchronyzed
no transfere, porque aquilo que faz mais sentido é querermos o transfere 
como uma operação atómica para não podermos fazer mesmo nada durante aquela
transferência, e isso na vida real não se verifica nunca!!.
R.:
*/

