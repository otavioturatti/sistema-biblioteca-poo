package br.ufu.biblioteca.modelo;

/**
 * Contrato de quem consegue entrar no sistema. Cada perfil tem uma regra
 * própria de autenticação, por isso o método é só declarado aqui.
 */
public interface Autenticavel {

    boolean autenticar(String senha);
}
