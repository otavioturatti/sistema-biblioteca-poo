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

/**
 * Janela principal. As abas montadas dependem do perfil de quem entrou: o
 * bibliotecário administra o sistema, o leitor apenas consulta. Quem responde
 * essa pergunta é o próprio objeto, pelo método podeGerenciarAcervo().
 */
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
            // O leitor só chega aqui se for um Leitor, mas o instanceof deixa
            // explícito que a conversão de tipo é segura.
            if (usuario instanceof Leitor) {
                abas.addTab("Meus empréstimos", new PainelMeusEmprestimos(biblioteca, (Leitor) usuario));
            }
            abas.addTab("Acervo", new PainelAcervo(biblioteca, false));
        }

        // Cada aba recarrega os dados ao ser aberta, assim um empréstimo feito
        // em uma tela já aparece na outra.
        abas.addChangeListener(evento -> atualizarAbaSelecionada());

        add(criarCabecalho(usuario), BorderLayout.NORTH);
        add(abas, BorderLayout.CENTER);

        setSize(new Dimension(1000, 700));
        setMinimumSize(new Dimension(880, 600));
        setLocationRelativeTo(null);
    }

    private void atualizarAbaSelecionada() {
        java.awt.Component selecionada = abas.getSelectedComponent();
        // Todas as abas herdam de PainelBase, então a chamada é a mesma para
        // qualquer uma delas: entra em ação a versão de atualizar() do painel.
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

    /**
     * Encerra a sessão e devolve a tela de login, sem fechar o programa. Os
     * dados já estão gravados a cada operação, então não há nada pendente
     * para salvar neste ponto.
     */
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
