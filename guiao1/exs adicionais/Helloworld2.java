/*este programa cria duas threads, duas instâncias de Helloworld e põe cada
thread a executar o código de cada instância, que neste caso sabemos que
é igual para ambas*/

//ver a explicação do Helloworld.java, é igual
class Helloworld2 implements Runnable {

	public void run(){
		System.out.println(Thread.currentThread().getName());
	}

	static public void main(String[] args){
		Helloworld2 i1 = new Helloworld2();
		Helloworld2 i2 = new Helloworld2();

		Thread t1 = new Thread(i1);
		Thread t2 = new Thread(i2);
		t1.start();
		t2.start();
	}  
}
