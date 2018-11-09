//comentar
class Counter{
	int j;

	Counter(int j){
		this.j = j;
	}

	public int getJ(){
		return this.j;
	}

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

/*RESULTADO: 
*/


/*DÚVIDAS:
-> 
R.: 
*/