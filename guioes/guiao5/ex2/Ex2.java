import java.util.*;
import java.util.concurrent.locks.*;

class Item{
	int quantity;

	ReentrantLock itemLock;

	Condition isEmpty;

	Item(int quantity){
		this.quantity = quantity;
		this.itemLock = new ReentrantLock();
		this.isEmpty = this.itemLock.newCondition();
	}

	public void supply(int quantity){
		
		this.itemLock.lock();
		this.quantity += quantity;
		System.out.println("Após reabastecimento: " + this.quantity);
		this.isEmpty.signal();
		this.itemLock.unlock();
	}

	public void consume(){
		this.itemLock.lock();
		try{
			if (this.quantity <= 0)
				this.isEmpty.await();
		}catch(InterruptedException e){}
		this.quantity--;
		System.out.println("Após consumo: " + this.quantity);
		this.itemLock.unlock();
	}
}

class Warehouse{
	private HashMap<String, Item> stock;

	Warehouse(){
		this.stock = new HashMap<String, Item>();
	}

	public void supply(String item, int quantity){
		if(this.stock.containsKey(item))
			this.stock.get(item).supply(quantity);
		else{
			Item newItem = new Item(quantity);
			this.stock.put(item, newItem);
		}
	}

	public void consume(String[] items){
		for(int i = 0; i < items.length; i++){
			if(this.stock.containsKey(items[i]))
				this.stock.get(items[i]).consume();
			else
				System.out.println("The item " + items[i] + " doesn't exist.");
		}
	}

	public void setStock(String nome, Item item){
		this.stock.put(nome, item);
	}	

	public HashMap getStock(){
		return this.stock;
	}
}

class Producer extends Thread{
	Warehouse warehouse;

	Producer(Warehouse warehouse){
		this.warehouse = warehouse;
	}

	public void run(){
		try{
			sleep(3000);
			this.warehouse.supply("avião", 1);
			sleep(3000);
			this.warehouse.supply("guitarra", 1);
			sleep(3000);
			this.warehouse.supply("squirtle", 1);
		}catch(InterruptedException e){}
	}
}

class Consumer extends Thread{
	Warehouse warehouse;

	Consumer(Warehouse warehouse){
		this.warehouse = warehouse;
	}

	public void run(){
		this.warehouse.consume(
							new String [] {"avião","guitarra","squirtle"});
	}
}

class Ex2{
	public static void main(String[] args){
		Warehouse warehouse = new Warehouse();
		warehouse.setStock("avião", new Item(0));
		warehouse.setStock("guitarra", new Item(0));
		warehouse.setStock("squirtle", new Item(0));

		Producer p1 = new Producer(warehouse);
		Consumer c1 = new Consumer(warehouse);

		p1.start();
		c1.start();

		try{
			p1.join();
			c1.join();
		}catch(InterruptedException e){}
	}
}