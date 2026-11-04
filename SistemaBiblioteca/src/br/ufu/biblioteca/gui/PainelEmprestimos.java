package br.ufu.biblioteca.gui;

import br.ufu.biblioteca.excecao.BibliotecaException;
import br.ufu.biblioteca.modelo.Emprestimo;
import br.ufu.biblioteca.modelo.ItemAcervo;
import br.ufu.biblioteca.modelo.ItemEmprestado;
import br.ufu.biblioteca.modelo.Leitor;
import br.ufu.biblioteca.modelo.StatusEmprestimo;
import br.ufu.biblioteca.servico.Biblioteca;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela central do sistema: registra empréstimos e devoluções.
 *
 * O leitor é localizado por busca, não escolhido em uma lista com a base
 * inteira. O catálogo mostra uma linha por obra, com quantos exemplares estão
 * na estante, e o sistema é quem separa a cópia na hora de emprestar.
 */
public class PainelEmprestimos extends PainelBase {

    private static final String[] COLUNAS_CATALOGO =
            {"", "Código", "Título", "Tipo", "Disponíveis", "Prazo", "Multa/dia"};

    private static final String[] COLUNAS_EMPRESTIMOS = {"Nº", "Leitor", "Item", "Tombo", "Retirada",
            "Previsão", "Devolvido", "Atraso", "Multa", "Situação"};

    private final JLabel leitorEscolhido = new JLabel("nenhum leitor escolhido");
    private final JTextField campoRetirada = new JTextField(10);
    private final JTextField campoDevolucao = new JTextField(10);
    private final JTextField filtro = new JTextField(16);
    private final JLabel contador = new JLabel("nenhum item marcado");
    private final JCheckBox apenasAtivos = new JCheckBox("Mostrar apenas empréstimos em aberto", true);

    private Leitor leitor;

    // Obras que estão sendo exibidas no catálogo, na mesma ordem do modelo.
    private final List<ItemAcervo> catalogoExibido = new ArrayList<>();

    // Guarda o empréstimo de cada linha da tabela de baixo, já que um
    // empréstimo ocupa várias linhas quando leva mais de um item.
    private final List<Emprestimo> emprestimoPorLinha = new ArrayList<>();

    private final DefaultTableModel modeloCatalogo = new DefaultTableModel(COLUNAS_CATALOGO, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            // Só a caixa de marcação é clicável, o resto é informação.
            return coluna == 0;
        }

