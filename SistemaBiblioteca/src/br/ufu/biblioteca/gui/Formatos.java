package br.ufu.biblioteca.gui;

import br.ufu.biblioteca.excecao.DadosInvalidosException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Conversões entre o que o usuário vê e o que o sistema usa. A tela mostra
 * data em dd/MM/aaaa e valor em reais, enquanto o modelo trabalha com
 * LocalDate e double.
 */
public final class Formatos {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");

    private Formatos() {
    }

    public static String data(LocalDate data) {
        return (data == null) ? "-" : data.format(DATA);
    }

    /**
     * Converte o texto digitado em data. O usuário pode errar o formato, então
     * a DateTimeParseException é capturada e vira uma mensagem clara.
     */
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

    // Mesmo número sem o prefixo, para preencher campos de formulário.
    public static String numero(double valor) {
        return String.format(BRASIL, "%.2f", valor);
    }

    public static String hoje() {
        return LocalDate.now().format(DATA);
    }
}
