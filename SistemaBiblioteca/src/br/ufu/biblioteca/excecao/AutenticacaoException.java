package br.ufu.biblioteca.excecao;

// Lançada quando o login falha. A mensagem não diz se o erro foi no usuário ou
// na senha, para não entregar quais cadastros existem.
public class AutenticacaoException extends BibliotecaException {

    public AutenticacaoException() {
        super("Usuário ou senha inválidos.");
    }
}
