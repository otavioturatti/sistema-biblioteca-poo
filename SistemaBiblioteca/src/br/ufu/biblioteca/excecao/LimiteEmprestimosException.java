package br.ufu.biblioteca.excecao;

public class LimiteEmprestimosException extends BibliotecaException {

    public LimiteEmprestimosException(String leitor, int limite) {
        super(leitor + " já está com " + limite + " itens, que é o limite por leitor.");
    }
}
