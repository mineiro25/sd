/*Neste exercício é suposto criar uma instância de Counter, à qual todas 
as threads têm acesso. Essa instância tem uma variável j que deve ser 
incrementada i vezes por cada thread, ou seja, o nº de incrementos total
daquela variável deverá ser i*n (i é o nº de incrementos feitos por uma 
thread, e n é o nº de threads criadas). Neste exercício temos que 
incrementar j através de um método increment. É necessário perceber bem 
o que o enunciado pede, i*n incrementos da variável j de Counter!

É nos também pedido que neste exercício executemos com o método increment 
e que noutro executemos com o incremento feito diretamente na variável; 
isto pretende que nós observemos uma descrepância no tempo de execução 
dos dois programas.*/

//classe que alberga a variável de instância a incrementar
class Counter{
	//variável de instância a incrementar
	int j;

	//construtor parametrizado
	Counter(int j){
		this.j = j;
	}

	//get de j
	public int getJ(){
		return this.j;
	}

	//método que incrementa j
	public void increment(){
		this.j++;
	}
}

/*A classe Thd, que extende Thread (ou seja, que herda todas as 
"funcionalidades" e "propriedades" de thread) tem o código que as threads 
criadas devem correr (método run). Além disso, tem duas variáveis que serão
"inicilizadas" quando forem criadas novas threads (que é o mesmo que dizer: 
"quando forem criadas novas instâncias de Thd"). Quando for criada uma
instância de Thd, vamos-lhe passar um inteiro, que é o nº de incrementos 
que a thread tem que fazer e uma instância de Counter, a tal instância 
que será acedida por todas as threads. Posto isto, cada thread criada, 
vai, exatamente como nos mostra o run(), imprimir o valor que encontra na 
variável da instância(da primeira vez que lhe acede) e, em loop, aceder 
à variável da instância, incrementá-la i vezes e posteriormente imprimir
o valor com que ficou*/
class Thd extends Thread{
	/*variável de instância correspondente aos incrementos que a thread
	deve fazer*/ 
	int i;

	//variável que apontará a instância que será acedida pela thread
	Counter c;

	//construtor parametrizado
	Thd(int i, Counter c){
		this.i = i;
		this.c = c;
	}

	//método que é executado pela thread
	public void run(){
		/*impressão do valor da variável da instância que é encontrado 
		pela thread na primeira vez que esta acede à instância*/
		//System.out.println(Thread.currentThread().getName()+"-->"+c.getJ());
		//loop responsável pelos i incrementos
		for(int a=0; a<this.i; a++){
			//incremento da variável da instância 
			c.increment();
			/*impressão do valor da variável da instância; CUIDADO COM ESTE
			SYSTEM.OUT.PRINTLN  --->  se o incluirmos no código vai fazer 
			com que o resultado final seja totalmente diferente porque vai
			atrasar o programa dando tempo às threads para fazerem o que
			não é suposto. Este System.out serve apenas para verificarmos 
			que as threads estão a incrementar. Não "descomentar".*/
			//System.out.println(Thread.currentThread().getName()+"-->"+c.getJ());
		}
	}

}

/*Desta vez decidi que devia fazer uma nova classe para lá colocar o 
método run e deixar esta livre dele. Esta class serve apenas para 
inicializar o programa.*/
class Ex2a{
	
	/*Na main, vai-se buscar os argumentos especificados no executável 
	(nº threads e nº incrementos), cria-se a instância que vai ser
	acedida pelas n threads, cria-se o array de threads, criam-se e 
	iniciam-se as threads*/
	public static void main(String args[]){

		int n = Integer.parseInt(args[0]);

		int i = Integer.parseInt(args[1]); 

		/*criação da instância que será acedida pelas threads que terá
		a sua variável inicializada a 1*/ 
		Counter c = new Counter(0);

		//criação do array das threads
		Thread t[] = new Thread[n];

		//criação das n threads
		for(int j=0; j<n; j++){
			/*a cada thread passaremos o nº de incrementos que ela terá
			que fazer e a instância inicializada em cima*/
			t[j] = new Thd(i, c);
			t[j].start(); 
		}

		try{
			for(int j=0; j<n; j++)
				t[j].join();
		}
		catch(InterruptedException e){}
	
		//impressão do valor final com que ficou a variável da instância
		System.out.println(Thread.currentThread().getName()+"-->"+c.getJ());

	}

}

/*RESULTADO: Se fizermos aquilo que o exercício 3 nos pede, 
verificamos que o número de incrementos da variável da
instância de Counter não é n*i, é menor! Isto acontece porque de vez
em quando existe mais do que uma thread a aceder ao método de increment
ao mesmo tempo! O que é que isto origina? Por exemplo, podemos ter 
uma thread 1 que ao aceder ao método increment**, lê a variável, e logo
de seguida uma outra thread 2 acede tbm ao método increment**, lendo, 
somando e guardando o valor na variável, continuando depois a thread 1
a fazer a soma e a guardar o valor na variável. Ora, com este exemplo
somos capazes de ver que a thread 1 deveria ter acabado o processo todo 
de uma vez, mas a thread 2 conseguiu ser mais rápida e fez todo o processo
de uma assentada, alterando o valor da variável. Neste momento, a thread 1
deveria ter o valor que a thread 2 colocou na variável, mas na verdade a
thread 1 já tinha feito a leitura e por esse motivo fara um incremento 
que originará o mesmo valor que a thread 2 originou. Posto isto, nesta 
situação temos um incremento que é feito em vão. Vejamos a figura em baixo:


Imaginemos que a variável do counter está a 10:
					
					    Counter = 10
					
			   t1						 t2
				|                         |
				|                         |
				| Read 10                 |
				|                         |
				|                         |Read 10
				|                         |SOMA 1
				|                         |Write 11
				|                         |
				|SOMA 1                   |
				|Write 11                 |
				|                         |
				|                         |
				|                         |
				|       Counter = 11      |
				|                         |
				|                         |
				v                         v

Esta situação vai-se repetir porque vão haver várias threads a fazer 
o increment ao mesmo tempo e isto acontece várias vezes. Então, por 
exemplo, fazer "java Ex2a 5 1000" devia dar 5000 mas irá dar algo, como 
por exemplo, 4532, devido aos incrementos falhados.

**vamos assumir que aceder ao método increment é ler o valor da variável,
fazer a soma de uma unidade, e guardar esse valor de novo na variável. 
*/


/*DÚVIDAS:
-> As threads partilham o método run? 
R.: Não. Cada thread, executa o método run independentemente do que 
outras threads fazem, ou seja, tudo que diga respeito ao método run 
que está a ser executado por uma thread é conhecido apenas por essa 
thread, não havendo interferência de outras. As únicas cenas que podem 
ser partilhadas e modificadas por várias threads são as variáveis da 
instância sobre a qual elas operam.   
*/