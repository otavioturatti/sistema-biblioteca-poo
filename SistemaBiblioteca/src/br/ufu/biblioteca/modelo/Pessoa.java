package br.ufu.biblioteca.modelo;

// Leitor e bibliotecário entram no sistema de jeitos diferentes, então o que
// é comum fica aqui e o resto cada um resolve.
public abstract class Pessoa implements Autenticavel {

    protected String nome;
    protected String cpf;
    protected String email;

    public Pessoa(String nome, String cpf, String email) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
    }

    public abstract String getPerfil();

    // Matrícula do leitor ou SIAPE do bibliotecário
    public abstract String getIdentificador();

    // Decide quais abas a tela principal vai montar
    public abstract boolean podeGerenciarAcervo();

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return nome;
    }
}
