import java.util.Arrays;

class BoundedBuffer{
		
	private int[] values;
	
	int poswrite;

	public BoundedBuffer(int size){
		values = new int[size];
		this.poswrite = 0;
	}

	public synchronized void put(int v) throws InterruptedException{
		while(this.poswrite >= this.values.length){
			//System.out.println("poswrite >= this.values.length " +this.poswrite+ ">=" +this.values.length);
			this.wait();
			//System.out.println("poswrite >= this.values.length " +this.poswrite+ ">=" +this.values.length);
		}

		//System.out.println("O buffer antes: " + Arrays.toString(this.values));

		this.values[poswrite] = v;

		poswrite++;
		
		this.notifyAll();

		System.out.println("O produtor pos o "+ v + " no buffer.");
		//System.out.println("O buffer depois: " + Arrays.toString(this.values));
		//System.out.println(" ");
	}

	public synchronized int get() throws InterruptedException{
		
		while(poswrite == 0){
			this.wait();
		}

		//System.out.println("O buffer antes: " + Arrays.toString(this.values));

		this.poswrite-- ;

		this.notifyAll();

		System.out.println("O consumidor tirou o " + this.values[poswrite] + " do buffer.");
		//System.out.println("O buffer depois: " + Arrays.toString(this.values));
		//System.out.println(" ");

		return this.values[poswrite];
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

		Consumidor c = new Consumidor(bf);

		Produtor p = new Produtor(bf);

		c.start();

		p.start();

		try{
			c.join();
			p.join();
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