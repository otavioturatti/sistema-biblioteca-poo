package br.ufu.biblioteca.excecao;

// Lançada na validação dos cadastros, antes de qualquer objeto ser criado.
public class DadosInvalidosException extends BibliotecaException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
