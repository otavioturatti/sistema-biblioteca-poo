package br.ufu.biblioteca.gui;

import br.ufu.biblioteca.excecao.BibliotecaException;
import br.ufu.biblioteca.modelo.Dvd;
import br.ufu.biblioteca.modelo.ItemAcervo;
import br.ufu.biblioteca.modelo.Livro;
import br.ufu.biblioteca.modelo.Revista;
import br.ufu.biblioteca.servico.Biblioteca;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.time.LocalDate;

/**
 * Formulário de item do acervo, usado tanto para cadastrar quanto para
 * editar. A mesma tela serve aos dois casos porque os campos são os mesmos:
 * o que muda é que na edição o código e o tipo ficam travados, já que são a
 * identidade do registro e dos tombos colados nos exemplares.
 *
 * O campo de detalhe muda de rótulo conforme o tipo, porque autor, edição e
 * duração ocupam o mesmo lugar na tela mas pertencem a classes diferentes.
 */
public class DialogoItem extends JDialog {

    private final Biblioteca biblioteca;

    // Nulo quando é um cadastro novo; preenchido quando é edição.
    private final ItemAcervo emEdicao;

    private final JComboBox<String> tipo = new JComboBox<>(new String[]{"Livro", "Revista", "DVD"});
    private final JTextField codigo = new JTextField(14);
    private final JTextField titulo = new JTextField(14);
    private final JTextField ano = new JTextField(14);
    private final JTextField detalhe = new JTextField(14);
    private final JTextField prazo = new JTextField(14);
    private final JTextField multa = new JTextField(14);
    private final JTextField exemplares = new JTextField(14);
    private final JLabel rotuloDetalhe = new JLabel("Autor:");
    private final JLabel aviso = new JLabel(" ");

    public DialogoItem(Frame dono, Biblioteca biblioteca) {
        this(dono, biblioteca, null);
    }

