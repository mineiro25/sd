/*Neste exercício, é pretendido que façamos o mesmo que no exercicio 2
do primeiro guião mas desta vez aplicando exclusão mútua. Exclusão mútua 
é uma propriedade que garante que dois processos (threads) não acedem
em simultâneo a um recurso que é partilhado por ambas mas não pode ser
modificado ao mesmo tempo. Para isso, escrevemos synchronized no método
que queremos que seja sempre executado por uma thread apenas, não podendo
as outras acede-lo enquanto essa o estiver a executar.*/
class Counter{
	int j;

	Counter(int j){
		this.j = j;
	}

	public int getJ(){
		return this.j;
	}

	//mecanismo synchronized para aplicar exclusão mútua 
	public synchronized void increment(){
		this.j++;
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
		System.out.println(Thread.currentThread().getName()+"-->"+c.getJ());
		for(int a=0; a<this.i; a++){
			System.out.println(Thread.currentThread().getName()+"-->"+c.getJ());
			c.increment();
		}
	}

}

class Ex1{
	
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

/*RESULTADO: o resultado final que é impresso é aquele que era suposto, 
isto é, n*i, pois agora não há "desperdício" de incrementos.*/


/*DÚVIDAS:
-> 
R.: 
*/