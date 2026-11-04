package br.ufu.biblioteca.modelo;

/**
 * Superclasse das pessoas do sistema. É abstrata porque "pessoa" sozinha não
 * existe na biblioteca: ou é leitor, ou é bibliotecário, e cada um autentica
 * de um jeito e tem permissões diferentes.
 */
public abstract class Pessoa implements Autenticavel {

    protected String nome;
    protected String cpf;
    protected String email;

    public Pessoa(String nome, String cpf, String email) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
    }

    // Cada subclasse responde o próprio nome de perfil, usado no rodapé da tela.
    public abstract String getPerfil();

    /**
     * Código que a pessoa digita para entrar: matrícula para o leitor, SIAPE
     * para o bibliotecário. Como o método existe aqui, a tela de login percorre
     * uma lista de Pessoa sem precisar saber de qual classe é cada objeto.
     */
    public abstract String getIdentificador();

    // Define o que a tela principal libera para o usuário logado, sem precisar
    // de instanceof espalhado pela interface gráfica.
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
