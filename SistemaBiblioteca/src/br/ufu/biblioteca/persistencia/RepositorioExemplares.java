package br.ufu.biblioteca.persistencia;

import br.ufu.biblioteca.excecao.PersistenciaException;
import br.ufu.biblioteca.modelo.Exemplar;
import br.ufu.biblioteca.modelo.ItemAcervo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Linha: codigoDaObra;tombo;disponivel
// Ficam em arquivo separado porque uma obra pode ter dezenas de cópias.
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

    // Devolve o índice por tombo, que o repositório de empréstimos usa depois
    public Map<String, Exemplar> carregar(Map<String, ItemAcervo> obras) throws PersistenciaException {
        Map<String, Exemplar> porTombo = new LinkedHashMap<>();

        for (String[] campos : ArquivoCsv.ler(ARQUIVO)) {
            if (campos.length < 3) {
                throw new PersistenciaException(
                        "Linha incompleta no arquivo " + ARQUIVO + ".", null);
            }

            ItemAcervo obra = obras.get(campos[0]);

            // a obra foi removida e a linha ficou para trás
            if (obra == null) {
                continue;
            }

            Exemplar exemplar = obra.restaurarExemplar(campos[1], Boolean.parseBoolean(campos[2]));
            porTombo.put(exemplar.getTombo(), exemplar);
        }

        return porTombo;
    }
}
