/*Este programa já faz algo diferente dos outros 3. Cria duas threads e 
põem ambas a correr ao mesmo tempo o código de uma mesma instância, ou 
seja, ambas as threads vão correr o método run independentemente. Visto 
que nesse método (run()) estamos a alterar uma variável da instância 
partilhada por ambas as threads, pode acontecer que as duas imprimam o 
mesmo valor e só depois o modifiquem, pode acontecer que uma imprima o valor
e o modifique antes da outra imprimir o valor (isto fará com que imprimam 
valores diferentes), etc.*/
public class Helloworld3 implements Runnable {
	
	//variável que é modificada;
	int n;
	
	//método run que é corrido pelas threads;
	public void run(){
		//impressão do valor do n; 
		System.out.println(n);
		//alteração do valor do n após a impressão;
		this.set(111);
	}

	//construtor;
	Helloworld3(int a) {
		n=a;
	}
	
	//método que altera o valor de n;	
	public void set(int b) { 
		n=b; 
	}
	
	//main;
	public static void main(String args[]) {
		//criação da instância, que imediatamente atribui um valor a n;
		Helloworld3 r=new Helloworld3(222);
		//criação das threads que correrão código da instância r;
		Thread t1=new Thread(r);
		Thread t2=new Thread(r);
		//a main thread imprime o "Antes";
		System.out.println("Antes");
		//dá-mos autorização às threads para iniciarem a sua atividade; 
		t1.start();
		t2.start();
		//a main thread imprime o "Depois";
		System.out.println("Depois");
		try {
			//obriga a main thread a esperar pela thread 2 (t2);
			t2.join();
			//obriga a main thread a esperar pela thread 1 (t1);
			t1.join();
		} catch (InterruptedException e) {}
		/*esta impressão virá sempre no final de todas as outras pois
		neste ponto já só existe a main thread*/
		System.out.println("Fim do programa");
	}
}