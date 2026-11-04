package br.ufu.biblioteca.excecao;

// Lançada quando um código de item informado não existe no acervo.
public class ItemNaoEncontradoException extends BibliotecaException {

    public ItemNaoEncontradoException(String codigo) {
        super("Nenhum item do acervo com o código " + codigo + ".");
    }
}
