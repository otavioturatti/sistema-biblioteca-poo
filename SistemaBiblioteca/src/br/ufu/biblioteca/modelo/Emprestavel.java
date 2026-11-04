package br.ufu.biblioteca.modelo;

/**
 * Contrato do que pode ser emprestado. Prazo e multa mudam conforme o tipo de
 * item, então a interface define o que todo item precisa responder e cada
 * classe concreta decide os valores.
 */
public interface Emprestavel {

    int getPrazoDias();

    double getMultaPorDia();

    boolean estaDisponivel();
}
