package br.ufu.biblioteca.modelo;

public class Livro extends ItemAcervo {

    public static final int PRAZO_PADRAO = 14;
    public static final double MULTA_PADRAO = 0.50;

    private String autor;

    public Livro(String codigo, String titulo, int ano, String autor) {
        super(codigo, titulo, ano);
        this.autor = autor;
    }

    @Override
    public String getTipo() {
        return "Livro";
    }

    @Override
    public String getDetalhe() {
        return autor;
    }

    @Override
    public String getRotuloDetalhe() {
        return "Autor";
    }

    @Override
    public boolean detalheENumerico() {
        return false;
    }

    @Override
    public void definirDetalhe(String valor) {
        this.autor = valor;
    }

    // Livro fica mais tempo com o leitor e a multa é a mais barata
    @Override
    public int getPrazoPadrao() {
        return PRAZO_PADRAO;
    }

    @Override
    public double getMultaPadrao() {
        return MULTA_PADRAO;
    }

    public String getAutor() {
        return autor;
    }
}
