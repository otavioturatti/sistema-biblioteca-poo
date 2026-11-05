# Sistema de Biblioteca

Projeto final da disciplina de Programação Orientada a Objetos (1B141), do curso de
Sistemas de Informação da Faculdade de Computação da Universidade Federal de Uberlândia.

Sistema em Java com interface gráfica Swing para controle de acervo, leitores e
empréstimos, com os dados gravados em arquivos CSV.

## Integrantes

| Nome | Matrícula |
|---|---|
| Caio Ramalho Araújo | 12521BSI256 |
| Gustavo Silva do Carmo | 12521BSI254 |
| João Otávio Turatti Barbosa | 12421BSI345 |
| Mariany Oliveira Cordeiro | 12521BSI258 |
| Micaelly Ferreira Magalhães | 12521BSI251 |
| Pedro Lucas Moreira Araújo | 12521BSI255 |

## Como executar

Requer JDK 17 ou superior.

```
javac -encoding UTF-8 -sourcepath src -d build src/br/ufu/biblioteca/Main.java
java -cp build br.ufu.biblioteca.Main
```

No Windows, o arquivo `SistemaBiblioteca/executar.bat` localiza o Java instalado, compila e
abre o sistema. Na IDE, basta executar a classe `br.ufu.biblioteca.Main`.

Na primeira execução o sistema cria uma carga inicial com nove obras, vinte e quatro
exemplares, oito leitores e um bibliotecário.

## Acesso

| Perfil | Usuário | Senha |
|---|---|---|
| Bibliotecário | 1001 | ufu2026 |
| Leitor | matrícula do leitor | quatro últimos dígitos do CPF |

## Estrutura

```
SistemaBiblioteca/src/br/ufu/biblioteca/
  modelo/        entidades do domínio
  servico/       regras de negócio
  persistencia/  leitura e gravação dos arquivos
  excecao/       hierarquia própria de exceções
  gui/           telas em Swing
relatorio/       relatório do projeto em PDF
telas/           capturas usadas no manual do usuário
```

O relatório em `relatorio/` descreve a modelagem, o diagrama de classes e onde cada
conceito de orientação a objetos foi aplicado.
