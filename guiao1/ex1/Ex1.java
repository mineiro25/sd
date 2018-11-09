/*Este programa cria n threads, que imprimem os naturais até i, cada uma,
conforme o utilizador especifique nos argumentos do executável. É 
suposto criar uma instância por thread, pelo que não haverá sequer 
partilha de variáveis de instância. Cada instância criada é passada à
thread criada. O que acontece é que cada thread assumirá o run 
independentemente e executará o seu código idependentemente do que as 
outras estiverem a executar, não partilhando variáveis, NÃO PARTILHANDO
NADA!*/
class Ex1 implements Runnable{

	/*variável declarada na classe para poder ser acedida por todos 
	os métodos*/
	int i;

	//método que será executado por uma thread
	public void run(){
		/*ciclo onde se vai imprimindo os naturais até i e onde se 
		identifica a thread que faz a impressão*/
		for(int j=1; j<=this.i; j++)
			System.out.println(Thread.currentThread().getName() + "->" + j);
	}
	
	//construtor parametrizado
	Ex1(int i){
		this.i = i;
	}

	//main
	public static void main(String[] args){
		
		/*1º argumento recebido pelo executável (nº de threads); 
		"int y = Integer.parseInt(x)" converte de string para int o x 
		colocando esse valor em y*/;
		int n = Integer.parseInt(args[0]);
		
		/*2º argumento recebido pelo executável (nº de incrementos); 
		"int y = Integer.parseInt(x)" converte de string para int o x 
		colocando esse valor em y*/;
		int i = Integer.parseInt(args[1]);

		/*criação de um array de threads com n posições (isto não 
		cria as threads), cria apenas o array!*/
		Thread t [] = new Thread[n];

		//ciclo que atribui a cada posição do array uma thread e a inicia
		for(int j=0; j!=n; j++){
			/*Estamos a atribuir a cada posição do array, uma thread, 
			e a essa mesma thread, vamos lhe passar uma instância; o
			"new Ex1(i)"" está a criar uma instância de Ex1*/
			t[j] = new Thread(new Ex1(i));
			/*damos "vida" à thread em questão, isto é, iniciámo-la*/
			t[j].start(); 
		}
		
		try{
			//ciclo que obriga a main thread a esperar pela morte das outras;
			for(int j=0; j!=n; j++)
				//main thread espera pela morte da thread t[j];
				t[j].join();
		} 
		catch(InterruptedException e){}
		
	}

}

/*Outra forma de fazer o exercício é colocar a classe Ex1 como subclasse
de thread. Assim, a classe Ex1 consegue herdar todas as características
da classe Thread, que são por exemplo o método start, join, etc. Notemos 
que também podemos chamar o método run pois a class Thread implementa
a interface Runnable!*/
/*class Ex1 extends Thread {

	int i;

	public void run(){
		for(int j=1; j<=this.i; j++)
			System.out.println(Thread.currentThread().getName() + "->" + j);
	}
	
	Ex1(int i){
		this.i = i;
	}

	public static void main(String[] args){
		int n = Integer.parseInt(args[0]);
		int i = Integer.parseInt(args[1]);
		Thread t [] = new Thread[n];
		for(int j=0; j!=n; j++){
			t[j] = new Ex1(i);
			t[j].start(); 
		}
		try{
			for(int j=0; j!=n; j++)
				t[j].join();
		} catch(InterruptedException e){}
	}

}*/

/*DÚVIDAS:
-> Porque é que estamos a dizer às threads para correrem o método run() que 
pertence à mesma instância onde são criadas? O correto não seria criar uma
nova classe, por lá o método run() e pô-las a correr essa instância?
R.: Podemos invocar o método run(), ou numa classe que implemente runnable
ou numa classe que extenda Thread. Acho que para não ficar confusão 
podíamos criar uma nova classe e lá colocar o método run, mas não é algo 
que faça diferença para o caso. Não vai ter problema nenhum fazer o que 
estamos a fazer porque a única coisa em que a thread vai pegar é nas 
variáveis de instância e no método run.

-> Apenas a classe que tem o método run() percisa do implements Runnable?
Ou seja, a classe que tem a main() que cria e inicia as threads não 
necessita do implements Runnable? Precisa do quê exatamente? 
R.: Exato. Ao fazermos Thread t = new Thread() estamos a criar uma 
instância de thread que já tem todos os procedimentos necessários 
que precisamos para fazer operações sobre a thread (como por exemplo, 
o start, o join, etc). Quando referimos t.start(), por exemplo, estamos a 
dizer para se iniciar o método start (que inicia a thread) que pertence 
à instância t. Portanto, para criar threads nao precisamos de nenhum 
extends ou implements na classe que cria as threads.

-> Qual a diferença entre String[] args(um array de strings chamado args) e
Thread t [] (um array de threads chamado t), isto é, porque não se escrevem
da mesma forma?
R.: É equivalente

-> 
R.:
*/
