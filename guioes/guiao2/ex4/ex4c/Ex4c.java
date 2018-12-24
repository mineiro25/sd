import java.util.concurrent.TimeUnit;
//mal!! eliminar cenas nos comentários!!

/*existe a cena do deadlock que é apesar dos locks serem feitos ao nivel 
das contas, imaginemos que uma thread faz transferencia de 0 para 1
e outra thread 2 de 1 para 0; a thread 1 tem lock da conta 0 mas ao mesmo tempo a 
thread 2 tem lock da conta 1, ora agora thread 1 quer aceder a conta 1 
mas nao pode, e a thread 2 quer aceder a conta 0 mas nao pode, ficam assim
eternamente*/

/*Até agora aprendemos a ter exclusão mútua ao nível das contas, mesmo
quando fazemos uma transferência! No entanto, existe um problema ainda 
não explorado e que pode acontecer; os DEADLOCKS. Da forma como escrevemos
o programa, estamos a admitir que podem existir operações em contas que 
não estejam a ser utilizadas, não obstante, não olhamos para um caso 
particular... 

Imaginemos duas threads... uma deve executar uma transferência da conta 
0 para a 1 e outra deve executar outra transferência de 1 para 0. 
Imaginemos também que ambas as operações são executadas em simultâneo. 
Ora, no exercício 4 b), vimos que quando o método "transfere" é invocado,
a thread que o invoca, aplica exclusão mútua à conta de origem e de 
seguida à conta de destino. Imaginemos então que ENTRE a aplicação de 
exclusão mútua à conta de origem e a aplicação de exclusão mútua à conta
de destino, outra thread decide iniciar a transferência que lhe foi 
incumbida e aplica exclusão mútua à conta de destino. Reparamos no que 
aconteceu? Uma das threads adquiriu a exclusão mútua da conta de origem,
e antes que esta conseguisse adquirir exclusão mútua da conta destino, 
outra thread adquiriu-a primeiro. Ora, a primeira thread, para acabar 
o seu "serviço", necessita de esperar que a outra thread liberte o lock 
daquela conta para ganhar exclusão mútua dessa conta destino; por outro 
lado, a segunda thread precisa de ganhar a exclusão mútua da conta origem 
mas terá também de esperar que a primeira thread liberte o lock da conta
origem. O que acontece é que nenhuma das threads irá libertar o lock da 
conta, pois para o fazerem precisam de concluir a operação que estão a 
executar, que implica adquirir o lock de outra conta (que já está a ser 
usado por outra thread)*. Isto leva o sistema operativo a entrar em 
deadlock, ficando um processo à espera de outro, enquanto que esse outro
também está à espera do primeiro! 

Como se soluciona o problema? Temos que arranjar um mecanismo que evite 
que o sistema operativo não caia neste erro, ou seja, se duas threads 
tentarem fazer transferências opostas, certeficar-nos-emos que ambas 
começam primeiro por tentar fazer o lock da mesta instância. Desse modo, 
se uma não conseguir fazer esse lock, irá esperar que a outra conclua 
o resto da execução do método.

Facilmente resolvemos o problema pegando nos índices das contas em 
questão e fazendo o primeiro synchronized sobre a conta cujo índice é
menor. Por outras palavras, fazemos ambas as threads fazerem o primeiro
lock sobre a mesma conta, e deste modo só uma conseguirá executar o 
transfere na sua totalidade. 

Eventualmente, podíamos estar-nos a questionar acerca dos problemas que
traria o facto de nesta solução estarmos a alterar a ordem dos 
synchronizeds em relação ao exercício 4 b). Na verdade, a ordem pela 
qual colocamos os synchronizeds, isto é, se fazemos primeiro 
synchronized(0){} e só depois synchronized(1){}, não importa para o 
resultado. Importa apenas para efeitos de exclusão mútua, pois obriga 
o sistema operativo a decidir a qual das threads atribui o lock daquela 
instância antes que ambas comecem a tentar fazer outro lock.

Exemplificando:

							DEMONSTAÇÃO DO ERRO

	            thread 1						thread 2
					|								|
					|								|
			synchronized(0){}				synchronized(1){}
				(consegue)						(consegue)
			//adquire lock de 0				//adquire lock de 1
					|								|	
					|								|
NÃO 	<--	synchronized(1){}				synchronized(0){} --> NÃO
CONSEGUE			|								|			CONSEGUE
					|								|
				DEADLOCK 						DEADLOCK




							DEMONSTAÇÃO DA SOLUÇÃO
									ENCONTRADA

	            thread 1						thread 2
					|								|
			i = minimoEntre(0,1)			i = minimoEntre(0,1)
				  (i=0)							  (i=0)
					|								|
			synchronized(i=0){}				synchronized(i=0){}
				(*consegue)						(*não consegue)
			//adquire lock de 0						|	
					|								|
			synchronized(1){}				synchronized(i=0){}
				(consegue)						(não consegue)	
			//adquire lock de 1						|		
					|								|
				 debito 							|
				 	|						synchronized(i=0){}
				 credito						(não consegue)
				 	|								|
			//liberta o lock de 1					|
			//liberta o lock de 0					|
											synchronized(0){}
												(consegue)
											//adquire lock de 0
													|
											synchronized(1){}
												(consegue)
											//adquire lock de 1													|
													|
												   ...
*a thread 1 é a que ganha a exclusão mútua porque eu assim escolhi;
poderia ser a thread 2 a ganhá-la primeiro!

*/


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

		synchronized(conta[min]) {
			synchronized(conta[max]) {
				conta[contaOrig].debito(valor);
				conta[contaDest].credito(valor);
			}
		}
	}

	/*SOLUÇÃO ERRADA COM DEADLOCK A OCORRER
	public void transfere(int contaOrig, int contaDest, 
										double valor){	
		synchronized(conta[contaOrig]) {
			synchronized(conta[contaDest]) {
				conta[contaOrig].debito(valor);
				conta[contaDest].credito(valor);
			}
		}
	}*/

}

class Conta{
	double saldo;

	Conta(double saldo){
		this.saldo = saldo;
	}

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

