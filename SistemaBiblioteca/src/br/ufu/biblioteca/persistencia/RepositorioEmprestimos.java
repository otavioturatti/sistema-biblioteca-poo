package br.ufu.biblioteca.persistencia;

import br.ufu.biblioteca.excecao.PersistenciaException;
import br.ufu.biblioteca.modelo.Emprestimo;
import br.ufu.biblioteca.modelo.Exemplar;
import br.ufu.biblioteca.modelo.ItemEmprestado;
import br.ufu.biblioteca.modelo.Leitor;
import br.ufu.biblioteca.modelo.StatusEmprestimo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Linha: id;matriculaLeitor;retiradaEm;status;tombo;previsao;devolvidoEm;multa
// Cada exemplar vira uma linha e repete o cabeçalho. Na leitura as linhas
// são agrupadas pelo id.
public class RepositorioEmprestimos {

    private static final String ARQUIVO = "emprestimos.csv";

    public void salvar(List<Emprestimo> emprestimos) throws PersistenciaException {
        List<String> linhas = new ArrayList<>();

        for (Emprestimo emprestimo : emprestimos) {
            for (ItemEmprestado emprestado : emprestimo.getItens()) {
                linhas.add(String.join(ArquivoCsv.SEPARADOR,
                        String.valueOf(emprestimo.getId()),
                        emprestimo.getLeitor().getMatricula(),
                        emprestimo.getRetiradaEm().toString(),
                        emprestimo.getStatus().name(),
                        emprestado.getExemplar().getTombo(),
                        emprestado.getPrevisaoDevolucao().toString(),
                        textoData(emprestado.getDevolvidoEm()),
                        String.valueOf(emprestado.getMulta())));
            }
        }

        ArquivoCsv.gravar(ARQUIVO, linhas);
    }

    // Precisa dos leitores e exemplares já carregados para religar as referências
    public List<Emprestimo> carregar(Map<String, Leitor> leitores,
                                     Map<String, Exemplar> exemplares) throws PersistenciaException {

        Map<Integer, Emprestimo> porId = new LinkedHashMap<>();

        for (String[] campos : ArquivoCsv.ler(ARQUIVO)) {
            if (campos.length < 8) {
                throw new PersistenciaException(
                        "Linha incompleta no arquivo " + ARQUIVO + ".", null);
            }

            try {
                int id = Integer.parseInt(campos[0]);
                Leitor leitor = leitores.get(campos[1]);
                Exemplar exemplar = exemplares.get(campos[4]);

                // aponta para leitor ou exemplar que não existe mais
                if (leitor == null || exemplar == null) {
                    continue;
                }

                Emprestimo emprestimo = porId.get(id);
                if (emprestimo == null) {
                    emprestimo = new Emprestimo(id, leitor,
                            LocalDate.parse(campos[2]),
                            StatusEmprestimo.valueOf(campos[3]));
                    porId.put(id, emprestimo);
                }

                LocalDate devolvidoEm = campos[6].isEmpty() ? null : LocalDate.parse(campos[6]);
                emprestimo.restaurarItem(exemplar, LocalDate.parse(campos[5]),
                        devolvidoEm, Double.parseDouble(campos[7]));

            } catch (NumberFormatException e) {
                throw new PersistenciaException(
                        "Número inválido no arquivo " + ARQUIVO + ".", e);
            } catch (DateTimeParseException e) {
                throw new PersistenciaException(
                        "Data fora do formato aaaa-mm-dd no arquivo " + ARQUIVO + ".", e);
            } catch (IllegalArgumentException e) {
                // status que não existe no enum
                throw new PersistenciaException(
                        "Situação de empréstimo desconhecida no arquivo.", e);
            }
        }

        return new ArrayList<>(porId.values());
    }

    private String textoData(LocalDate data) {
        return (data == null) ? "" : data.toString();
    }
}
