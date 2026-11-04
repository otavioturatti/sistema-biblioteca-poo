package br.ufu.biblioteca.modelo;

/**
 * Uma cópia física de um item do acervo. A biblioteca pode ter vários
 * exemplares do mesmo título, e é o exemplar que vai para o empréstimo, não
 * a obra.
 *
 * O construtor tem visibilidade de pacote porque só o ItemAcervo cria os
 * próprios exemplares: um exemplar não existe fora da obra a que pertence.
 */
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

    // Número de patrimônio da cópia, no formato CODIGO-1, CODIGO-2 e assim por diante.
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
