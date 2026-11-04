package br.ufu.biblioteca.persistencia;

import br.ufu.biblioteca.excecao.PersistenciaException;
import br.ufu.biblioteca.modelo.Exemplar;
import br.ufu.biblioteca.modelo.ItemAcervo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Grava e recupera as cópias físicas de cada obra.
 *
 * Linha: codigoDaObra;tombo;disponivel
 *
 * Os exemplares ficam em arquivo separado porque uma obra pode ter dezenas
 * deles. A ligação é pelo código da obra, do mesmo jeito que duas tabelas se
 * ligam por uma chave.
 */
public class RepositorioExemplares {

    private static final String ARQUIVO = "exemplares.csv";

    public void salvar(List<ItemAcervo> acervo) throws PersistenciaException {
        List<String> linhas = new ArrayList<>();

        for (ItemAcervo obra : acervo) {
            for (Exemplar exemplar : obra.getExemplares()) {
                linhas.add(String.join(ArquivoCsv.SEPARADOR,
                        obra.getCodigo(),
                        exemplar.getTombo(),
                        String.valueOf(exemplar.estaDisponivel())));
            }
        }

        ArquivoCsv.gravar(ARQUIVO, linhas);
    }

    /**
     * Recria os exemplares dentro das obras já carregadas e devolve um índice
     * por tombo, que o repositório de empréstimos usa para reapontar cada
     * linha ao exemplar certo.
     */
    public Map<String, Exemplar> carregar(Map<String, ItemAcervo> obras) throws PersistenciaException {
        Map<String, Exemplar> porTombo = new LinkedHashMap<>();

        for (String[] campos : ArquivoCsv.ler(ARQUIVO)) {
            if (campos.length < 3) {
                throw new PersistenciaException(
                        "Linha incompleta no arquivo " + ARQUIVO + ".", null);
            }

            ItemAcervo obra = obras.get(campos[0]);

            // Exemplar órfão: a obra foi removida do acervo e a linha ficou
            // para trás. Ignorar deixa o sistema abrir mesmo assim.
            if (obra == null) {
                continue;
            }

            Exemplar exemplar = obra.restaurarExemplar(campos[1], Boolean.parseBoolean(campos[2]));
            porTombo.put(exemplar.getTombo(), exemplar);
        }

        return porTombo;
    }
}
