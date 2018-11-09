class Contador{
	public int v = 0;
	synchronized public void inc() {v++;}
	public int get() {return v;}  
}

class MinhaThread extends Thread{
	private int id;
	private Contador c;

	public MinhaThread(int id, Contador c){
		this.id = id;
		this.c = c;
	}
	public void run(){
		for(int j = 1; j<=100000; j++){
			//System.out.println(Thread.currentThread.getName() + "-->" + c.get());
			c.inc();
		}
	}
}

class Ex2{
	static public void main(String[] args) throws Exception{
		Contador c = new Contador();
		MinhaThread[] mt = new MinhaThread[10];
		for(int i = 0; i!=10; i++){
			mt[i] = new MinhaThread(1, c);
			mt[i].start();
		}
		for(int i = 0; i != 10; i++)
			mt[i].join();
		System.out.println(c.get());
	}
}


/*Alterando uma coisa no código consegimos imediatamente corrigir o 
programa. Isto é introduzindo o synchronized antes do "public void inc()"*/

/*time -p java Ex2 -> para obtermos os tempos do programa*/
