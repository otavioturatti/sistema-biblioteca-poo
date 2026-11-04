package br.ufu.biblioteca.gui;

import br.ufu.biblioteca.excecao.BibliotecaException;
import br.ufu.biblioteca.modelo.Pessoa;
import br.ufu.biblioteca.servico.Biblioteca;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Porta de entrada do sistema. Não decide quem pode entrar: entrega o que foi
 * digitado para a Biblioteca e reage à exceção que voltar.
 */
public class TelaLogin extends JFrame {

    private final Biblioteca biblioteca;

    private final JTextField campoIdentificador = new JTextField(16);
    private final JPasswordField campoSenha = new JPasswordField(16);

    public TelaLogin(Biblioteca biblioteca) {
        super("Biblioteca UFU - Acesso");
        this.biblioteca = biblioteca;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarFormulario(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(420, getHeight()));
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setBackground(new Color(0x11397A));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        JLabel titulo = new JLabel("Sistema de Biblioteca");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Faculdade de Computação - UFU");
        subtitulo.setForeground(new Color(0xC3D2F0));
        subtitulo.setAlignmentX(CENTER_ALIGNMENT);

        cabecalho.add(titulo);
        cabecalho.add(Box.createVerticalStrut(4));
        cabecalho.add(subtitulo);
        return cabecalho;
    }

    private JPanel criarFormulario() {
        JPanel campos = new JPanel(new GridLayout(2, 2, 10, 12));
        campos.setBorder(BorderFactory.createEmptyBorder(24, 28, 10, 28));
        campos.add(new JLabel("Matrícula ou SIAPE:"));
        campos.add(campoIdentificador);
        campos.add(new JLabel("Senha:"));
        campos.add(campoSenha);

        JButton entrar = new JButton("Entrar");
        entrar.addActionListener(evento -> entrar());

        // Enter em qualquer um dos campos também faz login.
        getRootPane().setDefaultButton(entrar);

        JPanel botoes = new JPanel();
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 28, 16, 28));
        botoes.add(entrar);

        JPanel formulario = new JPanel(new BorderLayout());
        formulario.add(campos, BorderLayout.CENTER);
        formulario.add(botoes, BorderLayout.SOUTH);
        return formulario;
    }

    private JPanel criarRodape() {
        JLabel dica = new JLabel(
                "<html><center>Bibliotecário entra com o SIAPE e a senha.<br>"
                        + "Leitor entra com a matrícula e os 4 últimos dígitos do CPF.</center></html>",
                SwingConstants.CENTER);
        dica.setFont(dica.getFont().deriveFont(Font.PLAIN, 11f));
        dica.setForeground(new Color(0x555555));

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(new Color(0xF0F0F0));
        rodape.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        rodape.add(dica, BorderLayout.CENTER);
        return rodape;
    }

    private void entrar() {
        String identificador = campoIdentificador.getText().trim();
        String senha = new String(campoSenha.getPassword());

        try {
            Pessoa usuario = biblioteca.autenticar(identificador, senha);

            // Login aceito: a tela principal assume e esta é descartada.
            new TelaPrincipal(biblioteca, usuario).setVisible(true);
            dispose();

        } catch (BibliotecaException e) {
            // Uma única captura cobre erro de login e qualquer falha de
            // arquivo, porque todas as exceções do sistema têm a mesma base.
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Não foi possível entrar", JOptionPane.WARNING_MESSAGE);
            campoSenha.setText("");
            campoSenha.requestFocusInWindow();
        }
    }
}
