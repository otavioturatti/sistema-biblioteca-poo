package br.ufu.biblioteca.excecao;

/**
 * Lançada quando a devolução não faz sentido: item que não está emprestado,
 * empréstimo já encerrado ou data de devolução anterior à data de retirada.
 */
public class DevolucaoInvalidaException extends BibliotecaException {

    public DevolucaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
