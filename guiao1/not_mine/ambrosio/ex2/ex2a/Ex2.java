/*class Counter{

	int k;

	Counter(int k){
		this.k = k;
	}

	synchronized public void increment(){
		this.k++;
	}

	public int getK(){
		return this.k;
	}
}

class Ex2a implements Runnable{

	int i;

	Counter c = new Counter(1);

	public void run(){
		while(c.getK()<=this.i){
			c.increment();
			System.out.println(Thread.currentThread().getName()+"-->"+c.getK());
		}
	}

	Ex2a(int i){
		this.i = i;	
	}

	public static void main(String[] args){
		int n = Integer.parseInt(args[0]);
		Ex2a inst = new Ex2a(Integer.parseInt(args[1]));
		Thread t[] = new Thread[n];
		for(int j=0; j!=n; j++){
			t[j] = new Thread(inst);
			t[j].start();
		}
		try{
			for(int j=0; j!=n; j++)
				t[j].join();			
		}catch(InterruptedException e){}
	}
}*/
/*
class Counter {
	public int val;

	public Counter(){
		val = 0;
	}

	public void increment(){
		val++;
	}

	public int get(){
		return val;
	}
}

class Ex2 implements Runnable{
	public Counter count;

	public void run(){
		for(int i = 0; i < 1000; i++){
			System.out.println(Thread.currentThread().getName() + "-->" + count.get());	
			count.increment();
		}
	}

	Ex2(){
		count = new Counter();
	}

	public void get(){
		System.out.println("Total ---> " + count.get());
	}

	public static void main(String[] args) {
		int nthreads = Integer.parseInt(args[0]);

		Ex2 ex = new Ex2();

		Thread thread [] = new Thread[nthreads];
		for(int k = 0; k < nthreads; k++){
			thread[k] = new Thread(ex);
			thread[k].start();
		}

		try{
			for(int k = 0; k < nthreads; k++)
				thread[k].join();
		} catch(InterruptedException e){}

		ex.get();
	}
}*/

class Counter {
	public int val;

	public Counter(){
		val = 0;
	}

	public void increment(){
		val++;
	}

	public void get(){
		System.out.println(val);
	}
}

class Ex2 implements Runnable{
	public Counter count;

	public void run(){
		for(int i = 0; i < 1000; i++)
			count.increment();
	}

	Ex2(){
		count = new Counter();
	}

	public void get(){
		count.get();
	}

	public static void main(String[] args) {
		int nthreads = Integer.parseInt(args[0]);

		Ex2 ex = new Ex2();

		Thread thread [] = new Thread[nthreads];
		for(int k = 0; k < nthreads; k++){
			thread[k] = new Thread(ex);
			thread[k].start();
		}

		try{
			for(int k = 0; k < nthreads; k++)
				thread[k].join();
		} catch(InterruptedException e){}

		ex.get();
	}
}