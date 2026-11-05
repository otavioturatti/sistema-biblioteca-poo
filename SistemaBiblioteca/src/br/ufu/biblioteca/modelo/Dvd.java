package br.ufu.biblioteca.modelo;

public class Dvd extends ItemAcervo {

    public static final int PRAZO_PADRAO = 3;
    public static final double MULTA_PADRAO = 2.00;

    private int duracaoMinutos;

    public Dvd(String codigo, String titulo, int ano, int duracaoMinutos) {
        super(codigo, titulo, ano);
        this.duracaoMinutos = duracaoMinutos;
    }

    @Override
    public String getTipo() {
        return "DVD";
    }

    @Override
    public String getDetalhe() {
        return duracaoMinutos + " min";
    }

    @Override
    public String getRotuloDetalhe() {
        return "Duração (min)";
    }

    @Override
    public boolean detalheENumerico() {
        return true;
    }

    @Override
    public void definirDetalhe(String valor) {
        this.duracaoMinutos = Integer.parseInt(valor.trim());
    }

    // Poucas cópias, então prazo curto e multa alta
    @Override
    public int getPrazoPadrao() {
        return PRAZO_PADRAO;
    }

    @Override
    public double getMultaPadrao() {
        return MULTA_PADRAO;
    }

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }
}
