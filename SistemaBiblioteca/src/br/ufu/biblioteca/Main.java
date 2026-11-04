package br.ufu.biblioteca;

import br.ufu.biblioteca.excecao.PersistenciaException;
import br.ufu.biblioteca.gui.TelaLogin;
import br.ufu.biblioteca.servico.Biblioteca;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada. Carrega os dados gravados e abre a tela de login.
 *
 * Projeto Final de Programação Orientada a Objetos
 * Faculdade de Computação - Universidade Federal de Uberlândia
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Deixa as janelas com a aparência do sistema operacional.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Falhar aqui não impede o programa de rodar: o Swing cai na
            // aparência padrão dele e o sistema abre do mesmo jeito.
        }

        // Toda a interface é construída na thread de eventos do Swing.
        SwingUtilities.invokeLater(() -> {
            Biblioteca biblioteca = new Biblioteca();
            try {
                biblioteca.carregarDados();
                new TelaLogin(biblioteca).setVisible(true);

            } catch (PersistenciaException e) {
                // Sem os dados não há sistema, então o erro é mostrado com o
                // motivo técnico guardado na causa e o programa encerra.
                String detalhe = (e.getCause() == null) ? "" : "\n\nDetalhe: " + e.getCause();
                JOptionPane.showMessageDialog(null, e.getMessage() + detalhe,
                        "Erro ao abrir o sistema", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
