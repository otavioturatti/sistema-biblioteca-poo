package br.ufu.biblioteca.gui;

import br.ufu.biblioteca.excecao.BibliotecaException;
import br.ufu.biblioteca.modelo.ItemAcervo;
import br.ufu.biblioteca.servico.Biblioteca;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Lista o catálogo e, para o bibliotecário, permite cadastrar, editar e
 * remover obras.
 *
 * Cada linha é uma obra, não uma cópia: a coluna de exemplares mostra quantos
 * estão na estante do total que a biblioteca possui. As colunas de prazo e
 * multa mostram o polimorfismo na prática, já que os valores vêm do tipo de
 * cada item.
 */
public class PainelAcervo extends PainelBase {

    private static final String[] COLUNAS =
            {"Código", "Tipo", "Título", "Ano", "Detalhe", "Exemplares", "Prazo", "Multa/dia"};

    private final boolean podeEditar;
    private final List<ItemAcervo> itensExibidos = new ArrayList<>();

    private final DefaultTableModel modelo = new DefaultTableModel(COLUNAS, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            // A tabela é só de leitura: alterações passam pelo formulário.
            return false;
        }
    };

    private final JTable tabela = new JTable(modelo);
    private final JComboBox<String> filtroTipo =
            new JComboBox<>(new String[]{"Todos", "Livro", "Revista", "DVD"});
    private final JTextField busca = new JTextField(18);
    private final JLabel resumo = new JLabel();

    public PainelAcervo(Biblioteca biblioteca, boolean podeEditar) {
        super(biblioteca);
        this.podeEditar = podeEditar;

        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(criarBarraDeFiltro(), BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);

        prepararTabela();
        atualizar();
    }

    private void prepararTabela() {
        tabela.setRowHeight(24);
        tabela.setAutoCreateRowSorter(true);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(240);

        // Deixa visível de longe a obra que está sem exemplar na estante.
        tabela.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabela, Object valor,
                    boolean selecionada, boolean comFoco, int linha, int coluna) {

                Component celula = super.getTableCellRendererComponent(
                        tabela, valor, selecionada, comFoco, linha, coluna);
                if (!selecionada) {
                    boolean esgotado = String.valueOf(valor).startsWith("0 ");
                    celula.setForeground(esgotado ? new Color(0xB00020) : new Color(0x1B6B2F));
                }
                return celula;
            }
        });

        if (podeEditar) {
            // Duplo clique na linha abre a edição, que é o atalho esperado.
            tabela.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evento) {
                    if (evento.getClickCount() == 2) {
                        editar();
                    }
                }
            });
        }
    }

    private JPanel criarBarraDeFiltro() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barra.add(new JLabel("Tipo:"));
        barra.add(filtroTipo);
        barra.add(new JLabel("   Buscar por código, título ou autor:"));
        barra.add(busca);

        filtroTipo.addActionListener(evento -> atualizar());

        // Filtra conforme o usuário digita, sem precisar de botão.
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

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        esquerda.add(resumo);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(esquerda, BorderLayout.WEST);

        if (podeEditar) {
            JButton novo = new JButton("Cadastrar item");
            novo.addActionListener(evento -> cadastrar());

            JButton editar = new JButton("Editar selecionado");
            editar.addActionListener(evento -> editar());

            JButton remover = new JButton("Remover selecionado");
            remover.addActionListener(evento -> remover());

            JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
            direita.add(novo);
            direita.add(editar);
            direita.add(remover);
            rodape.add(direita, BorderLayout.EAST);
        }

        return rodape;
    }

    @Override
    public void atualizar() {
        modelo.setRowCount(0);
        itensExibidos.clear();

        String tipo = (String) filtroTipo.getSelectedItem();
        int exemplares = 0;

        for (ItemAcervo item : biblioteca.buscarObras(busca.getText())) {
            if (!"Todos".equals(tipo) && !item.getTipo().equals(tipo)) {
                continue;
            }

            // O asterisco marca a obra que não segue o padrão do tipo.
            String marca = item.temAjustePersonalizado() ? " *" : "";
            exemplares += item.getTotalDeExemplares();

            itensExibidos.add(item);
            modelo.addRow(new Object[]{
                    item.getCodigo(),
                    item.getTipo(),
                    item.getTitulo(),
                    item.getAno(),
                    item.getDetalhe(),
                    item.contarDisponiveis() + " de " + item.getTotalDeExemplares(),
                    item.getPrazoDias() + " dias" + marca,
                    Formatos.moeda(item.getMultaPorDia()) + marca});
        }

        resumo.setText(itensExibidos.size() + " obra(s) na lista, " + exemplares
                + " exemplar(es).    * prazo ou multa ajustados só para aquela obra.");
    }

    private void cadastrar() {
        DialogoItem dialogo = new DialogoItem(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this), biblioteca);
        dialogo.setVisible(true);
        atualizar();
    }

    private void editar() {
        ItemAcervo item = getItemSelecionado();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Selecione um item na tabela.",
                    "Nenhum item selecionado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DialogoItem dialogo = new DialogoItem(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this), biblioteca, item);
        dialogo.setVisible(true);
        atualizar();
    }

    private void remover() {
        ItemAcervo item = getItemSelecionado();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Selecione um item na tabela.",
                    "Nenhum item selecionado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(this,
                "Remover \"" + item.getTitulo() + "\" e os seus "
                        + item.getTotalDeExemplares() + " exemplar(es) do acervo?",
                "Confirmar remoção", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            biblioteca.removerItem(item);
            atualizar();
        } catch (BibliotecaException e) {
            // Obra com exemplar emprestado ou falha ao gravar o arquivo: as
            // duas situações chegam aqui com a mensagem pronta.
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Não foi possível remover", JOptionPane.WARNING_MESSAGE);
        }
    }

    private ItemAcervo getItemSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return null;
        }
        // A tabela é ordenável, então a linha da tela não corresponde direto à
        // posição da lista: o índice precisa ser convertido.
        int indice = tabela.convertRowIndexToModel(linha);
        if (indice < 0 || indice >= itensExibidos.size()) {
            return null;
        }
        return itensExibidos.get(indice);
    }
}
