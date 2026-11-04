package br.ufu.biblioteca.excecao;

// Lançada quando o leitor tem empréstimo em atraso e tenta retirar outro item.
public class PendenciaException extends BibliotecaException {

    public PendenciaException(String leitor) {
        super(leitor + " possui itens em atraso. Regularize a devolução antes de um novo empréstimo.");
    }
}
