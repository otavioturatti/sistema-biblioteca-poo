package br.ufu.biblioteca.gui;

import br.ufu.biblioteca.modelo.Emprestimo;
import br.ufu.biblioteca.modelo.ItemAcervo;
import br.ufu.biblioteca.modelo.Leitor;
import br.ufu.biblioteca.modelo.StatusEmprestimo;
import br.ufu.biblioteca.persistencia.ArquivoCsv;
import br.ufu.biblioteca.servico.Biblioteca;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;

// Números gerais e o caminho da pasta de dados, para achar os arquivos fácil.
public class PainelResumo extends PainelBase {

    private final JPanel cartoes = new JPanel(new GridLayout(2, 3, 12, 12));
    private final JLabel caminho = new JLabel();

    public PainelResumo(Biblioteca biblioteca) {
        super(biblioteca);

        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        cartoes.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        caminho.setFont(caminho.getFont().deriveFont(Font.PLAIN, 11f));
        caminho.setForeground(new Color(0x555555));

        add(cartoes, BorderLayout.NORTH);
        add(caminho, BorderLayout.SOUTH);

        atualizar();
    }

    @Override
    public void atualizar() {
        cartoes.removeAll();

        int emprestados = biblioteca.contarExemplaresEmprestados();
        int total = biblioteca.contarExemplares();

        int ativos = 0;
        int atrasados = 0;
        LocalDate hoje = LocalDate.now();
        for (Emprestimo emprestimo : biblioteca.getEmprestimos()) {
            if (emprestimo.getStatus() == StatusEmprestimo.ATIVO) {
                ativos++;
                if (emprestimo.estaAtrasado(hoje)) {
                    atrasados++;
                }
            }
        }

        cartoes.add(criarCartao("Obras catalogadas",
                String.valueOf(biblioteca.getAcervo().size()), new Color(0x11397A)));
        cartoes.add(criarCartao("Exemplares", String.valueOf(total), new Color(0x11397A)));
        cartoes.add(criarCartao("Disponíveis", String.valueOf(total - emprestados), new Color(0x1B6B2F)));
        cartoes.add(criarCartao("Emprestados", String.valueOf(emprestados), new Color(0x8A6D00)));
        cartoes.add(criarCartao("Empréstimos em aberto", ativos + " (" + atrasados + " em atraso)",
                atrasados > 0 ? new Color(0xB00020) : new Color(0x1B6B2F)));
        cartoes.add(criarCartao("Multas acumuladas",
                Formatos.moeda(biblioteca.getTotalDeMultas()), new Color(0xB00020)));

        caminho.setText("<html>Dados gravados em: " + ArquivoCsv.getPasta()
                + "<br>Acervo por tipo: " + contarPorTipo() + "</html>");

        cartoes.revalidate();
        cartoes.repaint();
    }

    private String contarPorTipo() {
        int livros = 0;
        int revistas = 0;
        int dvds = 0;
        for (ItemAcervo item : biblioteca.getAcervo()) {
            if ("Livro".equals(item.getTipo())) {
                livros++;
            } else if ("Revista".equals(item.getTipo())) {
                revistas++;
            } else {
                dvds++;
            }
        }
        return livros + " livros, " + revistas + " revistas, " + dvds + " DVDs"
                + "  |  " + biblioteca.getLeitores().size() + " leitores cadastrados"
                + "  |  limite por leitor: " + Leitor.LIMITE_EMPRESTIMOS + " itens";
    }

    private JPanel criarCartao(String titulo, String valor, Color cor) {
        JLabel rotulo = new JLabel(titulo);
        rotulo.setForeground(new Color(0x555555));

        JLabel numero = new JLabel(valor);
        numero.setForeground(cor);
        numero.setFont(numero.getFont().deriveFont(Font.BOLD, 20f));

        JPanel cartao = new JPanel(new BorderLayout(0, 6));
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD5D5D5)),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        cartao.add(rotulo, BorderLayout.NORTH);
        cartao.add(numero, BorderLayout.CENTER);
        return cartao;
    }
}
