package br.ufu.biblioteca.gui;

import br.ufu.biblioteca.modelo.Emprestimo;
import br.ufu.biblioteca.modelo.ItemEmprestado;
import br.ufu.biblioteca.modelo.Leitor;
import br.ufu.biblioteca.servico.Biblioteca;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.time.LocalDate;

// O que o leitor vê: só os empréstimos dele e nenhum botão de alteração.
public class PainelMeusEmprestimos extends PainelBase {

    private static final String[] COLUNAS =
            {"Nº", "Item", "Tipo", "Tombo", "Retirada", "Devolver até", "Devolvido", "Atraso", "Multa"};

    private final Leitor leitor;
    private final JLabel resumo = new JLabel();

    private final DefaultTableModel modelo = new DefaultTableModel(COLUNAS, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    public PainelMeusEmprestimos(Biblioteca biblioteca, Leitor leitor) {
        super(biblioteca);
        this.leitor = leitor;

        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        resumo.setFont(resumo.getFont().deriveFont(Font.PLAIN, 13f));
        resumo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(24);

        add(resumo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        atualizar();
    }

    @Override
    public void atualizar() {
        modelo.setRowCount(0);
        LocalDate hoje = LocalDate.now();
        double multaTotal = 0.0;

        for (Emprestimo emprestimo : biblioteca.getEmprestimosDoLeitor(leitor)) {
            for (ItemEmprestado emprestado : emprestimo.getItens()) {
                long atraso = emprestado.getDiasDeAtraso(hoje);
                double multa = emprestado.estaDevolvido()
                        ? emprestado.getMulta()
                        : emprestado.getObra().calcularMulta(atraso);
                multaTotal += multa;

                modelo.addRow(new Object[]{
                        emprestimo.getId(),
                        emprestado.getObra().getTitulo(),
                        emprestado.getObra().getTipo(),
                        emprestado.getExemplar().getTombo(),
                        Formatos.data(emprestimo.getRetiradaEm()),
                        Formatos.data(emprestado.getPrevisaoDevolucao()),
                        Formatos.data(emprestado.getDevolvidoEm()),
                        atraso > 0 ? atraso + " dia(s)" : "-",
                        Formatos.moeda(multa)});
            }
        }

        resumo.setText("Itens em mãos: " + biblioteca.contarItensEmMaos(leitor)
                + " de " + Leitor.LIMITE_EMPRESTIMOS
                + "     |     Situação: " + (biblioteca.temAtraso(leitor, hoje) ? "em atraso" : "regular")
                + "     |     Multas: " + Formatos.moeda(multaTotal));
    }
}
