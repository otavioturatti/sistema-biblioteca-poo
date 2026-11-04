package br.ufu.biblioteca.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Uma linha do empréstimo: qual exemplar saiu, até quando, quando voltou e
 * quanto de multa gerou.
 *
 * Os construtores têm visibilidade de pacote de propósito, assim só a classe
 * Emprestimo cria estes objetos, que é o que caracteriza a composição:
 * nenhum ItemEmprestado existe solto no sistema.
 */
public class ItemEmprestado {

    // Agregação: o exemplar continua pertencendo ao acervo, aqui é referenciado.
    private final Exemplar exemplar;
    private final LocalDate previsaoDevolucao;
    private LocalDate devolvidoEm;
    private double multa;

    ItemEmprestado(Exemplar exemplar, LocalDate retiradaEm) {
        this.exemplar = exemplar;
        // o prazo vem da obra, então cada tipo de item vence em uma data
        this.previsaoDevolucao = retiradaEm.plusDays(exemplar.getObra().getPrazoDias());
    }

    ItemEmprestado(Exemplar exemplar, LocalDate previsaoDevolucao, LocalDate devolvidoEm, double multa) {
        this.exemplar = exemplar;
        this.previsaoDevolucao = previsaoDevolucao;
        this.devolvidoEm = devolvidoEm;
        this.multa = multa;
    }

    double registrarDevolucao(LocalDate dataDevolucao) {
        this.devolvidoEm = dataDevolucao;
        long atraso = ChronoUnit.DAYS.between(previsaoDevolucao, dataDevolucao);
        // calcularMulta é o mesmo método para todos, mas o valor diário é o da
        // obra: livro, revista e DVD cobram valores diferentes
        this.multa = exemplar.getObra().calcularMulta(atraso);
        exemplar.marcarDisponivel();
        return multa;
    }

    public long getDiasDeAtraso(LocalDate referencia) {
        LocalDate base = (devolvidoEm != null) ? devolvidoEm : referencia;
        long dias = ChronoUnit.DAYS.between(previsaoDevolucao, base);
        return (dias > 0) ? dias : 0;
    }

    public boolean estaDevolvido() {
        return devolvidoEm != null;
    }

    public Exemplar getExemplar() {
        return exemplar;
    }

    // Atalho para a obra, que é de onde vêm título, tipo e multa.
    public ItemAcervo getObra() {
        return exemplar.getObra();
    }

    public LocalDate getPrevisaoDevolucao() {
        return previsaoDevolucao;
    }

    public LocalDate getDevolvidoEm() {
        return devolvidoEm;
    }

    public double getMulta() {
        return multa;
    }
}
