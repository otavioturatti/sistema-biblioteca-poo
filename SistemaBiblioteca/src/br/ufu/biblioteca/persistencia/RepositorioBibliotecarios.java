package br.ufu.biblioteca.persistencia;

import br.ufu.biblioteca.excecao.PersistenciaException;
import br.ufu.biblioteca.modelo.Bibliotecario;

import java.util.ArrayList;
import java.util.List;

// Linha: siape;nome;cpf;email;senha
public class RepositorioBibliotecarios {

    private static final String ARQUIVO = "bibliotecarios.csv";

    public void salvar(List<Bibliotecario> bibliotecarios) throws PersistenciaException {
        List<String> linhas = new ArrayList<>();

        for (Bibliotecario bibliotecario : bibliotecarios) {
            linhas.add(String.join(ArquivoCsv.SEPARADOR,
                    bibliotecario.getSiape(),
                    bibliotecario.getNome(),
                    bibliotecario.getCpf(),
                    bibliotecario.getEmail(),
                    bibliotecario.getSenha()));
        }

        ArquivoCsv.gravar(ARQUIVO, linhas);
    }

    public List<Bibliotecario> carregar() throws PersistenciaException {
        List<Bibliotecario> bibliotecarios = new ArrayList<>();

        for (String[] campos : ArquivoCsv.ler(ARQUIVO)) {
            if (campos.length < 5) {
                throw new PersistenciaException(
                        "Linha incompleta no arquivo " + ARQUIVO + ".", null);
            }
            bibliotecarios.add(new Bibliotecario(
                    campos[0], campos[1], campos[2], campos[3], campos[4]));
        }

        return bibliotecarios;
    }
}
