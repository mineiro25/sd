/*Neste exercício temos que garantir que o saldo final na conta 0 é de 
0€, pois é suposto o cliente 1 creditar-lhe 5000€(1000*5€) e o cliente
2 debitar-lhe 5000€(1000*5€), sendo ambas as operações efetuadas em 
concorrência, obviamente. 

Para garantirmos que isto acontece, visto que temos concorrência, temos que 
fazer o mesmo que fizemos para o exercício 1 deste guião, que basicamente 
é garantir que nenhum daqueles incrementos/decrementos de 5€ são perdidos.
Para que isto aconteça, já sabemos, temos que aplicar exclusão mútua a cada
um dos métodos que alterem/consultem valores da conta 0, pois vimos 
anteriormente que enquanto uma thread está a alterar/consultar, outra
pode também estar a consultar/alterar, o que pode gerar conflitos.

Posto isto, vamos criar uma classe banco cuja variável de instância é um
array de doubles, em que cada posição corresponde a uma conta e o seu 
conteúdo, ao saldo dessa mesma conta. Nesta classe vamos criar os métodos 
pedidos, aplicando a cada um exclusão mútua, pois todos eles operam sobre
o array, e ter um deles a ser executado ao mesmo tempo que outro poderá 
originar os tais conflitos já falados. 

Criamos duas outras classes, subclasses de thread, ou seja, as suas 
instâncias serão threads. Uma irá ter no método run um ciclo que executa
o método crédito (de 5€) na conta 0, 1000 vezes, outra irá ter no método 
run um ciclo que executa 1000 vezes o método débito (de 5€), também na 
conta 0.

Por fim criamos uma class main onde iremos instanciar uma vez a classe 
banco, instanciar duas vezes a classe das threads e "dar-lhes vida". 

Se não tivermos nenhum erro, o saldo final da conta 0 será 0€!*/
class Banco{

	//um array de inteiros com o saldo de 10 contas
	double saldos[] = new double[10]; 

	Banco(){
		for(int i=0; i<10; i++)
			this.saldos[i]=0;
	}

	public synchronized double consulta(int conta){
		return saldos[conta];
	}

	public synchronized void credito(int conta, double c){
		saldos[conta] += c; 
	}

	public synchronized void debito(int conta, double d){
		saldos[conta] -= d;
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

