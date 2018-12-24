import java.util.concurrent.TimeUnit;

/*Neste exercício é-nos pedido que acrescentemos o método transfere 
à classe Banco. Este método é a composição das operações crédito e 
débito de um determinado valor sobre duas contas. Para testarmos isto, 
fará sentido pôr 1 cliente (thread) a transferir, por exemplo 1000€ da 
conta 0 para a conta 1, e outro cliente a levantar 1000€ da conta 1.

O que acontecerá se escrevermos o método transfere sem que este 
implemente exclusão mútua? Se ao método transfere não for aplicada 
exclusão mútua, então qualquer thread que o comece a executar, só vai 
encontrar exclusão mútua quando chegar ao método debito, aí essa mesma
thread adquire o lock da instância, e durante o período que ela o detem
só ela tem acesso às variáveis de instância (contas). Até aqui, tudo bem. 
Mas, chegamos ao momento em que essa thread liberta o lock (unlock), e por 
mero acaso uma outra thread, muito rapidamente, decide executar o método 
debito sobre a conta de destino da transferência anteriormente falada. Ora, 
esta thread, irá modificar o saldo da conta 1, não obstante, esta operação
está a ser feita e a operação de transferência ainda nem foi concluida. 
Percebemos então que o método transfere que devia ser uma operação cuja 
execução fosse completamente sequencial, é afinal uma operação que permite
que outras sejam executadas naquele "buraco" existente entre o debito e o 
credito. 

Posto isto, a solução é definir o método transfere como synchronized; 
nesse caso, sempre que uma thread o executar, essa thread, vai, do início
ao fim da execução do método, adquirir o lock da instância banco, pelo que
mais nenhuma operação (que seja synchronized) vai poder ser feita!*/

class Banco{

	//um array de inteiros com o saldo de 10 contas
	double saldos[] = new double[10]; 

	Banco(){
		for(int i=0; i<10; i++)
			this.saldos[i]=0;
	}

	Banco(double saldos[]){
		for(int i=0; i<10; i++){
			this.saldos[i] = saldos[i];
			if (this.saldos[i] < 0) {
 	     		throw new ArithmeticException("O saldo da conta "+i+" é negativo");
    		}		
		}
	}

		public synchronized double consulta(int conta){
			return saldos[conta];
		}

		public synchronized void credito(int conta, double c){
			saldos[conta] += c; 
		}

		public synchronized void debito(int conta, double d){
			saldos[conta] -= d;
			if (this.consulta(conta) < 0) {
			       throw new ArithmeticException("O saldo da conta é negativo");
			}
		}

		public synchronized void transfere(int contaOrig, int contaDest, 
											double valor){
			this.debito(contaOrig, valor);
			this.credito(contaDest, valor);
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

