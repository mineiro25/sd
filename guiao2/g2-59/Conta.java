package com.example.java;

//Ex4:
public class Conta {

    int saldo = 0;

    public synchronized void credito(int valor){
        saldo += valor;
    }

    public synchronized void debito(int valor){
        saldo -= valor;
    }

    public synchronized int consulta(){
        return saldo;
    }

    //temos sync em tds os métodos para garantir exclusão mútua, assim não há valores alterados incorretamente

}
