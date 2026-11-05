package br.ufu.biblioteca.gui;

import br.ufu.biblioteca.modelo.Leitor;
import br.ufu.biblioteca.servico.Biblioteca;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Uma lista suspensa até funciona com poucos leitores, mas trava com a base
// cheia. Aqui ele digita o que sabe e escolhe no resultado.
public class DialogoBuscaLeitor extends JDialog {

    private static final String[] COLUNAS =
            {"Matrícula", "Nome", "CPF", "Itens em mãos", "Situação"};

    private final Biblioteca biblioteca;
    private final List<Leitor> encontrados = new ArrayList<>();

    private final JTextField busca = new JTextField(22);
    private final JLabel resultado = new JLabel();

    private final DefaultTableModel modelo = new DefaultTableModel(COLUNAS, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    private final JTable tabela = new JTable(modelo);

    private Leitor selecionado;

    public DialogoBuscaLeitor(Frame dono, Biblioteca biblioteca) {
        super(dono, "Escolher leitor", true);
        this.biblioteca = biblioteca;

        setLayout(new BorderLayout(0, 6));
        add(criarBarraDeBusca(), BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(criarBotoes(), BorderLayout.SOUTH);

        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoCreateRowSorter(true);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(220);

        // duplo clique escolhe direto
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                if (evento.getClickCount() == 2) {
                    confirmar();
                }
            }
        });

        atualizar();

        setSize(new Dimension(640, 420));
        setLocationRelativeTo(dono);
    }

    private JPanel criarBarraDeBusca() {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        linha.add(new JLabel("Matrícula, nome ou CPF:"));
        linha.add(busca);

        resultado.setFont(resultado.getFont().deriveFont(Font.PLAIN, 11f));
        resultado.setForeground(new Color(0x555555));
        linha.add(resultado);

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

        return linha;
    }

    private JPanel criarBotoes() {
        JButton escolher = new JButton("Escolher");
        escolher.addActionListener(evento -> confirmar());

        JButton cancelar = new JButton("Cancelar");
        cancelar.addActionListener(evento -> dispose());

        getRootPane().setDefaultButton(escolher);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        botoes.add(cancelar);
        botoes.add(escolher);
        return botoes;
    }

    private void atualizar() {
        modelo.setRowCount(0);
        encontrados.clear();

        LocalDate hoje = LocalDate.now();
        for (Leitor leitor : biblioteca.buscarLeitores(busca.getText())) {
            encontrados.add(leitor);
            modelo.addRow(new Object[]{
                    leitor.getMatricula(),
                    leitor.getNome(),
                    leitor.getCpf(),
                    biblioteca.contarItensEmMaos(leitor) + " de " + Leitor.LIMITE_EMPRESTIMOS,
                    biblioteca.temAtraso(leitor, hoje) ? "Em atraso" : "Regular"});
        }

        int total = encontrados.size();
        resultado.setText(total == 1 ? "1 leitor encontrado" : total + " leitores encontrados");
    }

    private void confirmar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        // a tabela é ordenável, então converte a linha
        selecionado = encontrados.get(tabela.convertRowIndexToModel(linha));
        dispose();
    }

    public Leitor getSelecionado() {
        return selecionado;
    }
}
