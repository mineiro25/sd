package com.example.java;

//Ex1:

public class Contador {

    private int v;

    public Contador(int v){
        this.v = v;
    }

    synchronized public void inc(){
        v++;
    }

    synchronized public int valor(){
        return v;
    }
}
