package br.ufu.biblioteca.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Emprestimo {

    // Contador único do sistema, gera o número do próximo empréstimo
    private static int proximoId = 1;

    private final int id;
    private final Leitor leitor;
    private final LocalDate retiradaEm;
    private final List<ItemEmprestado> itens = new ArrayList<>();
    private StatusEmprestimo status;

    public Emprestimo(Leitor leitor, LocalDate retiradaEm) {
        this(proximoId++, leitor, retiradaEm, StatusEmprestimo.ATIVO);
    }

    // Usado ao carregar do arquivo, quando o id já existe
    public Emprestimo(int id, Leitor leitor, LocalDate retiradaEm, StatusEmprestimo status) {
        this.id = id;
        this.leitor = leitor;
        this.retiradaEm = retiradaEm;
        this.status = status;
        // evita repetir um id que veio do arquivo
        if (id >= proximoId) {
            proximoId = id + 1;
        }
    }

    public void adicionarExemplar(Exemplar exemplar) {
        itens.add(new ItemEmprestado(exemplar, retiradaEm));
        exemplar.marcarEmprestado();
    }

    public void restaurarItem(Exemplar exemplar, LocalDate previsao,
                              LocalDate devolvidoEm, double multa) {
        itens.add(new ItemEmprestado(exemplar, previsao, devolvidoEm, multa));
        if (devolvidoEm == null) {
            exemplar.marcarEmprestado();
        }
    }

    public double devolver(LocalDate dataDevolucao) {
        double total = 0.0;
        for (ItemEmprestado emprestado : itens) {
            if (!emprestado.estaDevolvido()) {
                total += emprestado.registrarDevolucao(dataDevolucao);
            }
        }
        status = StatusEmprestimo.DEVOLVIDO;
        return total;
    }

    public double getMultaTotal() {
        double total = 0.0;
        for (ItemEmprestado emprestado : itens) {
            total += emprestado.getMulta();
        }
        return total;
    }

    public boolean estaAtrasado(LocalDate referencia) {
        if (status == StatusEmprestimo.DEVOLVIDO) {
            return false;
        }
        for (ItemEmprestado emprestado : itens) {
            if (emprestado.getDiasDeAtraso(referencia) > 0) {
                return true;
            }
        }
        return false;
    }

    public int getQuantidadeDeItens() {
        return itens.size();
    }

    // Só para leitura: ninguém insere nem remove item por fora daqui
    public List<ItemEmprestado> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public int getId() {
        return id;
    }

    public Leitor getLeitor() {
        return leitor;
    }

    public LocalDate getRetiradaEm() {
        return retiradaEm;
    }

    public StatusEmprestimo getStatus() {
        return status;
    }
}
