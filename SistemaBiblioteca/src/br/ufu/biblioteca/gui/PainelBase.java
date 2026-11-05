package br.ufu.biblioteca.gui;

import br.ufu.biblioteca.servico.Biblioteca;

import javax.swing.JPanel;
import java.awt.BorderLayout;

// Superclasse das abas. Toda aba precisa saber se redesenhar quando os dados
// mudam em outra tela.
public abstract class PainelBase extends JPanel {

    protected final Biblioteca biblioteca;

    protected PainelBase(Biblioteca biblioteca) {
        super(new BorderLayout(8, 8));
        this.biblioteca = biblioteca;
    }

    public abstract void atualizar();
}
