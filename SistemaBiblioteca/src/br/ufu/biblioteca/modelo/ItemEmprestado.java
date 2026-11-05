package br.ufu.biblioteca.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Uma linha do empréstimo: qual exemplar saiu, até quando e quanto de multa
// deu. Os construtores não são public porque só o Emprestimo cria estes.
public class ItemEmprestado {

    private final Exemplar exemplar;
    private final LocalDate previsaoDevolucao;
    private LocalDate devolvidoEm;
    private double multa;

    ItemEmprestado(Exemplar exemplar, LocalDate retiradaEm) {
        this.exemplar = exemplar;
        // o prazo vem da obra, então cada tipo vence em uma data
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
