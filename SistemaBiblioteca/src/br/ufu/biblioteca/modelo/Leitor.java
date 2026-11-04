package br.ufu.biblioteca.modelo;

public class Leitor extends Pessoa {

    // Constante de classe: o limite vale para a biblioteca inteira, não para
    // um leitor específico, por isso é static final.
    public static final int LIMITE_EMPRESTIMOS = 3;

    private String matricula;

    public Leitor(String matricula, String nome, String cpf, String email) {
        super(nome, cpf, email);
        this.matricula = matricula;
    }

    /**
     * Regra do leitor: entra com os quatro últimos dígitos do CPF. É uma regra
     * mais fraca que a do bibliotecário porque o leitor só consulta dados dele.
     */
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
