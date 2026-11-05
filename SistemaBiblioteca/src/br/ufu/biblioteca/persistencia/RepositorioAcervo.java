package br.ufu.biblioteca.persistencia;

import br.ufu.biblioteca.excecao.PersistenciaException;
import br.ufu.biblioteca.modelo.Dvd;
import br.ufu.biblioteca.modelo.ItemAcervo;
import br.ufu.biblioteca.modelo.Livro;
import br.ufu.biblioteca.modelo.Revista;

import java.util.ArrayList;
import java.util.List;

// Linha: tipo;codigo;titulo;ano;detalhe;prazo;multa
// O tipo na primeira coluna diz qual subclasse criar na hora de carregar.
// Prazo e multa ficam vazios quando a obra usa o padrão do tipo.
public class RepositorioAcervo {

    private static final String ARQUIVO = "acervo.csv";

    public void salvar(List<ItemAcervo> acervo) throws PersistenciaException {
        List<String> linhas = new ArrayList<>();

        for (ItemAcervo item : acervo) {
            String prazo = (item.getPrazoDias() == item.getPrazoPadrao())
                    ? "" : String.valueOf(item.getPrazoDias());
            String multa = (Math.abs(item.getMultaPorDia() - item.getMultaPadrao()) < 0.001)
                    ? "" : String.valueOf(item.getMultaPorDia());

            linhas.add(String.join(ArquivoCsv.SEPARADOR,
                    item.getTipo(),
                    item.getCodigo(),
                    item.getTitulo(),
                    String.valueOf(item.getAno()),
                    item.getDetalhe(),
                    prazo,
                    multa));
        }

        ArquivoCsv.gravar(ARQUIVO, linhas);
    }

    public List<ItemAcervo> carregar() throws PersistenciaException {
        List<ItemAcervo> acervo = new ArrayList<>();

        for (String[] campos : ArquivoCsv.ler(ARQUIVO)) {
            if (campos.length < 7) {
                throw new PersistenciaException(
                        "Linha incompleta no arquivo " + ARQUIVO + ".", null);
            }

            String tipo = campos[0];
            String codigo = campos[1];
            String titulo = campos[2];
            String detalhe = campos[4];

            try {
                int ano = Integer.parseInt(campos[3]);

                ItemAcervo item;
                if ("Livro".equals(tipo)) {
                    item = new Livro(codigo, titulo, ano, detalhe);
                } else if ("Revista".equals(tipo)) {
                    // foi gravado como "Edição 42", então tira só o número
                    item = new Revista(codigo, titulo, ano, extrairNumero(detalhe));
                } else if ("DVD".equals(tipo)) {
                    item = new Dvd(codigo, titulo, ano, extrairNumero(detalhe));
                } else {
                    throw new PersistenciaException(
                            "Tipo de item desconhecido no arquivo: " + tipo, null);
                }

                if (!campos[5].isEmpty()) {
                    item.definirPrazo(Integer.parseInt(campos[5]));
                }
                if (!campos[6].isEmpty()) {
                    item.definirMulta(Double.parseDouble(campos[6]));
                }

                acervo.add(item);

            } catch (NumberFormatException e) {
                // alguém editou o arquivo à mão e botou algo que não é número
                throw new PersistenciaException(
                        "Número inválido na linha do item " + codigo + ".", e);
            }
        }

        return acervo;
    }

    // Tira os dígitos de textos como "Edição 42" ou "120 min".
    private int extrairNumero(String texto) {
        String apenasDigitos = texto.replaceAll("[^0-9]", "");
        if (apenasDigitos.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(apenasDigitos);
    }
}
