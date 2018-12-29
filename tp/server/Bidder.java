import java.io.*;
import java.net.*;
import java.util.*;
//import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.*;

public class Bidder{
	String username;

	String password;

	double debt;

	ReentrantLock lock;

	Bidder(){
		this.username = null;
		this.password = null;
		this.debt = 0;
		this.lock = new ReentrantLock();
	}

	Bidder(String username, String password){
		this.username = username;
		this.password = password;
		this.debt = 0;
		this.lock = new ReentrantLock();
	}

	public void setUsername(String username){
		this.lock.lock();
		this.username = username;
		this.lock.unlock();
	}

	public void setPassword(String password){
		this.lock.lock();
		this.password = password;
		this.lock.unlock();
	}

	public void setDebt(double debt){
		this.lock.lock();
		this.debt = debt;
		this.lock.unlock();
	}

	public String getUsername(){
		this.lock.lock();
		String result = this.username;
		this.lock.unlock();
		return result;
	}

	public String getPassword(){
		this.lock.lock();
		String result = this.password;
		this.lock.unlock();
		return result;
	}

	public double getDebt(){
		this.lock.lock();
		double result = this.debt;
		this.lock.unlock();
		return result;
	}

	public void sumDebt(double debt){
		this.lock.lock();
		this.debt += Math.round(debt*100.0) / 100.0;
		this.debt = Math.round(this.debt*100.0) / 100.0;
		this.lock.unlock(); 
	}
}