package br.ufu.biblioteca.excecao;

public class CadastroDuplicadoException extends BibliotecaException {

    public CadastroDuplicadoException(String campo, String valor) {
        super("Já existe um cadastro com " + campo + " " + valor + ".");
    }
}
