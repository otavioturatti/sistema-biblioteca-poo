package br.ufu.biblioteca.modelo;

// Cópia física de uma obra. O construtor não é public porque só o ItemAcervo
// cria os exemplares dele.
public class Exemplar {

    private final ItemAcervo obra;
    private final String tombo;
    private boolean disponivel;

    Exemplar(ItemAcervo obra, String tombo) {
        this.obra = obra;
        this.tombo = tombo;
        this.disponivel = true;
    }

    public ItemAcervo getObra() {
        return obra;
    }

    // Formato CODIGO-1, CODIGO-2, ...
    public String getTombo() {
        return tombo;
    }

    public boolean estaDisponivel() {
        return disponivel;
    }

    public void marcarEmprestado() {
        this.disponivel = false;
    }

    public void marcarDisponivel() {
        this.disponivel = true;
    }

    @Override
    public String toString() {
        return tombo + " - " + obra.getTitulo();
    }
}
