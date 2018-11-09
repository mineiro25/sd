/*Este exercício é igual ao anterior. Apenas nos é pedido que em vez de 
incrementar com um método façamos o incremento diretamente na variável. O
professor disse que a única diferença seria no tempo de execução que pode
ser verificado com time -p java Ex2b n i */
class Counter{
	public int j;

	Counter(int j){
		this.j = j;
	}

	public int getJ(){
		return this.j;
	}

}

class Thd extends Thread{
	int i;

	Counter c;

	Thd(int i, Counter c){
		this.i = i;
		this.c = c;
	}

	public void run(){
		//System.out.println(Thread.currentThread().getName()+"-->"+c.getJ());
		for(int a=0; a<this.i; a++){
			c.j++;
			//System.out.println(Thread.currentThread().getName()+"-->"+c.getJ());
		}
	}

}

class Ex2b{
	
	public static void main(String args[]){

		int n = Integer.parseInt(args[0]);

		int i = Integer.parseInt(args[1]); 

		Counter c = new Counter(0);

		Thread t[] = new Thread[n];

		for(int j=0; j<n; j++){
			t[j] = new Thd(i, c);
			t[j].start(); 
		}

		try{
			for(int j=0; j<n; j++)
				t[j].join();
		}
		catch(InterruptedException e){}
	
		System.out.println(Thread.currentThread().getName()+"-->"+c.getJ());

	}

}

/*Resultado:

POR AQUI O RESULTADO DOS TEMPOS DE EXECUÇÃO PARA O Ex2b E PARA O Ex2b

*/

/*DÚVIDAS:
-> Como é que o pomos a incrementar diretamente a variável? Eu acho que
incrementei bem e não noto descrepância nenhuma no tempo. É suposto?
R.: 
*/