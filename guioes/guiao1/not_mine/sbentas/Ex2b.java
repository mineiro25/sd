package com.example.java;

public class Ex2b implements Runnable{

    public int count;
    public int inc;

    public void run(){
        for(int i = 0; i < inc; i++)
            count++;
    }

    Ex2b(){
        count = 0;
        inc = 1000;
    }

    public void get(){
        System.out.println(count);
    }

    public static void main(String[] args){
        int nthreads = Integer.parseInt(args[0]);

        Ex2b ex = new Ex2b();

        Thread thread[] = new Thread[nthreads];
        for(int k = 0; k < nthreads; k++){
            thread[k] = new Thread(ex);
            thread[k].start();
        }

        try{
            for(int k = 0; k < nthreads; k++)
                thread[k].join();
        } catch(InterruptedException e){};

        ex.get();
    }
}
