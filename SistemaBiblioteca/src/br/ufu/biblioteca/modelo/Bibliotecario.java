package br.ufu.biblioteca.modelo;

public class Bibliotecario extends Pessoa {

    private String siape;
    private String senha;

    public Bibliotecario(String siape, String nome, String cpf, String email, String senha) {
        super(nome, cpf, email);
        this.siape = siape;
        this.senha = senha;
    }

    // Senha de verdade, com no mínimo seis caracteres
    @Override
    public boolean autenticar(String senha) {
        return this.senha != null && this.senha.equals(senha) && senha.length() >= 6;
    }

    @Override
    public String getIdentificador() {
        return siape;
    }

    @Override
    public String getPerfil() {
        return "Bibliotecário";
    }

    @Override
    public boolean podeGerenciarAcervo() {
        return true;
    }

    public String getSiape() {
        return siape;
    }

    public String getSenha() {
        return senha;
    }
}
