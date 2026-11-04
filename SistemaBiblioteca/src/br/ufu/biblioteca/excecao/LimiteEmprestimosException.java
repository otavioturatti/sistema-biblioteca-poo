package br.ufu.biblioteca.excecao;

// Lançada quando o leitor tenta ultrapassar o limite de itens simultâneos.
public class LimiteEmprestimosException extends BibliotecaException {

    public LimiteEmprestimosException(String leitor, int limite) {
        super(leitor + " já está com " + limite + " itens, que é o limite por leitor.");
    }
}
