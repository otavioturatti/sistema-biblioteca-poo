package br.ufu.biblioteca;

import br.ufu.biblioteca.excecao.PersistenciaException;
import br.ufu.biblioteca.gui.TelaLogin;
import br.ufu.biblioteca.servico.Biblioteca;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

// Projeto final de Programação Orientada a Objetos
// Faculdade de Computação, Universidade Federal de Uberlândia
public class Main {

    public static void main(String[] args) {
        try {
            // deixa as janelas com a cara do sistema operacional
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // se falhar, o Swing usa a aparência padrão e o sistema abre igual
        }

        // a interface tem que ser montada na thread de eventos do Swing
        SwingUtilities.invokeLater(() -> {
            Biblioteca biblioteca = new Biblioteca();
            try {
                biblioteca.carregarDados();
                new TelaLogin(biblioteca).setVisible(true);

            } catch (PersistenciaException e) {
                // sem os dados não tem sistema, então mostra o erro e fecha
                String detalhe = (e.getCause() == null) ? "" : "\n\nDetalhe: " + e.getCause();
                JOptionPane.showMessageDialog(null, e.getMessage() + detalhe,
                        "Erro ao abrir o sistema", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
