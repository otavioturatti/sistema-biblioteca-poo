package br.ufu.biblioteca.excecao;

public class ItemIndisponivelException extends BibliotecaException {

    public ItemIndisponivelException(String titulo) {
        super("O item \"" + titulo + "\" já está emprestado.");
    }
}
