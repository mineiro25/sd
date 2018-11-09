package com.example.java;

public class Ex2a implements Runnable{

    public Counter count;

    public void run(){
        for(int i = 0; i<1000; i++)
            count.increment();
    }

    Ex2a(){
        count = new Counter();
    }

    public void get(){
        count.get();
    }

    public static void main(String[] args){
        int nthreads = Integer.parseInt(args[0]);

        Ex2a ex = new Ex2a();

        Thread thread[] = new Thread[nthreads];
        for(int k = 0; k < nthreads; k++) {
            thread[k] = new Thread(ex); //?
            thread[k].start();
        }
        try{
            for(int k = 0; k < nthreads; k++)
                thread[k].join();

        } catch(InterruptedException e){};
    }
}