    public DialogoItem(Frame dono, Biblioteca biblioteca, ItemAcervo obra) {
        super(dono, (obra == null) ? "Cadastrar item do acervo" : "Editar item do acervo", true);
        this.biblioteca = biblioteca;
        this.emEdicao = obra;

        setLayout(new BorderLayout());
        add(criarCampos(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);

        tipo.addActionListener(evento -> aplicarPadroesDoTipo());

        if (obra == null) {
            aplicarPadroesDoTipo();
            exemplares.setText("1");
        } else {
            preencherCom(obra);
        }

        pack();
        setMinimumSize(new Dimension(440, getHeight()));
        setLocationRelativeTo(dono);
        setResizable(false);
    }

    private JPanel criarCampos() {
        JPanel campos = new JPanel(new GridLayout(8, 2, 8, 9));
        campos.setBorder(BorderFactory.createEmptyBorder(16, 20, 6, 20));
        campos.add(new JLabel("Tipo:"));
        campos.add(tipo);
        campos.add(new JLabel("Código:"));
        campos.add(codigo);
        campos.add(new JLabel("Título:"));
        campos.add(titulo);
        campos.add(new JLabel("Ano de publicação:"));
        campos.add(ano);
        campos.add(rotuloDetalhe);
        campos.add(detalhe);
        campos.add(new JLabel("Prazo de empréstimo (dias):"));
        campos.add(prazo);
        campos.add(new JLabel("Multa por dia de atraso (R$):"));
        campos.add(multa);
        campos.add(new JLabel("Exemplares no acervo:"));
        campos.add(exemplares);
        return campos;
    }

    private JPanel criarRodape() {
        aviso.setFont(aviso.getFont().deriveFont(Font.PLAIN, 11f));
        aviso.setForeground(new Color(0x555555));
        aviso.setBorder(BorderFactory.createEmptyBorder(0, 22, 6, 12));

        JButton salvar = new JButton("Salvar");
        salvar.addActionListener(evento -> salvar());

        JButton cancelar = new JButton("Cancelar");
        cancelar.addActionListener(evento -> dispose());

        getRootPane().setDefaultButton(salvar);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 20, 8, 12));
        botoes.add(cancelar);
        botoes.add(salvar);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(aviso, BorderLayout.NORTH);
        rodape.add(botoes, BorderLayout.SOUTH);
        return rodape;
    }

    /**
     * Troca o rótulo do detalhe e recarrega prazo e multa com as constantes da
     * classe correspondente, sem precisar criar o objeto para consultá-las.
     */
    private void aplicarPadroesDoTipo() {
        String escolhido = (String) tipo.getSelectedItem();

        if ("Revista".equals(escolhido)) {
            rotuloDetalhe.setText("Edição:");
            prazo.setText(String.valueOf(Revista.PRAZO_PADRAO));
            multa.setText(Formatos.numero(Revista.MULTA_PADRAO));
        } else if ("DVD".equals(escolhido)) {
            rotuloDetalhe.setText("Duração (min):");
            prazo.setText(String.valueOf(Dvd.PRAZO_PADRAO));
            multa.setText(Formatos.numero(Dvd.MULTA_PADRAO));
        } else {
            rotuloDetalhe.setText("Autor:");
            prazo.setText(String.valueOf(Livro.PRAZO_PADRAO));
            multa.setText(Formatos.numero(Livro.MULTA_PADRAO));
        }

        if (ano.getText().trim().isEmpty()) {
            ano.setText(String.valueOf(LocalDate.now().getYear()));
        }
        aviso.setText("Prazo e multa já vêm com o padrão do tipo. Altere só se esta obra tiver regra própria.");
    }

    private void preencherCom(ItemAcervo obra) {
        tipo.setSelectedItem(obra.getTipo());
        tipo.setEnabled(false);
        codigo.setText(obra.getCodigo());
        codigo.setEnabled(false);

        rotuloDetalhe.setText(obra.getRotuloDetalhe() + ":");
        titulo.setText(obra.getTitulo());
        ano.setText(String.valueOf(obra.getAno()));
        detalhe.setText(valorEditavelDoDetalhe(obra));
        prazo.setText(String.valueOf(obra.getPrazoDias()));
        multa.setText(Formatos.numero(obra.getMultaPorDia()));
        exemplares.setText(String.valueOf(obra.getTotalDeExemplares()));

        aviso.setText("Código e tipo não mudam. "
                + obra.contarEmprestados() + " exemplar(es) emprestado(s) no momento.");
    }

    /**
     * Revista e DVD guardam número, mas mostram "Edição 42" e "120 min". No
     * formulário entra só o número, que é o que o usuário edita.
     */
    private String valorEditavelDoDetalhe(ItemAcervo obra) {
        if (!obra.detalheENumerico()) {
            return obra.getDetalhe();
        }
        return obra.getDetalhe().replaceAll("[^0-9]", "");
    }

    private void salvar() {
        try {
            if (emEdicao != null) {
                biblioteca.editarItem(emEdicao, titulo.getText(), ano.getText(),
                        detalhe.getText(), prazo.getText(), multa.getText(), exemplares.getText());
                JOptionPane.showMessageDialog(this, "Item atualizado.",
                        "Pronto", JOptionPane.INFORMATION_MESSAGE);
            } else {
                cadastrar();
                JOptionPane.showMessageDialog(this, "Item cadastrado no acervo.",
                        "Pronto", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();

        } catch (BibliotecaException e) {
            // Campo vazio, código repetido, ano fora do intervalo, prazo ou
            // multa inválidos, exemplares de menos: a janela segue aberta com
            // o que já foi digitado.
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Não foi possível salvar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cadastrar() throws BibliotecaException {
        String escolhido = (String) tipo.getSelectedItem();

        // Cada tipo tem o seu método de cadastro porque os dados específicos
        // e as validações são diferentes.
        if ("Revista".equals(escolhido)) {
            biblioteca.cadastrarRevista(codigo.getText(), titulo.getText(), ano.getText(),
                    detalhe.getText(), prazo.getText(), multa.getText(), exemplares.getText());
        } else if ("DVD".equals(escolhido)) {
            biblioteca.cadastrarDvd(codigo.getText(), titulo.getText(), ano.getText(),
                    detalhe.getText(), prazo.getText(), multa.getText(), exemplares.getText());
        } else {
            biblioteca.cadastrarLivro(codigo.getText(), titulo.getText(), ano.getText(),
                    detalhe.getText(), prazo.getText(), multa.getText(), exemplares.getText());
        }
    }
}
