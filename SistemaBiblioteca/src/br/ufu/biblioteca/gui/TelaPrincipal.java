package br.ufu.biblioteca.gui;

import br.ufu.biblioteca.modelo.Leitor;
import br.ufu.biblioteca.modelo.Pessoa;
import br.ufu.biblioteca.servico.Biblioteca;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

// As abas montadas dependem do perfil de quem entrou, e quem responde isso
// é o próprio objeto.
public class TelaPrincipal extends JFrame {

    private final JTabbedPane abas = new JTabbedPane();
    private final Biblioteca biblioteca;

    public TelaPrincipal(Biblioteca biblioteca, Pessoa usuario) {
        super("Biblioteca UFU");
        this.biblioteca = biblioteca;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        if (usuario.podeGerenciarAcervo()) {
            abas.addTab("Empréstimos", new PainelEmprestimos(biblioteca));
            abas.addTab("Acervo", new PainelAcervo(biblioteca, true));
            abas.addTab("Leitores", new PainelLeitores(biblioteca));
            abas.addTab("Resumo", new PainelResumo(biblioteca));
        } else {
            // o instanceof deixa claro que a conversão é segura
            if (usuario instanceof Leitor) {
                abas.addTab("Meus empréstimos", new PainelMeusEmprestimos(biblioteca, (Leitor) usuario));
            }
            abas.addTab("Acervo", new PainelAcervo(biblioteca, false));
        }

        // cada aba recarrega ao ser aberta, senão mostra dado velho
        abas.addChangeListener(evento -> atualizarAbaSelecionada());

        add(criarCabecalho(usuario), BorderLayout.NORTH);
        add(abas, BorderLayout.CENTER);

        setSize(new Dimension(1000, 700));
        setMinimumSize(new Dimension(880, 600));
        setLocationRelativeTo(null);
    }

    private void atualizarAbaSelecionada() {
        java.awt.Component selecionada = abas.getSelectedComponent();
        // todas as abas herdam de PainelBase, então a chamada é a mesma
        if (selecionada instanceof PainelBase) {
            ((PainelBase) selecionada).atualizar();
        }
    }

    private JPanel criarCabecalho(Pessoa usuario) {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(new Color(0x11397A));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel titulo = new JLabel("Sistema de Biblioteca");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));

        JLabel identificacao = new JLabel(usuario.getNome() + "  |  " + usuario.getPerfil());
        identificacao.setForeground(new Color(0xC3D2F0));

        JButton sair = new JButton("Sair");
        sair.addActionListener(evento -> sair());

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        direita.setOpaque(false);
        direita.add(identificacao);
        direita.add(sair);

        cabecalho.add(titulo, BorderLayout.WEST);
        cabecalho.add(direita, BorderLayout.EAST);
        return cabecalho;
    }

    // Volta para o login sem fechar o programa. Não precisa salvar nada:
    // cada operação já grava na hora.
    private void sair() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Encerrar a sessão e voltar para a tela de acesso?",
                "Sair", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        new TelaLogin(biblioteca).setVisible(true);
        dispose();
    }
}
