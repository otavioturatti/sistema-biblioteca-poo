package br.ufu.biblioteca.excecao;

/**
 * Exceção base do sistema. Todas as exceções próprias herdam desta, o que
 * permite à interface gráfica capturar BibliotecaException em um único catch
 * e ainda assim tratar casos específicos quando precisa.
 * Estende Exception (checked) para obrigar quem chama a regra de negócio a
 * decidir o que fazer com o erro.
 */
public class BibliotecaException extends Exception {

    public BibliotecaException(String mensagem) {
        super(mensagem);
    }

    public BibliotecaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
