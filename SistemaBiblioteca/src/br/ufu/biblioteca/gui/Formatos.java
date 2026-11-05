package br.ufu.biblioteca.gui;

import br.ufu.biblioteca.excecao.DadosInvalidosException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

// A tela mostra data em dd/MM/aaaa e valor em reais, o modelo usa LocalDate
// e double.
public final class Formatos {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");

    private Formatos() {
    }

    public static String data(LocalDate data) {
        return (data == null) ? "-" : data.format(DATA);
    }

    // Se o formato estiver errado, vira mensagem em vez de estourar na tela
    public static LocalDate lerData(String texto, String campo) throws DadosInvalidosException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new DadosInvalidosException("Informe a " + campo + ".");
        }
        try {
            return LocalDate.parse(texto.trim(), DATA);
        } catch (DateTimeParseException e) {
            throw new DadosInvalidosException(
                    "A " + campo + " deve estar no formato dd/mm/aaaa.");
        }
    }

    public static String moeda(double valor) {
        return String.format(BRASIL, "R$ %.2f", valor);
    }

    // Sem o R$, para preencher campo de formulário
    public static String numero(double valor) {
        return String.format(BRASIL, "%.2f", valor);
    }

    public static String hoje() {
        return LocalDate.now().format(DATA);
    }
}
