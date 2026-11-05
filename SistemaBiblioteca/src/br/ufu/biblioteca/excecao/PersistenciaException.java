package br.ufu.biblioteca.excecao;

// Guarda a IOException original como causa.
public class PersistenciaException extends BibliotecaException {

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
