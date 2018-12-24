import java.util.concurrent.TimeUnit;

/*Este exercício 4 b) ajuda-nos a perceber que no exercício 4 a) estamos 
a fazer mal o método transferir. Isto é: o método transferir precisa de
exclusão mútua? Precisa! (sem ela, as operações de débito e crédito, não
são executadas de forma atómica). Então não basta aplicar o synchronized
atrás de trasnfere? Podemos fazê-lo, mas, desse modo, estamos novamente
a adquirir exclusão mútua para a instância de banco, impedindo a execução 
de outras operações que poderiam ser feitas em paralelo com esta, que é 
o objetivo contrário ao do exercício 4. Sendo assim, existe uma forma 
de aplicarmos exclusão mútua ao nível da(s) conta(s) sobre a(s) qual/quais 
pretendemos fazer alterações/consultas...*/

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

	/*Deste modo estamos apenas a adquirir a exclusão mútua das instâncias
	das contas sobre as quais queremos fazer operações*/
	public void transfere(int contaOrig, int contaDest, 
										double valor){
		synchronized(conta[contaOrig]){
			synchronized(conta[contaDest]){
				conta[contaOrig].debito(valor);
				conta[contaDest].credito(valor);
			}
		}
	}

}

class Conta{
	double saldo;

	Conta(double saldo){
		this.saldo = saldo;
	}
	
	/*nestas três operações podíamos retirar o synchronized que temos atrás
	do nome de cada uma; para isso teríamos que acrescentar outra forma
	de exclusão mútua, fazendo, por exemplo, aquilo que fizemos em 
	"transfere" para as restantes operações*/
	public synchronized	double consulta(){
		return saldo;
	}

	public synchronized void credito(double valor){
		this.saldo += valor;
	}

	public synchronized void debito(double valor){
		this.saldo -= valor;
	}

}

class ClienteThd1 extends Thread{
	Banco b; 

	ClienteThd1(Banco b){
		this.b = b;
	}

	//transfere da conta 0 para a 1
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

	//transfere da conta 4 para a 3 
	public void run(){
		for(int i = 0; i<100000; i++)
			b.transfere(4, 3, 5);
	}
}

class Ex4b{
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

/*DÚVIDAS:
-> Nos slides, o professor tem: 
public void transferir(conta0,conta1,valor){
	synchronized (conta0){
		synchronized (conta1){
			conta0.levanta(valor)
			conta1.deposita(valor)
		}
	}
}
Que synchronized's são aqueles que estão aqui em cima? Para aplicarmos
exclusão mútua ao nível das contas não é suposto colocar os métodos 
que ela tem, cada um, com synchronized????
R.:Sim. Os métodos que apenas alteram/consultam as variáveis de uma 
intância (de Conta), são todos invocados com o synchronized, pois na mesma 
conta apenas pode estar a ser feita uma operação. No entanto, se 
estivermos a fazer operações num nível mais acima (no banco), por 
exemplo - fazer uma transferência entre contas (operação que só existe
na classe banco) - não precisamos de aplicar o synchronized a todo o 
método (que implica que seja feito o lock da instância de banco), que era 
o que fazíamos até agora, isto porque as operações 
nem sempre são feitas envolvendo as mesmas contas (isto é, transferencia 
de 0 para 1 e credito de 5€ em 1). Por esse motivo, não é necessário 
estarmos a adquirir o lock da instância banco e podemos adquirir o 
lock apenas das instâncias (de contas) que estamos a utilizar. 

Exemplificando, no caso que temos imediatamente a cima, a thread que 
executa aquele método transferir (assumindo que este pertence à instância
banco e que existem várias instâncias conta), irá adquirir, não o lock da 
instância banco (pois transferir não tem synchronized), mas sim os locks das 
instâncias conta, neste caso da conta 0 e da conta 1 (por esta ordem). Ou 
seja, a thread a executar o método transferir, adquire exclusão mútua 
primeiro de conta0 e depois de conta1. OU SEJA, quaisquer operações y 
executadas dentro de synchronized(x){y} serão executadas sobre o regime
de exclusão mútua para a instância x!

->
R.:
*/


