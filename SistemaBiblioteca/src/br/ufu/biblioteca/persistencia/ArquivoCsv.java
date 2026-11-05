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

// Leitura e gravação dos arquivos. Todos os repositórios passam por aqui,
// então o tratamento de IOException fica em um lugar só.
public final class ArquivoCsv {

    public static final String SEPARADOR = ";";

    private static final Path PASTA = Paths.get("dados");

    // Classe utilitária, não faz sentido instanciar
    private ArquivoCsv() {
    }

    public static List<String[]> ler(String arquivo) throws PersistenciaException {
        List<String[]> linhas = new ArrayList<>();
        Path caminho = PASTA.resolve(arquivo);

        // Primeira execução: ainda não existe arquivo, e isso não é erro
        if (!Files.exists(caminho)) {
            return linhas;
        }

        // o try-with-resources fecha o arquivo sozinho, mesmo se der erro no meio
        try (BufferedReader leitor = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    // o -1 mantém os campos vazios do fim da linha
                    linhas.add(linha.split(SEPARADOR, -1));
                }
            }
        } catch (IOException e) {
            // guarda a IOException como causa para não perder o motivo
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
