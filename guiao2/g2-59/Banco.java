package com.example.java;

public class Banco{

    Conta[] contas;

    public Banco(int n){

        contas = new Conta[n];
        for(int i = 0; i < n; i++)
            contas[i].saldo = 0;
    }

    //Ex2:

    synchronized public int consulta(int i){

        return contas[i].consulta();
    }

    public void credito(int i, int valor){ // credito = depositar

        contas[i].saldo += valor;
    }

    public void debito(int i, int valor){ // debito = retirar

        contas[i].saldo -= valor;
    }


    // n pomos sync no método debito nem credito pq só podemos mexer numa conta de cada vez: ineficiente
    // ou seja, não vamos fazer exclusão mútua ao nível do Banco e sim ao nível da conta com o objetivo de ficar + eficiente

    //Ex3:

    /*

    NÃO FUNCIONA: CAUSA DEADLOCK
    acontece quando tranfiro da conta 5 pra conta 10 e da conta 10 pra conta 5;

    Caso em que a sequência dos eventos é a seguinte:
    T1: bloqueia conta 5
    T2: bloqueia conta 10
    DEADLOCK!!!


    public void transfereErrado(int o, int d, int valor){ // não precisamos de pôr sync pq debito e credito já estão em exlusão mútua
        synchronized (contas[o]){ //ganho exclusividade pra entrar na contas[o] e na contas[d], mais nng lá entra enquanto eu lá estou
            synchronized (contas[d]){
                contas[o].debito(valor);
                contas[d].credito(valor);
            }
        }
    }

    */
    
    public void transfere(int o, int d, int valor){
        int i = o < d ? o : d;
        int j = o < d ? d : o; //encontrei o indice mais baixo
        synchronized (contas[i]){
            synchronized (contas[j]){
                contas[o].debito(valor);
                contas[d].credito(valor);
            }
        }
    }


}
