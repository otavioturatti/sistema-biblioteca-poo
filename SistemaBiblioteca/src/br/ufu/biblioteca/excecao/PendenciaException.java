package br.ufu.biblioteca.excecao;

public class PendenciaException extends BibliotecaException {

    public PendenciaException(String leitor) {
        super(leitor + " possui itens em atraso. Regularize a devolução antes de um novo empréstimo.");
    }
}
