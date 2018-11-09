/*este programa cria uma thread, uma instância de Helloworld e poẽ a thread 
a executar o código dentro de run()*/
/*este objeto tem um método run(); a thread que correr este objeto fará 
tudo e apenas o que estiver nesse método (no run())*/
class Helloworld implements Runnable {

	public void run(){
		System.out.println("I am a thread");
	}

	static public void main(String[] args){
		Helloworld i = new Helloworld();
		//tenho que passar à thread o objeto que tem o método run
		Thread t = new Thread(i);
		/*só quando dizemos à thread para ela começar a executar, através 
		do start(), é que ela irá fazer o que está dentro do run()*/
		t.start();
	}  
}