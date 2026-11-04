package br.ufu.biblioteca.modelo;

public class Revista extends ItemAcervo {

    public static final int PRAZO_PADRAO = 7;
    public static final double MULTA_PADRAO = 1.00;

    private int edicao;

    public Revista(String codigo, String titulo, int ano, int edicao) {
        super(codigo, titulo, ano);
        this.edicao = edicao;
    }

    @Override
    public String getTipo() {
        return "Revista";
    }

    @Override
    public String getDetalhe() {
        return "Edição " + edicao;
    }

    @Override
    public String getRotuloDetalhe() {
        return "Edição";
    }

    @Override
    public boolean detalheENumerico() {
        return true;
    }

    @Override
    public void definirDetalhe(String valor) {
        this.edicao = Integer.parseInt(valor.trim());
    }

    // Prazo curto porque a revista circula entre vários leitores no mês.
    @Override
    public int getPrazoPadrao() {
        return PRAZO_PADRAO;
    }

    @Override
    public double getMultaPadrao() {
        return MULTA_PADRAO;
    }

    public int getEdicao() {
        return edicao;
    }
}
