import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;


/*Neste guião, a ideia é reduzir ao máximo a parte do código que está a 
ser abrangida pela secção critica, para que, os locks de cada instancia 
sejam utilizados apenas quando sao mesmo necessarios. Há situações em
que estamos a utilizar o lock de banco e dentro do lock de banco temos 
outro lock de conta. Até agora só podiamos ter locks aninhados; o que 
se vai explorar neste guião é uma nova forma de usar locks, mais liberal.
Isto é, a partir de agora, com os locks explícitos, vamos puder fazer 
os locks da seguinte forma:
		
		~~~~~~~~~~~~
		lockBanco.lock();
		~~~~~~~~~~~~
		lockConta.lock();
		~~~~~~~~~~~~
		lockBanco.unlock();
		~~~~~~~~~~~~
		lockConta.unlock();
		~~~~~~~~~~~

ou seja pudemos enterlaçar os locks, algo que dantes não era possível. 
Isto, por exemplo, após termos feito um lock do banco e 
posteriormente, o lock de uma conta, permite-nos fazer o unlock do banco
sem termos que fazer o unlock da conta primeiro! É uma situação bem 
real e que nos liberta o banco caso já não estejamos a precisar dele. Tal
não seria possível caso conhecessemos apenas o mecanismo syncrhonized's 
em bloco que falamos anteriormente*/
class Conta{

	Integer id;

	double saldo;

	private ReentrantLock lock = new ReentrantLock();

	Conta(Integer id, double saldo){
		this.id = id; 
		this.saldo = saldo;;
	}

	public double consultar(){
		this.lock.lock();
		double saldo = this.saldo;
		this.lock.unlock();
		return saldo;
	}

	public void credito(double valor){
		this.lock.lock();
		this.saldo += valor;
		this.lock.unlock();
	}

	public void debito(double valor) throws SaldoInsuficiente{
		if((this.saldo - valor) < 0){
			String msg = "O saldo da conta " + 
								this.id + " não pode ser negativo.";
			throw new SaldoInsuficiente(msg);
		}
		this.lock.lock();
		this.saldo -= valor;
		this.lock.unlock();
	}

	public void contaLock(){
		this.lock.lock();
	}

	public void contaUnlock(){
		this.lock.unlock();
	}

}

class ContaInvalida extends Exception{
	ContaInvalida(String msg){
		super(msg);
	}
}

class SaldoInsuficiente extends Exception{
	SaldoInsuficiente (String msg){
		super(msg);
	}
}


class Banco{

	private HashMap<Integer,Conta> contas;

	private int novoID;

	private ReentrantLock lock = new ReentrantLock();

	public void transfere(Integer idOrigem, Integer idDestino, 
				double valor) throws ContaInvalida, SaldoInsuficiente{
		int min = Math.min(idOrigem, idDestino);
		int max = Math.max(idOrigem, idDestino);

		this.contas.get(min).contaLock();
		this.contas.get(max).contaLock();
		try{
			this.contas.get(idOrigem).debito(valor);
			this.contas.get(idDestino).credito(valor);
		}catch(SaldoInsuficiente e){
			System.out.println(e.getMessage());
			System.out.println("Transferência não realizada.");
		}
		this.contas.get(max).contaUnlock();
		this.contas.get(min).contaUnlock();

	}


	/*quando se cria uma conta, teremos de cria-la dentro da seccao
	critica pois nao podemos ter mais nenhuma thread a criar uma conta;
	caso tivessemos, iriamos correr o risco de perder incrementos do id, 
	aquele problema que vimos no inicio dos guioes*/
	public Integer criarConta(double saldoInicial) 
												throws SaldoInsuficiente {
		if(saldoInicial < 0){
			String msg = "O saldo da conta não pode ser negativo." ;
			throw new SaldoInsuficiente(msg);
		}
		/*é a partir deste momento que queremos fazer o lock de banco, pois
		é a partir deste momento que teremos a criacao de um novo id que
		pode apenas estar a ser acedido por uma thread*/		
		lock.lock();
		Integer id = this.novoID++;
		Conta c = new Conta(id, saldoInicial);
		this.contas.put(id, c);
		lock.unlock();
		/*o unlock do banco é feito aqui porque as operações sobre o map
		também têm que ser protegidas*/
		return id;
	}

	public double fecharConta(Integer id) throws ContaInvalida{
		/*o lock de banco tem que começar aqui! porque? Imaginemos 2 
		threads, que ao mesmo tempo verificam a existencia do id que é
		passado às funções fecharConta() e consultar() (neste programa nao 
		temos a verificacao para consultar()) que cada uma executa. Imaginemos
		que a thread1 a seguir a esta verificação decide fechar essa mesma
		conta, enquanto que a thread 2, ao mesmo tempo que isto acontece, 
		não  pode fazer nada (supondo que poríamos o lock depois do if) 
		após ter verificado a existencia daquela conta. Ora tendo a thread
		1 acabado o seu "serviço" e libertado o lock, a thread 2 adquire
		o lock do banco e procede à consulta do saldo da conta. Obviamente
		o resultado é um null pointer exception por estar a aceder a um
		elemento do hashmap que nao existe!*/
		lock.lock();
		if(!(contas.containsKey(id))){
			throw new ContaInvalida("A conta nao existe: ");
		}
		/*e se ja temos o lock de banco, porque é que temos que fazer um 
		lock de conta?? É trivial perceber que nem todas as threads fazem
		operações ao nivel do banco. Há threads que simplesmente estão a 
		fazer operações ao nível das contas. Imaginemos entao que temos uma
		thread a fechar uma conta e outra a fazer um deposito nessa conta. 
		Seria perfeitamente possível que durante a operação de deposito
		a conta deixasse de existir porque estamos a permitir a duas threads 
		que façam alterações nessa conta! Por esse motivo, o método do fecho
		da conta necessita de um lock na conta tal como o método depositar
		já o tem!

		nota: como a conta vai ser removida e eliminada antes de fazermos 
		unlock, temos que guardar a referencia dessa conta e fazer o lock
		para a referencia dessa conta, para que depois de eliminada, 
		possamos fazer unlock dessa referencia.
		*/
		Conta c = this.contas.get(id);
		c.contaLock();
		double saldo = c.consultar();
		this.contas.remove(id);
		c.contaUnlock();
		lock.unlock();
		return saldo;
	}

