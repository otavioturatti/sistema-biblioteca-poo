package br.ufu.biblioteca.modelo;

public class Leitor extends Pessoa {

    // Vale para a biblioteca toda, não para um leitor só
    public static final int LIMITE_EMPRESTIMOS = 3;

    private String matricula;

    public Leitor(String matricula, String nome, String cpf, String email) {
        super(nome, cpf, email);
        this.matricula = matricula;
    }

    // Senha do leitor: os quatro últimos dígitos do CPF
    @Override
    public boolean autenticar(String senha) {
        if (cpf == null || cpf.length() < 4 || senha == null) {
            return false;
        }
        return cpf.substring(cpf.length() - 4).equals(senha);
    }

    @Override
    public String getIdentificador() {
        return matricula;
    }

    @Override
    public String getPerfil() {
        return "Leitor";
    }

    @Override
    public boolean podeGerenciarAcervo() {
        return false;
    }

    public String getMatricula() {
        return matricula;
    }
}