        @Override
        public Class<?> getColumnClass(int coluna) {
            // Declarar a coluna como Boolean é o que faz o Swing desenhar a
            // caixa de marcação no lugar do texto "true".
            return (coluna == 0) ? Boolean.class : String.class;
        }
    };

    private final DefaultTableModel modeloEmprestimos = new DefaultTableModel(COLUNAS_EMPRESTIMOS, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    private final JTable tabelaCatalogo = new JTable(modeloCatalogo);
    private final JTable tabelaEmprestimos = new JTable(modeloEmprestimos);

    public PainelEmprestimos(Biblioteca biblioteca) {
        super(biblioteca);

        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(criarFormulario(), BorderLayout.NORTH);
        add(criarTabelaDeEmprestimos(), BorderLayout.CENTER);
        add(criarBarraDeDevolucao(), BorderLayout.SOUTH);

        prepararTabelas();
        atualizar();
    }

    // ------------------------------------------------------------- montagem

    private JPanel criarFormulario() {
        campoRetirada.setText(Formatos.hoje());

        JButton escolherLeitor = new JButton("Escolher leitor...");
        escolherLeitor.addActionListener(evento -> escolherLeitor());

        leitorEscolhido.setFont(leitorEscolhido.getFont().deriveFont(Font.BOLD));
        leitorEscolhido.setForeground(new Color(0x555555));

        JButton emprestar = new JButton("Registrar empréstimo");
        emprestar.addActionListener(evento -> emprestar());

        JPanel linhaTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        linhaTopo.add(escolherLeitor);
        linhaTopo.add(leitorEscolhido);

        JPanel linhaAcao = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        linhaAcao.add(new JLabel("Retirada:"));
        linhaAcao.add(campoRetirada);
        linhaAcao.add(emprestar);
        linhaAcao.add(new JLabel("    Filtrar catálogo:"));
        linhaAcao.add(filtro);

        JButton limpar = new JButton("Limpar marcações");
        limpar.addActionListener(evento -> desmarcarTudo());
        linhaAcao.add(limpar);

        contador.setFont(contador.getFont().deriveFont(Font.BOLD));
        linhaAcao.add(contador);

        filtro.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarCatalogo();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarCatalogo();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarCatalogo();
            }
        });

        // Marcar ou desmarcar um item atualiza o contador na hora.
        modeloCatalogo.addTableModelListener(evento -> atualizarContador());

        JScrollPane rolagem = new JScrollPane(tabelaCatalogo);
        rolagem.setPreferredSize(new Dimension(600, 158));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.add(linhaTopo, BorderLayout.NORTH);
        cabecalho.add(linhaAcao, BorderLayout.SOUTH);

        JPanel formulario = new JPanel(new BorderLayout(0, 4));
        formulario.setBorder(BorderFactory.createTitledBorder(
                "Novo empréstimo: escolha o leitor e marque os itens do catálogo"));
        formulario.add(cabecalho, BorderLayout.NORTH);
        formulario.add(rolagem, BorderLayout.CENTER);
        return formulario;
    }

    private JScrollPane criarTabelaDeEmprestimos() {
        JScrollPane rolagem = new JScrollPane(tabelaEmprestimos);
        rolagem.setBorder(BorderFactory.createTitledBorder("Empréstimos registrados"));
        return rolagem;
    }

    private JPanel criarBarraDeDevolucao() {
        campoDevolucao.setText(Formatos.hoje());

        JButton devolver = new JButton("Registrar devolução");
        devolver.addActionListener(evento -> devolver());

        apenasAtivos.addActionListener(evento -> atualizarTabelaDeEmprestimos());

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        esquerda.add(apenasAtivos);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        direita.add(new JLabel("Data da devolução:"));
        direita.add(campoDevolucao);
        direita.add(devolver);

        JPanel barra = new JPanel(new BorderLayout());
        barra.add(esquerda, BorderLayout.WEST);
        barra.add(direita, BorderLayout.EAST);
        return barra;
    }

    private void prepararTabelas() {
        tabelaCatalogo.setRowHeight(24);
        tabelaCatalogo.getColumnModel().getColumn(0).setPreferredWidth(30);
        tabelaCatalogo.getColumnModel().getColumn(0).setMaxWidth(34);
        tabelaCatalogo.getColumnModel().getColumn(1).setPreferredWidth(70);
        tabelaCatalogo.getColumnModel().getColumn(2).setPreferredWidth(280);

        tabelaEmprestimos.setRowHeight(24);
        tabelaEmprestimos.setAutoCreateRowSorter(true);
        tabelaEmprestimos.getColumnModel().getColumn(0).setPreferredWidth(36);
        tabelaEmprestimos.getColumnModel().getColumn(1).setPreferredWidth(160);
        tabelaEmprestimos.getColumnModel().getColumn(2).setPreferredWidth(180);

        tabelaEmprestimos.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabela, Object valor,
                    boolean selecionada, boolean comFoco, int linha, int coluna) {

                Component celula = super.getTableCellRendererComponent(
                        tabela, valor, selecionada, comFoco, linha, coluna);
                if (!selecionada) {
                    if ("Em atraso".equals(valor)) {
                        celula.setForeground(new Color(0xB00020));
                    } else if ("Devolvido".equals(valor)) {
                        celula.setForeground(new Color(0x666666));
                    } else {
                        celula.setForeground(new Color(0x1B6B2F));
                    }
                }
                return celula;
            }
        });
    }

    // ----------------------------------------------------------- atualização

    @Override
    public void atualizar() {
        conferirLeitorEscolhido();
        atualizarCatalogo();
        atualizarTabelaDeEmprestimos();
    }

    /**
     * O leitor pode ter sido removido em outra aba, ou ter pego mais itens.
     * A etiqueta é remontada para não mostrar informação velha.
     */
    private void conferirLeitorEscolhido() {
        if (leitor != null && !biblioteca.getLeitores().contains(leitor)) {
            leitor = null;
        }
        mostrarLeitor();
    }

    private void mostrarLeitor() {
        if (leitor == null) {
            leitorEscolhido.setText("nenhum leitor escolhido");
            leitorEscolhido.setForeground(new Color(0x555555));
            return;
        }

        boolean atrasado = biblioteca.temAtraso(leitor, LocalDate.now());
        leitorEscolhido.setText(leitor.getNome() + "  (" + leitor.getMatricula() + ")"
                + "  |  " + biblioteca.contarItensEmMaos(leitor)
                + " de " + Leitor.LIMITE_EMPRESTIMOS + " itens"
                + (atrasado ? "  |  EM ATRASO" : ""));
        leitorEscolhido.setForeground(atrasado ? new Color(0xB00020) : new Color(0x11397A));
    }

    private void atualizarCatalogo() {
        modeloCatalogo.setRowCount(0);
        catalogoExibido.clear();

        for (ItemAcervo obra : biblioteca.buscarObras(filtro.getText())) {
            catalogoExibido.add(obra);
            modeloCatalogo.addRow(new Object[]{
                    Boolean.FALSE,
                    obra.getCodigo(),
                    obra.getTitulo(),
                    obra.getTipo(),
                    obra.contarDisponiveis() + " de " + obra.getTotalDeExemplares(),
                    obra.getPrazoDias() + " dias",
                    Formatos.moeda(obra.getMultaPorDia())});
        }

        atualizarContador();
    }

    private void atualizarTabelaDeEmprestimos() {
        modeloEmprestimos.setRowCount(0);
        emprestimoPorLinha.clear();

        LocalDate hoje = LocalDate.now();

        for (Emprestimo emprestimo : biblioteca.getEmprestimos()) {
            if (apenasAtivos.isSelected() && emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
                continue;
            }

            for (ItemEmprestado emprestado : emprestimo.getItens()) {
                long atraso = emprestado.getDiasDeAtraso(hoje);

                String situacao;
                if (emprestado.estaDevolvido()) {
                    situacao = "Devolvido";
                } else if (atraso > 0) {
                    situacao = "Em atraso";
                } else {
                    situacao = "Em dia";
                }

                // Enquanto o item não volta, a multa mostrada é a projeção do
                // atraso até hoje, calculada pelo valor diário da obra.
                double multa = emprestado.estaDevolvido()
                        ? emprestado.getMulta()
                        : emprestado.getObra().calcularMulta(atraso);

                emprestimoPorLinha.add(emprestimo);
                modeloEmprestimos.addRow(new Object[]{
                        emprestimo.getId(),
                        emprestimo.getLeitor().getNome(),
                        emprestado.getObra().getTitulo(),
                        emprestado.getExemplar().getTombo(),
                        Formatos.data(emprestimo.getRetiradaEm()),
                        Formatos.data(emprestado.getPrevisaoDevolucao()),
                        Formatos.data(emprestado.getDevolvidoEm()),
                        atraso > 0 ? atraso + " dia(s)" : "-",
                        Formatos.moeda(multa),
                        situacao});
            }
        }
    }

    private void atualizarContador() {
        int marcados = getObrasMarcadas().size();
        if (marcados == 0) {
            contador.setText("nenhum item marcado");
            contador.setForeground(new Color(0x555555));
        } else {
            contador.setText(marcados + (marcados == 1 ? " item marcado" : " itens marcados"));
            contador.setForeground(new Color(0x11397A));
        }
    }

    // --------------------------------------------------------------- ações

    private void escolherLeitor() {
        DialogoBuscaLeitor dialogo = new DialogoBuscaLeitor(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this), biblioteca);
        dialogo.setVisible(true);

        if (dialogo.getSelecionado() != null) {
            leitor = dialogo.getSelecionado();
            mostrarLeitor();
        }
    }

    private List<ItemAcervo> getObrasMarcadas() {
        List<ItemAcervo> marcadas = new ArrayList<>();
        for (int linha = 0; linha < modeloCatalogo.getRowCount(); linha++) {
            if (Boolean.TRUE.equals(modeloCatalogo.getValueAt(linha, 0))) {
                marcadas.add(catalogoExibido.get(linha));
            }
        }
        return marcadas;
    }

    private void desmarcarTudo() {
        for (int linha = 0; linha < modeloCatalogo.getRowCount(); linha++) {
            modeloCatalogo.setValueAt(Boolean.FALSE, linha, 0);
        }
    }

    private void emprestar() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            // Encerra a edição em andamento, senão o último clique na caixa de
            // marcação ainda não teria chegado ao modelo da tabela.
            if (tabelaCatalogo.isEditing()) {
                tabelaCatalogo.getCellEditor().stopCellEditing();
            }

            List<ItemAcervo> escolhidas = getObrasMarcadas();
            LocalDate retirada = Formatos.lerData(campoRetirada.getText(), "data de retirada");

            Emprestimo emprestimo = biblioteca.emprestar(leitor, escolhidas, retirada);

            JOptionPane.showMessageDialog(this, montarRecibo(emprestimo),
                    "Empréstimo registrado", JOptionPane.INFORMATION_MESSAGE);
            atualizar();

        } catch (BibliotecaException e) {
            // Limite atingido, obra sem exemplar livre, leitor em atraso, data
            // inválida ou erro de gravação: todas chegam com mensagem pronta.
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Empréstimo não realizado", JOptionPane.WARNING_MESSAGE);
        } finally {
            // O finally garante que o cursor volte ao normal mesmo quando a
            // operação termina em exceção.
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void devolver() {
        int linha = tabelaEmprestimos.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione na tabela de baixo a linha do empréstimo a devolver.",
                    "Nenhum empréstimo selecionado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // A tabela pode estar ordenada por qualquer coluna, então a linha
        // clicada precisa ser convertida para a posição real dos dados.
        Emprestimo emprestimo = emprestimoPorLinha.get(
                tabelaEmprestimos.convertRowIndexToModel(linha));

        if (emprestimo.getQuantidadeDeItens() > 1) {
            int resposta = JOptionPane.showConfirmDialog(this,
                    "O empréstimo nº " + emprestimo.getId() + " tem "
                            + emprestimo.getQuantidadeDeItens() + " itens e todos serão devolvidos.\n"
                            + "Confirma?",
                    "Devolver empréstimo completo", JOptionPane.YES_NO_OPTION);
            if (resposta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            LocalDate data = Formatos.lerData(campoDevolucao.getText(), "data de devolução");
            double multa = biblioteca.devolver(emprestimo, data);

            String mensagem = (multa > 0)
                    ? "Devolução registrada com multa de " + Formatos.moeda(multa) + "."
                    : "Devolução registrada no prazo, sem multa.";
            JOptionPane.showMessageDialog(this, mensagem, "Devolução",
                    JOptionPane.INFORMATION_MESSAGE);
            atualizar();

        } catch (BibliotecaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Devolução não realizada", JOptionPane.WARNING_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private String montarRecibo(Emprestimo emprestimo) {
        StringBuilder texto = new StringBuilder();
        texto.append("Empréstimo nº ").append(emprestimo.getId())
                .append("\nLeitor: ").append(emprestimo.getLeitor().getNome()).append("\n\n");

        for (ItemEmprestado emprestado : emprestimo.getItens()) {
            texto.append(emprestado.getObra().getTitulo())
                    .append("  [tombo ").append(emprestado.getExemplar().getTombo()).append("]")
                    .append("\n    devolver até ")
                    .append(Formatos.data(emprestado.getPrevisaoDevolucao()))
                    .append("\n");
        }
        return texto.toString();
    }
}
