package br.ufu.biblioteca.gui;

import br.ufu.biblioteca.excecao.BibliotecaException;
import br.ufu.biblioteca.servico.Biblioteca;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

public class DialogoLeitor extends JDialog {

    private final Biblioteca biblioteca;

    private final JTextField matricula = new JTextField(14);
    private final JTextField nome = new JTextField(14);
    private final JTextField cpf = new JTextField(14);
    private final JTextField email = new JTextField(14);

    public DialogoLeitor(Frame dono, Biblioteca biblioteca) {
        super(dono, "Cadastrar leitor", true);
        this.biblioteca = biblioteca;

        setLayout(new BorderLayout());
        add(criarCampos(), BorderLayout.CENTER);
        add(criarBotoes(), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(400, getHeight()));
        setLocationRelativeTo(dono);
        setResizable(false);
    }

    private JPanel criarCampos() {
        JPanel campos = new JPanel(new GridLayout(5, 2, 8, 10));
        campos.setBorder(BorderFactory.createEmptyBorder(18, 20, 10, 20));
        campos.add(new JLabel("Matrícula:"));
        campos.add(matricula);
        campos.add(new JLabel("Nome:"));
        campos.add(nome);
        campos.add(new JLabel("CPF (11 dígitos):"));
        campos.add(cpf);
        campos.add(new JLabel("E-mail:"));
        campos.add(email);
        campos.add(new JLabel(""));
        campos.add(new JLabel("<html><i>A senha será os 4<br>últimos dígitos do CPF.</i></html>"));
        return campos;
    }

    private JPanel criarBotoes() {
        JButton salvar = new JButton("Salvar");
        salvar.addActionListener(evento -> salvar());

        JButton cancelar = new JButton("Cancelar");
        cancelar.addActionListener(evento -> dispose());

        getRootPane().setDefaultButton(salvar);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 12));
        botoes.add(cancelar);
        botoes.add(salvar);
        return botoes;
    }

    private void salvar() {
        try {
            biblioteca.cadastrarLeitor(matricula.getText(), nome.getText(),
                    cpf.getText(), email.getText());

            JOptionPane.showMessageDialog(this, "Leitor cadastrado.",
                    "Pronto", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (BibliotecaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Cadastro não realizado", JOptionPane.WARNING_MESSAGE);
        }
    }
}
