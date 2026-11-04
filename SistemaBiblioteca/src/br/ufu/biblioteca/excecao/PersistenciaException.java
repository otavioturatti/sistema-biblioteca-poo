package br.ufu.biblioteca.excecao;

/**
 * Envolve as falhas de leitura e gravação dos arquivos. A IOException original
 * é guardada como causa, então o motivo técnico não se perde quando a tela
 * mostra uma mensagem simples ao usuário.
 */
public class PersistenciaException extends BibliotecaException {

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
