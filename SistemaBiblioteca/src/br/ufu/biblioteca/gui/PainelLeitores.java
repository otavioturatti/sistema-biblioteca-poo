package br.ufu.biblioteca.gui;

import br.ufu.biblioteca.excecao.BibliotecaException;
import br.ufu.biblioteca.modelo.Leitor;
import br.ufu.biblioteca.servico.Biblioteca;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cadastro de leitores, com busca por matrícula, nome ou CPF. A situação de
 * cada um é calculada na hora, a partir dos empréstimos.
 */
public class PainelLeitores extends PainelBase {

    private static final String[] COLUNAS =
            {"Matrícula", "Nome", "CPF", "E-mail", "Itens em mãos", "Situação"};

    private final List<Leitor> leitoresExibidos = new ArrayList<>();

    private final DefaultTableModel modelo = new DefaultTableModel(COLUNAS, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    private final JTable tabela = new JTable(modelo);
    private final JTextField busca = new JTextField(20);
    private final JLabel resumo = new JLabel();

    public PainelLeitores(Biblioteca biblioteca) {
        super(biblioteca);

        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(criarBarraDeBusca(), BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);

        prepararTabela();
        atualizar();
    }

    private void prepararTabela() {
        tabela.setRowHeight(24);
        tabela.setAutoCreateRowSorter(true);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(220);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(190);

        tabela.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabela, Object valor,
                    boolean selecionada, boolean comFoco, int linha, int coluna) {

                Component celula = super.getTableCellRendererComponent(
                        tabela, valor, selecionada, comFoco, linha, coluna);
                if (!selecionada) {
                    celula.setForeground("Em atraso".equals(valor)
                            ? new Color(0xB00020) : new Color(0x1B6B2F));
                }
                return celula;
            }
        });
    }

    private JPanel criarBarraDeBusca() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barra.add(new JLabel("Buscar por matrícula, nome ou CPF:"));
        barra.add(busca);

        busca.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizar();
            }
        });

        return barra;
    }

    private JPanel criarRodape() {
        resumo.setFont(resumo.getFont().deriveFont(Font.PLAIN, 11f));
        resumo.setForeground(new Color(0x555555));

        JButton novo = new JButton("Cadastrar leitor");
        novo.addActionListener(evento -> cadastrar());

        JButton remover = new JButton("Remover selecionado");
        remover.addActionListener(evento -> remover());

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        esquerda.add(resumo);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        direita.add(novo);
        direita.add(remover);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(esquerda, BorderLayout.WEST);
        rodape.add(direita, BorderLayout.EAST);
        return rodape;
    }

    @Override
    public void atualizar() {
        modelo.setRowCount(0);
        leitoresExibidos.clear();

        LocalDate hoje = LocalDate.now();
        int emAtraso = 0;

        for (Leitor leitor : biblioteca.buscarLeitores(busca.getText())) {
            boolean atrasado = biblioteca.temAtraso(leitor, hoje);
            if (atrasado) {
                emAtraso++;
            }

            leitoresExibidos.add(leitor);
            modelo.addRow(new Object[]{
                    leitor.getMatricula(),
                    leitor.getNome(),
                    leitor.getCpf(),
                    leitor.getEmail(),
                    biblioteca.contarItensEmMaos(leitor) + " de " + Leitor.LIMITE_EMPRESTIMOS,
                    atrasado ? "Em atraso" : "Regular"});
        }

        resumo.setText(leitoresExibidos.size() + " leitor(es) na lista, " + emAtraso + " em atraso.");
    }

    private void cadastrar() {
        DialogoLeitor dialogo = new DialogoLeitor(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this), biblioteca);
        dialogo.setVisible(true);
        atualizar();
    }

    private void remover() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um leitor na tabela.",
                    "Nenhum leitor selecionado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // A tabela é ordenável, então a linha da tela precisa ser convertida.
        Leitor leitor = leitoresExibidos.get(tabela.convertRowIndexToModel(linha));

        int resposta = JOptionPane.showConfirmDialog(this,
                "Remover o cadastro de " + leitor.getNome() + "?",
                "Confirmar remoção", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            biblioteca.removerLeitor(leitor);
            atualizar();
        } catch (BibliotecaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Não foi possível remover", JOptionPane.WARNING_MESSAGE);
        }
    }
}
