import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class BoundedBuffer{
		
	private int[] values;
	
	int poswrite;

	ReentrantLock lock;

	Condition isEmpty;

	Condition isFull;

	public BoundedBuffer(int size){
		this.values = new int[size];
		this.poswrite = 0;
		this.lock = new ReentrantLock();
		this.isEmpty = this.lock.newCondition();
		this.isFull = this.lock.newCondition();
	}

	public void put(int v) throws InterruptedException{
		this.lock.lock();
		while(this.poswrite >= this.values.length){
			//System.out.println("poswrite >= this.values.length " +this.poswrite+ ">=" +this.values.length);
			/*as threads que chegarem a este await, apenas irão acordar
			quando lhes for enviado um sinal que está associado à mesma 
			variável de condição que este await (isFull)*/
			this.isFull.await();
			//System.out.println("poswrite >= this.values.length " +this.poswrite+ ">=" +this.values.length);
		}

		//System.out.println("O buffer antes: " + Arrays.toString(this.values));

		this.values[poswrite] = v;

		poswrite++;
		
		this.isEmpty.signal();

		System.out.println("O produtor pos o "+ v + " no buffer.");
		//System.out.println("O buffer depois: " + Arrays.toString(this.values));
		//System.out.println(" ");
		this.lock.unlock();
	}

	public synchronized int get() throws InterruptedException{
		this.lock.lock();
		while(poswrite == 0){
			this.isEmpty.await();
		}

		//System.out.println("O buffer antes: " + Arrays.toString(this.values));

		this.poswrite-- ;
		int r = this.values[poswrite];
		this.isFull.signal();

		System.out.println("O consumidor tirou o " + this.values[poswrite] + " do buffer.");
		//System.out.println("O buffer depois: " + Arrays.toString(this.values));
		//System.out.println(" ");
		
		this.lock.unlock();

		return r;
	}

	public int[] getValues() {
    	return values;
	}
}

class Consumidor extends Thread{
	private BoundedBuffer buffer;

	public Consumidor(BoundedBuffer buffer){
		this.buffer = buffer;
	}

	public void run(){
		try{
			for(int i = 0; i<20; i++){
				this.buffer.get();
			}
		}catch(InterruptedException e){}
	}
}

class Produtor extends Thread{
	private BoundedBuffer buffer;

	public Produtor(BoundedBuffer buffer){
		this.buffer = buffer;
	}

	public void run(){
		try{
			for(int i = 1; i<=20; i++)
				this.buffer.put(i);
		}catch(InterruptedException e){}
	}
}

class Ex1{
	public static void main(String[] args){
		BoundedBuffer bf = new BoundedBuffer(10);


		Consumidor c1 = new Consumidor(bf);
		Consumidor c2 = new Consumidor(bf);
		Consumidor c3 = new Consumidor(bf);
		Consumidor c4 = new Consumidor(bf);


		Produtor p1 = new Produtor(bf);
		Produtor p2 = new Produtor(bf);
		Produtor p3 = new Produtor(bf);
		Produtor p4 = new Produtor(bf);


		c1.start();
		c2.start();
		c3.start();
		c4.start();

		p1.start();
		p2.start();
		p3.start();
		p4.start();

		try{
			c1.join();
			c2.join();
			c3.join();
			c4.join();

			p1.join();
			p2.join();
			p3.join();
			p4.join();

		}catch(InterruptedException e){}
	}
}

/*
É suposto o produtor echer o buffer todo e só depois é que o consumidor
começa a tirar de la tudo ou é mero acaso?
R.: Está a acontecer muitas vezes, talves por causa dos printlns, mas se
os comentar-mos, como se encontram agora, e corrermos várias vezes, quase
de certeza que verificamos o que é suposto: o consumidor e o produtor, a 
tirar e a por, respetivamente, de forma aleatoria, ou seja, o produtor, 
poe cinco nrs o consumidor tira dois nrs, o produtor volta a por um nr, 
etc...
*/