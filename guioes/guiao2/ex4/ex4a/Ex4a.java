import java.util.concurrent.TimeUnit;

//mal!! eliminar cenas nos comentários!!


/*Neste exercício, o objetivo é perceber que em exercícios anteriores, 
sempre que utilizávamos o synchronized, era feito um lock ao nível da 
classe Banco, ou seja, quaisquer operações que quisessemos executar em 
paralelo, mesmo que em contas diferentes, não eram permitidas, pois a 
partir do momento que uma thread executa um método synchronized, qualquer
outra fica impedida de executar o que quer que seja (pois todos os outros
métodos das classe são synchronized) (pois a execução de qualquer método 
que envolva qualquer alteração/consulta das variáveis daquela instância
está bloqueada). Por exemplo, um cliente que queira debitar 5 euros da 
conta 0, não o pode fazer se já existir outro cliente a fazer outra 
operação naquele banco, como por exemplo, creditar 5 euros da conta 5.
É este problema que este exercício nos quer mostrar.


Para o resolver, o que é que vamos fazer? Vamos fazer com que as threads
adquiram o lock para algo mais pequeno e específico, pois até agora 
estávamos a restringir uma grande quantidade de variáveis que não estávamos
sequer a usar. Para isso vamos criar uma classe Conta, e nela vamos 
implementar as operações credito, debito e consultar, ambas usarão exclusão 
mútua.

Na classe banco, vamos criar várias instâncias de Conta, para que depois
possamos fazer cenas com elas. Vamos também ter o método transferir!

Na classe main instancia-se o banco e os clientes (threads).*/

/* Versão com locks ao nível da conta; para ver o tempo que demora e, 
posteriormente, comparar com a função abaixo utilizar "time -p java Ex4a
Ambos os programas vao ter duas threads; uma credita 1000 vezes 5€ na 
conta 0 e outra credita 1000 vezes 5€ na conta 1.*/

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

	public synchronized void transfere(int contaOrig, int contaDest, 
										double valor){	
		conta[contaOrig].debito(valor);
		conta[contaDest].credito(valor);
	}

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
		for(int i=0; i<1000000; i++)
			b.credito(0, 5);
	}
}

class ClienteThd2 extends Thread{
	Banco b; 

	ClienteThd2(Banco b){
		this.b = b;
	}

	public void run(){		
		for(int i=0; i<1000000; i++)
			b.credito(1, 5);
	}
}

class Ex4a{
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

/*Versão com locks ao nível do banco(ex3); para ver o tempo que demora e 
posteriormente, comparar com a função acima utilizar "time -p java Ex4a".
Ambos os programas vao ter duas threads; uma credita 1000 vezes 5€ na 
conta 0 e outra credita 1000 vezes 5€ na conta 1.*/
/*
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
		for(int i=0; i<1000000; i++)
			b.credito(0, 5);
	}
}

class ClienteThd2 extends Thread{
	Banco b; 

	ClienteThd2(Banco b){
		this.b = b;
	}

	public void run(){		
		for(int i=0; i<1000000; i++)
			b.credito(1, 5);
	}
}

class Ex4a{
	public static void main(String args[]){
		Banco b = new Banco(new double[] {0,0,0,0,0,0,0,0,0,0});
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
		System.out.println("Saldo final da conta 0: "+ b.consulta(0));
		System.out.println("Saldo final da conta 1: "+ b.consulta(1));
	}
}*/

/*DÙVIDAS:
-> Nos slides do prof Neves, temos que a classe banco tem que ter os 
métodos depositar, levantar, consultar e transferir. O transferir, 
compreendo, pois as transferências têm que ser feitas entre contas, e só 
o banco é que tem acesso a todas as contas (isto é, não vamos por o 
transferir na classe conta), não obstante para que queremos os outros três
métodos (depositar, levantar e consultar) na classe banco quando podemos 
aceder à conta que queremos e invocar esses métodos?
R.: A resposta é simples. Quem é que vai querer fazer alterações/consultas
nas contas? Os clientes. Como é que os clientes se referem à conta que
querem alterar? Identificando-a com um int. Então se quiser debitar 5 € da
conta 4, o utilizador diz ao banco que quer debitar 5 € da conta 4. O banco
recebe a informação e invoca o método debito da conta 4 que lhe debita 5€.

-> Em vez de estarmos a criar uma nova classe, porque é que não aplicamos 
exclusão mútua para cada posição do array das contas que tinhamos 
nos exercícios anteriores? Não podíamos fazer isso usando o 
synchronized(){}? 
R.: Não. Os tipos nativos não conseguem ter os locks que as instâncias das
classes que nós criamos têm (têm se lhes aplicarmos o synchronized). 

->É suposto conseguirmos ver a diferença de tempo ou é muito pequena? 
R.: Não conseguimos ver o tempo porque estamos a falar de tempos muito
pequenos, senão viamos.
*/