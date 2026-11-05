package br.ufu.biblioteca.excecao;

// Classe base das exceções do sistema. Todas as outras herdam dela, então a
// tela consegue tratar tudo em um catch só.
public class BibliotecaException extends Exception {

    public BibliotecaException(String mensagem) {
        super(mensagem);
    }

    public BibliotecaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
