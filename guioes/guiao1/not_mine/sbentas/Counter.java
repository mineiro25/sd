package com.example.java;

public class Counter {

    public int val;

    public Counter(){
        val = 0;
    }

    public void increment(){
        val++;
    }

    public void get(){
        System.out.println(val);
    }
}
