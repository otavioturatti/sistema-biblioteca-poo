package br.ufu.biblioteca.persistencia;

import br.ufu.biblioteca.excecao.PersistenciaException;
import br.ufu.biblioteca.modelo.Leitor;

import java.util.ArrayList;
import java.util.List;

// Linha: matricula;nome;cpf;email
public class RepositorioLeitores {

    private static final String ARQUIVO = "leitores.csv";

    public void salvar(List<Leitor> leitores) throws PersistenciaException {
        List<String> linhas = new ArrayList<>();

        for (Leitor leitor : leitores) {
            linhas.add(String.join(ArquivoCsv.SEPARADOR,
                    leitor.getMatricula(),
                    leitor.getNome(),
                    leitor.getCpf(),
                    leitor.getEmail()));
        }

        ArquivoCsv.gravar(ARQUIVO, linhas);
    }

    public List<Leitor> carregar() throws PersistenciaException {
        List<Leitor> leitores = new ArrayList<>();

        for (String[] campos : ArquivoCsv.ler(ARQUIVO)) {
            if (campos.length < 4) {
                throw new PersistenciaException(
                        "Linha incompleta no arquivo " + ARQUIVO + ".", null);
            }
            leitores.add(new Leitor(campos[0], campos[1], campos[2], campos[3]));
        }

        return leitores;
    }
}
