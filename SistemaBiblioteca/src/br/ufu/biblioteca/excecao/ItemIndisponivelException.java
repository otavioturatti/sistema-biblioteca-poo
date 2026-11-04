package br.ufu.biblioteca.excecao;

// Lançada quando o item escolhido já está emprestado a outro leitor.
public class ItemIndisponivelException extends BibliotecaException {

    public ItemIndisponivelException(String titulo) {
        super("O item \"" + titulo + "\" já está emprestado.");
    }
}
