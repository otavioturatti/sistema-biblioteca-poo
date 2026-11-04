package br.ufu.biblioteca.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Superclasse abstrata do acervo. Representa a obra (o título catalogado),
 * não a cópia: a biblioteca pode ter vários exemplares do mesmo livro, e são
 * eles que circulam nos empréstimos.
 *
 * A obra é composta pelos seus exemplares: eles são criados aqui dentro e
 * deixam de existir junto com ela.
 *
 * Prazo e multa funcionam em dois níveis: cada tipo de item tem o seu padrão,
 * definido pela subclasse, e o bibliotecário pode ajustar o valor de uma obra
 * específica. Enquanto não ajusta, vale o padrão do tipo.
 */
public abstract class ItemAcervo implements Emprestavel {

    private String codigo;
    private String titulo;
    private int ano;

    private final List<Exemplar> exemplares = new ArrayList<>();

    // Nulo significa "sem ajuste": a obra segue o padrão da própria classe.
    private Integer prazoPersonalizado;
    private Double multaPersonalizada;

    protected ItemAcervo(String codigo, String titulo, int ano) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.ano = ano;
    }

    // ------------------------------------------------- comportamento do tipo

    // Nome do tipo exibido na tabela e gravado como primeira coluna do CSV.
    public abstract String getTipo();

    // Informação específica de cada tipo (autor, edição, duração).
    public abstract String getDetalhe();

    // Como esse campo se chama na tela e se ele aceita apenas números.
    public abstract String getRotuloDetalhe();

    public abstract boolean detalheENumerico();

    // Usado na edição: cada subclasse sabe em qual atributo guardar o valor.
    public abstract void definirDetalhe(String valor);

    // Valores que cada tipo pratica quando a obra não recebe ajuste.
    public abstract int getPrazoPadrao();

    public abstract double getMultaPadrao();

    // ------------------------------------------------------------ exemplares

    /**
     * Cria exemplares numerados em sequência a partir do que já existe, então
     * a numeração não se repete quando a obra recebe cópias novas depois.
     */
    public void adicionarExemplares(int quantidade) {
        int proximo = exemplares.size() + 1;
        for (int i = 0; i < quantidade; i++) {
            exemplares.add(new Exemplar(this, codigo + "-" + (proximo + i)));
        }
    }

    // Usado pela persistência para remontar um exemplar já gravado.
    public Exemplar restaurarExemplar(String tombo, boolean disponivel) {
        Exemplar exemplar = new Exemplar(this, tombo);
        if (!disponivel) {
            exemplar.marcarEmprestado();
        }
        exemplares.add(exemplar);
        return exemplar;
    }

    /**
     * Devolve o primeiro exemplar livre, ou nulo se todos estiverem
     * emprestados. Quem empresta não escolhe a cópia: o sistema entrega
     * qualquer uma disponível, que é como funciona no balcão.
     */
    public Exemplar getExemplarDisponivel() {
        for (Exemplar exemplar : exemplares) {
            if (exemplar.estaDisponivel()) {
                return exemplar;
            }
        }
        return null;
    }

    public int getTotalDeExemplares() {
        return exemplares.size();
    }

    public int contarDisponiveis() {
        int total = 0;
        for (Exemplar exemplar : exemplares) {
            if (exemplar.estaDisponivel()) {
                total++;
            }
        }
        return total;
    }

    public int contarEmprestados() {
        return exemplares.size() - contarDisponiveis();
    }

    public List<Exemplar> getExemplares() {
        return Collections.unmodifiableList(exemplares);
    }

    /**
     * Retira cópias do acervo, sempre começando pelas que estão na estante.
     * Devolve quantas conseguiu remover, para a tela avisar quando sobrou
     * exemplar emprestado.
     */
    public int removerExemplaresDisponiveis(int quantidade) {
        int removidos = 0;
        for (int i = exemplares.size() - 1; i >= 0 && removidos < quantidade; i--) {
            if (exemplares.get(i).estaDisponivel()) {
                exemplares.remove(i);
                removidos++;
            }
        }
        return removidos;
    }

    @Override
    public boolean estaDisponivel() {
        return contarDisponiveis() > 0;
    }

    // ------------------------------------------------------ prazo e multa

    @Override
    public int getPrazoDias() {
        return (prazoPersonalizado != null) ? prazoPersonalizado : getPrazoPadrao();
    }

    @Override
    public double getMultaPorDia() {
        return (multaPersonalizada != null) ? multaPersonalizada : getMultaPadrao();
    }

    /**
     * Guarda o ajuste apenas quando ele difere do padrão do tipo. Assim uma
     * obra que ficou com o valor de fábrica continua acompanhando a regra da
     * classe mesmo que o padrão mude depois.
     */
    public void definirPrazo(int dias) {
        this.prazoPersonalizado = (dias == getPrazoPadrao()) ? null : dias;
    }

    public void definirMulta(double valor) {
        this.multaPersonalizada = (Math.abs(valor - getMultaPadrao()) < 0.001) ? null : valor;
    }

    public boolean temAjustePersonalizado() {
        return prazoPersonalizado != null || multaPersonalizada != null;
    }

    /**
     * A conta é a mesma para toda obra, o que muda é o valor diário. Por isso
     * o método é concreto aqui e chama getMultaPorDia(), que cai no padrão da
     * subclasse ou no ajuste feito para esta obra.
     */
    public double calcularMulta(long diasDeAtraso) {
        if (diasDeAtraso <= 0) {
            return 0.0;
        }
        return diasDeAtraso * getMultaPorDia();
    }

    // ---------------------------------------------------------- dados gerais

    public String getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    @Override
    public String toString() {
        return codigo + " - " + titulo;
    }
}
