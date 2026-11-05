package br.ufu.biblioteca.modelo;

// O que pode ser emprestado
public interface Emprestavel {

    int getPrazoDias();

    double getMultaPorDia();

    boolean estaDisponivel();
}
