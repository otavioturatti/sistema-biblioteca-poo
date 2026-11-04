package br.ufu.biblioteca.modelo;

// Situação do empréstimo. O atraso não entra aqui porque é calculado a partir
// da data prevista, não é um estado guardado em arquivo.
public enum StatusEmprestimo {

    ATIVO,
    DEVOLVIDO
}
