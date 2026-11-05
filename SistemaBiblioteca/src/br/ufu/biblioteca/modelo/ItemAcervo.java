package br.ufu.biblioteca.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Obra do catálogo. Não é a cópia: a biblioteca pode ter vários exemplares do
// mesmo título, e são eles que vão para o empréstimo.
public abstract class ItemAcervo implements Emprestavel {

    private String codigo;
    private String titulo;
    private int ano;

    private final List<Exemplar> exemplares = new ArrayList<>();

    // null = a obra usa o valor padrão do tipo dela
    private Integer prazoPersonalizado;
    private Double multaPersonalizada;

    protected ItemAcervo(String codigo, String titulo, int ano) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.ano = ano;
    }

    public abstract String getTipo();

    public abstract String getDetalhe();

    // Rótulo do detalhe na tela e se ele aceita só número
    public abstract String getRotuloDetalhe();

    public abstract boolean detalheENumerico();

    public abstract void definirDetalhe(String valor);

    public abstract int getPrazoPadrao();

    public abstract double getMultaPadrao();

    // Numera a partir do que já existe para não repetir tombo
    public void adicionarExemplares(int quantidade) {
        int proximo = exemplares.size() + 1;
        for (int i = 0; i < quantidade; i++) {
            exemplares.add(new Exemplar(this, codigo + "-" + (proximo + i)));
        }
    }

    public Exemplar restaurarExemplar(String tombo, boolean disponivel) {
        Exemplar exemplar = new Exemplar(this, tombo);
        if (!disponivel) {
            exemplar.marcarEmprestado();
        }
        exemplares.add(exemplar);
        return exemplar;
    }

    // Devolve null se todas as cópias estiverem emprestadas
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

    // Tira primeiro as cópias que estão na estante e devolve quantas saíram
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

    @Override
    public int getPrazoDias() {
        return (prazoPersonalizado != null) ? prazoPersonalizado : getPrazoPadrao();
    }

    @Override
    public double getMultaPorDia() {
        return (multaPersonalizada != null) ? multaPersonalizada : getMultaPadrao();
    }

    // Só guarda o ajuste se for diferente do padrão do tipo
    public void definirPrazo(int dias) {
        this.prazoPersonalizado = (dias == getPrazoPadrao()) ? null : dias;
    }

    public void definirMulta(double valor) {
        this.multaPersonalizada = (Math.abs(valor - getMultaPadrao()) < 0.001) ? null : valor;
    }

    public boolean temAjustePersonalizado() {
        return prazoPersonalizado != null || multaPersonalizada != null;
    }

    // A conta é igual para todos, o que muda é o valor diário de cada tipo
    public double calcularMulta(long diasDeAtraso) {
        if (diasDeAtraso <= 0) {
            return 0.0;
        }
        return diasDeAtraso * getMultaPorDia();
    }

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
