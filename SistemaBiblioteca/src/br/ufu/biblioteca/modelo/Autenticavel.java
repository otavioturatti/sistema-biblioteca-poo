package br.ufu.biblioteca.modelo;

// Quem consegue entrar no sistema
public interface Autenticavel {

    boolean autenticar(String senha);
}
