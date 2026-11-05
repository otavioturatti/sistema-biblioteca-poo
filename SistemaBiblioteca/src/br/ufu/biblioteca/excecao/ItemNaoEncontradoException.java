package br.ufu.biblioteca.excecao;

public class ItemNaoEncontradoException extends BibliotecaException {

    public ItemNaoEncontradoException(String codigo) {
        super("Nenhum item do acervo com o código " + codigo + ".");
    }
}