	/*para esta operação vamos assumir que a consulta do saldo de cada
	conta demora 5 segundos! Por esse motivo vamos primeiro verificar 
	se as contas existem e vamos tentar restringir ao máximo a secção 
	critica tanto para o banco como paras as contas*/
	public double saldoNContas(Integer ids []){
		/*nao é preciso que o lock abranja as 2 variáveis a seguir, visto
		que sao variáveis do metodo que está a ser acedido pela thread*/
		double saldoTotal = 0;
		ArrayList<Integer> contasLocked = new ArrayList(ids.length);
		/*começamos o lock aqui antes do for pois nao queremos que uma 
		conta, após verificada a sua existência, possa ser eliminada por 
		outra thread, por exemplo!*/
		lock.lock();
		for(int i = 0; i < ids.length; i++){
			if(this.contas.containsKey(ids[i])){
				/*se a conta existir é feito um lock sobre essa conta 
				e é adicionada a um array*/
				this.contas.get(ids[i]).contaLock();
				contasLocked.add(ids[i]);		
			}
		}
		/*até agora, o que fizemos, foi obter os locks das contas para 
		podermos libertar o lock de banco, visto que nao queremos que o 
		banco fique bloqueado 5*N segundos só para fazer a consulta dos
		saldos daquelas contas! Visto que também nao vamos fazer operações
		sobre o map está mais do que visto que podemos libertar o lock de 
		banco!*/
		lock.unlock();
		/*vamos então percorrer as n contas consultar e a seguir somar os 
		seus saldos ao saldo total. Somos capazes de ver que ao mesmo 
		tempo que isto acontece, o banco pode estar a realizar outras 
		operações, isto graças ao lock enterlaçado que podemos agora
		fazer! Assim que temos o saldo dessa conta somado, podemos
		imediatamente libertar essa conta.*/
		for(int id : contasLocked){
			saldoTotal = this.contas.get(id).consultar();
			this.contas.get(id).contaUnlock();
		}
		return saldoTotal;
	}

	Banco(){
		contas = new HashMap<Integer,Conta>();
		novoID = 0;
	}

	public HashMap<Integer,Conta> getContas(){
		return this.contas;
	}

}


class Cliente1 extends Thread{
	private Banco banco;

	public void run(){
		double saldo1 = 0;
		double saldo2 = 5;
		Integer idOrigem = 0;
		Integer idDestino = 2;
		Integer nContas [] = new Integer[]{0,1,2};
		try{
			banco.criarConta(saldo1);
			banco.transfere(idOrigem, idDestino, saldo2);
			System.out
				.println("C1 -> Soma saldos das contas 0, 1 e 2: " + 
					banco.saldoNContas(nContas));
		}catch(SaldoInsuficiente | ContaInvalida e){
			System.out.println(e.getMessage());
		}
	}

	Cliente1(Banco banco){
		this.banco = banco;
	}
}


class Cliente2 extends Thread{
	private Banco banco;

	public void run(){
		Integer idOrigem = 0; 
		Integer idDestino = 1;
		double saldo = 10;
		try{
			banco.transfere(idOrigem,idDestino,saldo);
			banco.fecharConta(idDestino);
			System.out
				.println("C2 -> Saldo conta "+idOrigem+ ": " + banco
												.getContas()
													.get(idOrigem)
														.consultar());
		}catch(ContaInvalida | SaldoInsuficiente e){//se na funcao fecharConta for enviada uma exception, esta, é apanhada aqui
			System.out.println(e.getMessage());
		}
	}

	Cliente2(Banco banco){
		this.banco = banco;
	}
}

class Ex1{

	public static void main(String args[]) throws SaldoInsuficiente{
		Banco b = new Banco();
		double saldo = 10;
		for(int i = 0; i<2; i++){
			try{
				b.criarConta(saldo);
			}catch(SaldoInsuficiente e){
				System.out.println(e.getMessage() + saldo);
			}
		}

		Cliente1 c1 = new Cliente1(b);
		c1.start();
		Cliente2 c2 = new Cliente2(b);
		c2.start();

		try{
			c1.join();
			c2.join();
		}
		catch(InterruptedException e){}

	System.out.println("Saldo da conta 0: " + b.getContas().get(0)
												.consultar());
	//System.out.println("Saldo da conta 1: " + b.getContas().get(1)
	//												.consultar());
	System.out.println("Saldo da conta 2: " + b.getContas().get(2)
												.consultar());
	}

}


/*
Dúvidas:
-> É suposto dar o erro ...
 " Exception in thread "Thread-1" java.lang.NullPointerException
	at Banco.transfere(Ex1.java:74)
	at Cliente2.run(Ex1.java:136) "  ... quando pedimos uma conta inválida?
Supostamente estamos a pedir a impressão de uma mensagem e essa mensagem 
não está a aparecer...
R.:

*/