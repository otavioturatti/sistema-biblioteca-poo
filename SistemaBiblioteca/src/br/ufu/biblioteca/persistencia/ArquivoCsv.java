package br.ufu.biblioteca.persistencia;

import br.ufu.biblioteca.excecao.PersistenciaException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Leitura e gravação dos arquivos de dados. Todos os repositórios passam por
 * aqui, então o tratamento de IOException fica concentrado em um lugar só.
 *
 * O formato escolhido foi CSV em texto puro para que os dados possam ser
 * abertos em qualquer editor durante a apresentação, mostrando que o sistema
 * realmente gravou o que foi cadastrado.
 */
public final class ArquivoCsv {

    public static final String SEPARADOR = ";";

    private static final Path PASTA = Paths.get("dados");

    // Classe utilitária: o construtor privado impede que alguém a instancie.
    private ArquivoCsv() {
    }

    public static List<String[]> ler(String arquivo) throws PersistenciaException {
        List<String[]> linhas = new ArrayList<>();
        Path caminho = PASTA.resolve(arquivo);

        // Primeira execução do sistema: ainda não existe arquivo, e isso não é
        // erro. Devolver a lista vazia deixa o programa abrir normalmente.
        if (!Files.exists(caminho)) {
            return linhas;
        }

        // try-with-resources: o BufferedReader é fechado sozinho no fim do
        // bloco, inclusive se a leitura lançar exceção no meio do arquivo.
        try (BufferedReader leitor = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    // o -1 mantém os campos vazios do fim da linha
                    linhas.add(linha.split(SEPARADOR, -1));
                }
            }
        } catch (IOException e) {
            // A IOException vira uma exceção do sistema, mas segue guardada
            // como causa para não perder o motivo técnico da falha.
            throw new PersistenciaException("Falha ao ler o arquivo " + arquivo + ".", e);
        }

        return linhas;
    }

    public static void gravar(String arquivo, List<String> linhas) throws PersistenciaException {
        Path caminho = PASTA.resolve(arquivo);

        try {
            Files.createDirectories(PASTA);
        } catch (IOException e) {
            throw new PersistenciaException("Não foi possível criar a pasta de dados.", e);
        }

        try (BufferedWriter escritor = Files.newBufferedWriter(caminho, StandardCharsets.UTF_8)) {
            for (String linha : linhas) {
                escritor.write(linha);
                escritor.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Falha ao gravar o arquivo " + arquivo + ".", e);
        }
    }

    public static Path getPasta() {
        return PASTA.toAbsolutePath();
    }
}
