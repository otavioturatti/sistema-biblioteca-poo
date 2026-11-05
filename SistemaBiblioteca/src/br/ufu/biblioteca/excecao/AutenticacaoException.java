package br.ufu.biblioteca.excecao;

// Não diz se o erro foi no usuário ou na senha, para não entregar quais
// cadastros existem.
public class AutenticacaoException extends BibliotecaException {

    public AutenticacaoException() {
        super("Usuário ou senha inválidos.");
    }
}
