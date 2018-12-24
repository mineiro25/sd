package com.example.java;

public class Ex1 implements Runnable{
    int num;

    public void run(){ //o que se vai fazer em cada uma das threads
        int i = 1;
        while(i<= num){
            System.out.println(i);
            i++;
        }
    }

    Ex1(int arg){
        num = arg;
    }

    public static void main(String[] args) {
	    int nthreads = Integer.parseInt(args[0]);
	    int i = Integer.parseInt(args[1]);

	    Ex1 aux = new Ex1(i);

	    Thread thread[] = new Thread[nthreads];
	    for(int k = 0; k < nthreads; k++) {
            thread[k] = new Thread(aux); //?
            thread[k].start();
        }

        try{
	        for(int k = 0; k < nthreads; k++)
	            thread[k].join();

        } catch(InterruptedException e){};
    }
}
