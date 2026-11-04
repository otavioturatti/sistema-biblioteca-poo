package br.ufu.biblioteca.excecao;

// Lançada ao cadastrar um código de item ou uma matrícula de leitor já usados.
public class CadastroDuplicadoException extends BibliotecaException {

    public CadastroDuplicadoException(String campo, String valor) {
        super("Já existe um cadastro com " + campo + " " + valor + ".");
    }
}
