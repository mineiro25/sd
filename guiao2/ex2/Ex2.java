class Conta{
	private int saldo;

	Conta(int saldo){
		this.saldo = saldo;
	}

	public int getSaldo(){
		return this.saldo;
	}

	public void setSaldo(int saldo){
		this.saldo = saldo;
	}
}

class Banco{

	Conta c = new Conta(0);

	public void consulta(){
		return c.getSaldo();
	}

	public void credito(int c){
		c.setSaldo(c.getSaldo()+c);
	}

	public void debito(int d){
		c.setSaldo(c.getSaldo()+d);
	}

}

class ClienteThd extends Thread{
	Banco b = new Banco();
	public void run(){
		for(int i=0; i<1000; i++)
			b.credito(5);
	}
}

class Ex2{
	public static void main(){
		ClienteThd t1 = new ClienteThd();
		ClienteThd t2 = new ClienteThd();
	}
}

