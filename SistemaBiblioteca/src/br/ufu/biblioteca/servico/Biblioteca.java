package br.ufu.biblioteca.servico;

import br.ufu.biblioteca.excecao.AutenticacaoException;
import br.ufu.biblioteca.excecao.CadastroDuplicadoException;
import br.ufu.biblioteca.excecao.DadosInvalidosException;
import br.ufu.biblioteca.excecao.DevolucaoInvalidaException;
import br.ufu.biblioteca.excecao.ItemIndisponivelException;
import br.ufu.biblioteca.excecao.ItemNaoEncontradoException;
import br.ufu.biblioteca.excecao.LimiteEmprestimosException;
import br.ufu.biblioteca.excecao.PendenciaException;
import br.ufu.biblioteca.excecao.PersistenciaException;
import br.ufu.biblioteca.modelo.Bibliotecario;
import br.ufu.biblioteca.modelo.Dvd;
import br.ufu.biblioteca.modelo.Emprestimo;
import br.ufu.biblioteca.modelo.Exemplar;
import br.ufu.biblioteca.modelo.ItemAcervo;
import br.ufu.biblioteca.modelo.ItemEmprestado;
import br.ufu.biblioteca.modelo.Leitor;
import br.ufu.biblioteca.modelo.Livro;
import br.ufu.biblioteca.modelo.Pessoa;
import br.ufu.biblioteca.modelo.Revista;
import br.ufu.biblioteca.modelo.StatusEmprestimo;
import br.ufu.biblioteca.persistencia.RepositorioAcervo;
import br.ufu.biblioteca.persistencia.RepositorioBibliotecarios;
import br.ufu.biblioteca.persistencia.RepositorioEmprestimos;
import br.ufu.biblioteca.persistencia.RepositorioExemplares;
import br.ufu.biblioteca.persistencia.RepositorioLeitores;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Guarda as listas e aplica as regras de negócio. A tela não decide nada:
// chama um método daqui e trata a exceção que vier.
public class Biblioteca {

    // Limites para o que pode ser digitado no cadastro
    public static final int LIMITE_PRAZO_DIAS = 90;
    public static final double LIMITE_MULTA_DIARIA = 50.0;
    public static final int LIMITE_EXEMPLARES = 99;

    private final List<ItemAcervo> acervo = new ArrayList<>();
    private final List<Leitor> leitores = new ArrayList<>();
    private final List<Bibliotecario> bibliotecarios = new ArrayList<>();
    private final List<Emprestimo> emprestimos = new ArrayList<>();

    private final RepositorioAcervo repositorioAcervo = new RepositorioAcervo();
    private final RepositorioExemplares repositorioExemplares = new RepositorioExemplares();
    private final RepositorioLeitores repositorioLeitores = new RepositorioLeitores();
    private final RepositorioBibliotecarios repositorioBibliotecarios = new RepositorioBibliotecarios();
    private final RepositorioEmprestimos repositorioEmprestimos = new RepositorioEmprestimos();

    public void carregarDados() throws PersistenciaException {
        acervo.clear();
        leitores.clear();
        bibliotecarios.clear();
        emprestimos.clear();

        acervo.addAll(repositorioAcervo.carregar());
        leitores.addAll(repositorioLeitores.carregar());
        bibliotecarios.addAll(repositorioBibliotecarios.carregar());

        // os arquivos guardam só códigos, então os mapas abaixo reapontam
        // cada linha ao objeto já carregado
        Map<String, ItemAcervo> mapaObras = new LinkedHashMap<>();
        for (ItemAcervo item : acervo) {
            mapaObras.put(item.getCodigo(), item);
        }
        Map<String, Exemplar> mapaExemplares = repositorioExemplares.carregar(mapaObras);

        Map<String, Leitor> mapaLeitores = new LinkedHashMap<>();
        for (Leitor leitor : leitores) {
            mapaLeitores.put(leitor.getMatricula(), leitor);
        }

        emprestimos.addAll(repositorioEmprestimos.carregar(mapaLeitores, mapaExemplares));

        // sem bibliotecário não tem como entrar no sistema
        if (bibliotecarios.isEmpty()) {
            criarDadosIniciais();
            salvarTudo();
        }
    }

    public void salvarTudo() throws PersistenciaException {
        repositorioAcervo.salvar(acervo);
        repositorioExemplares.salvar(acervo);
        repositorioLeitores.salvar(leitores);
        repositorioBibliotecarios.salvar(bibliotecarios);
        repositorioEmprestimos.salvar(emprestimos);
    }

    // Só roda quando a pasta de dados está vazia
    private void criarDadosIniciais() {
        bibliotecarios.add(new Bibliotecario("1001", "Roberto Caetano",
                "11122233344", "roberto@ufu.br", "ufu2026"));

        adicionarObra(new Livro("L001", "Java como Programar", 2017, "Paul Deitel"), 4);
        adicionarObra(new Livro("L002", "Use a Cabeça! Java", 2009, "Kathy Sierra"), 3);
        adicionarObra(new Livro("L003", "Engenharia de Software", 2019, "Ian Sommerville"), 2);
        adicionarObra(new Livro("L004", "Algoritmos: Teoria e Prática", 2012, "Thomas Cormen"), 5);
        adicionarObra(new Livro("L005", "Código Limpo", 2009, "Robert Martin"), 2);
        adicionarObra(new Revista("R001", "Superinteressante", 2026, 468), 3);
        adicionarObra(new Revista("R002", "Galileu", 2026, 391), 2);
        adicionarObra(new Dvd("D001", "O Senhor dos Anéis", 2001, 178), 2);
        adicionarObra(new Dvd("D002", "Matrix", 1999, 136), 1);

        leitores.add(new Leitor("12421BSI345", "João Otávio Turatti Barbosa",
                "52998224725", "joao.otavio@ufu.br"));
        leitores.add(new Leitor("12421BSI100", "Maria Clara Souza",
                "39053344705", "maria.souza@ufu.br"));
        leitores.add(new Leitor("12421BSI211", "Pedro Henrique Lima",
                "16899535009", "pedro.lima@ufu.br"));
        leitores.add(new Leitor("12320BCC087", "Ana Beatriz Ferreira",
                "70312288065", "ana.ferreira@ufu.br"));
        leitores.add(new Leitor("12522BSI019", "Lucas Martins Rocha",
                "48795612300", "lucas.rocha@ufu.br"));
        leitores.add(new Leitor("12421BSI502", "Camila Andrade Nunes",
                "33344455566", "camila.nunes@ufu.br"));
        leitores.add(new Leitor("12219BCC044", "Rafael Oliveira Dias",
                "22233344455", "rafael.dias@ufu.br"));
        leitores.add(new Leitor("12623BSI130", "Juliana Pereira Costa",
                "88899900011", "juliana.costa@ufu.br"));
    }

    private void adicionarObra(ItemAcervo obra, int exemplares) {
        obra.adicionarExemplares(exemplares);
        acervo.add(obra);
    }

    // Procura pelo identificador e deixa a própria pessoa validar a senha
    public Pessoa autenticar(String identificador, String senha) throws AutenticacaoException {
        List<Pessoa> usuarios = new ArrayList<>();
        usuarios.addAll(bibliotecarios);
        usuarios.addAll(leitores);

        for (Pessoa pessoa : usuarios) {
            if (pessoa.getIdentificador().equalsIgnoreCase(identificador)
                    && pessoa.autenticar(senha)) {
                return pessoa;
            }
        }

        throw new AutenticacaoException();
    }

    public void cadastrarLivro(String codigo, String titulo, String ano, String autor,
                               String prazo, String multa, String exemplares)
            throws CadastroDuplicadoException, DadosInvalidosException, PersistenciaException {

        validarItem(codigo, titulo, ano);
        exigirTexto(autor, "autor");

        Livro livro = new Livro(codigo.trim(), titulo.trim(),
                Integer.parseInt(ano.trim()), autor.trim());
        cadastrar(livro, prazo, multa, exemplares);
    }

    public void cadastrarRevista(String codigo, String titulo, String ano, String edicao,
                                 String prazo, String multa, String exemplares)
            throws CadastroDuplicadoException, DadosInvalidosException, PersistenciaException {

        validarItem(codigo, titulo, ano);

        Revista revista = new Revista(codigo.trim(), titulo.trim(),
                Integer.parseInt(ano.trim()), numeroPositivo(edicao, "edição"));
        cadastrar(revista, prazo, multa, exemplares);
    }

    public void cadastrarDvd(String codigo, String titulo, String ano, String duracao,
                             String prazo, String multa, String exemplares)
            throws CadastroDuplicadoException, DadosInvalidosException, PersistenciaException {

        validarItem(codigo, titulo, ano);

        Dvd dvd = new Dvd(codigo.trim(), titulo.trim(),
                Integer.parseInt(ano.trim()), numeroPositivo(duracao, "duração"));
        cadastrar(dvd, prazo, multa, exemplares);
    }

    private void cadastrar(ItemAcervo obra, String prazo, String multa, String exemplares)
            throws DadosInvalidosException, PersistenciaException {

        ajustarPrazoEMulta(obra, prazo, multa);
        obra.adicionarExemplares(quantidadeDeExemplares(exemplares));

        acervo.add(obra);
        salvarTudo();
    }

    // Código e tipo não mudam: são a identidade do registro e dos tombos.
    // Valida tudo antes de alterar para a obra não ficar pela metade.
    public void editarItem(ItemAcervo obra, String titulo, String ano, String detalhe,
                           String prazo, String multa, String exemplares)
            throws DadosInvalidosException, PersistenciaException {

        if (obra == null) {
            throw new DadosInvalidosException("Selecione o item que será editado.");
        }

        String campoDetalhe = obra.getRotuloDetalhe().toLowerCase();

        exigirTexto(titulo, "título");
        int anoNumerico = validarAno(ano);
        exigirTexto(detalhe, campoDetalhe);
        if (obra.detalheENumerico()) {
            numeroPositivo(detalhe, campoDetalhe);
        }

        int quantidade = quantidadeDeExemplares(exemplares);
        if (quantidade < obra.contarEmprestados()) {
            throw new DadosInvalidosException("Esta obra tem " + obra.contarEmprestados()
                    + " exemplar(es) emprestado(s), então não pode ficar com menos que isso.");
        }

        obra.setTitulo(titulo.trim());
        obra.setAno(anoNumerico);
        obra.definirDetalhe(detalhe.trim());
        ajustarPrazoEMulta(obra, prazo, multa);

        if (quantidade > obra.getTotalDeExemplares()) {
            obra.adicionarExemplares(quantidade - obra.getTotalDeExemplares());
        } else if (quantidade < obra.getTotalDeExemplares()) {
            obra.removerExemplaresDisponiveis(obra.getTotalDeExemplares() - quantidade);
        }

        salvarTudo();
    }

    // Campo em branco mantém o padrão do tipo
    public void ajustarPrazoEMulta(ItemAcervo item, String prazo, String multa)
            throws DadosInvalidosException {

        if (prazo != null && !prazo.trim().isEmpty()) {
            int dias = numeroPositivo(prazo, "prazo de empréstimo");
            if (dias > LIMITE_PRAZO_DIAS) {
                throw new DadosInvalidosException(
                        "O prazo de empréstimo não pode passar de " + LIMITE_PRAZO_DIAS + " dias.");
            }
            item.definirPrazo(dias);
        }

        if (multa != null && !multa.trim().isEmpty()) {
            double valor = valorNaoNegativo(multa, "multa por dia");
            if (valor > LIMITE_MULTA_DIARIA) {
                throw new DadosInvalidosException(
                        "A multa por dia não pode passar de R$ " + LIMITE_MULTA_DIARIA + ".");
            }
            item.definirMulta(valor);
        }
    }

    public void removerItem(ItemAcervo item) throws DadosInvalidosException, PersistenciaException {
        if (item.contarEmprestados() > 0) {
            throw new DadosInvalidosException("\"" + item.getTitulo() + "\" tem "
                    + item.contarEmprestados() + " exemplar(es) emprestado(s) e não pode ser removido.");
        }
        acervo.remove(item);
        salvarTudo();
    }

    public ItemAcervo buscarItem(String codigo) throws ItemNaoEncontradoException {
        for (ItemAcervo item : acervo) {
            if (item.getCodigo().equalsIgnoreCase(codigo)) {
                return item;
            }
        }
        throw new ItemNaoEncontradoException(codigo);
    }

    // Texto vazio devolve o acervo inteiro
    public List<ItemAcervo> buscarObras(String texto) {
        List<ItemAcervo> encontrados = new ArrayList<>();
        String procurado = (texto == null) ? "" : texto.trim().toLowerCase();

        for (ItemAcervo item : acervo) {
            if (procurado.isEmpty()
                    || item.getCodigo().toLowerCase().contains(procurado)
                    || item.getTitulo().toLowerCase().contains(procurado)
                    || item.getTipo().toLowerCase().contains(procurado)
                    || item.getDetalhe().toLowerCase().contains(procurado)) {
                encontrados.add(item);
            }
        }
        return encontrados;
    }

    public void cadastrarLeitor(String matricula, String nome, String cpf, String email)
            throws CadastroDuplicadoException, DadosInvalidosException, PersistenciaException {

        exigirTexto(matricula, "matrícula");
        exigirTexto(nome, "nome");
        exigirTexto(cpf, "CPF");
        exigirTexto(email, "e-mail");

        String limpo = cpf.replaceAll("[^0-9]", "");
        if (limpo.length() != 11) {
            throw new DadosInvalidosException("O CPF deve ter 11 dígitos.");
        }
        if (!email.contains("@")) {
            throw new DadosInvalidosException("E-mail inválido: falta o @.");
        }
        for (Leitor leitor : leitores) {
            if (leitor.getMatricula().equalsIgnoreCase(matricula.trim())) {
                throw new CadastroDuplicadoException("matrícula", matricula.trim());
            }
        }

        leitores.add(new Leitor(matricula.trim(), nome.trim(), limpo, email.trim()));
        salvarTudo();
    }

    public void removerLeitor(Leitor leitor) throws DadosInvalidosException, PersistenciaException {
        if (contarItensEmMaos(leitor) > 0) {
            throw new DadosInvalidosException(
                    leitor.getNome() + " está com itens emprestados e não pode ser removido.");
        }
        leitores.remove(leitor);
        salvarTudo();
    }

    // Usada pela tela de empréstimo, que não lista a base inteira
    public List<Leitor> buscarLeitores(String texto) {
        List<Leitor> encontrados = new ArrayList<>();
        String procurado = (texto == null) ? "" : texto.trim().toLowerCase();

        for (Leitor leitor : leitores) {
            if (procurado.isEmpty()
                    || leitor.getNome().toLowerCase().contains(procurado)
                    || leitor.getMatricula().toLowerCase().contains(procurado)
                    || leitor.getCpf().contains(procurado)) {
                encontrados.add(leitor);
            }
        }
        return encontrados;
    }

    // As regras vêm na ordem da mais barata para a mais cara de verificar,
    // e cada uma tem a sua exceção
    public Emprestimo emprestar(Leitor leitor, List<ItemAcervo> obrasEscolhidas, LocalDate retiradaEm)
            throws DadosInvalidosException, ItemIndisponivelException,
            LimiteEmprestimosException, PendenciaException, PersistenciaException {

        if (leitor == null) {
            throw new DadosInvalidosException("Escolha o leitor.");
        }
        if (obrasEscolhidas == null || obrasEscolhidas.isEmpty()) {
            throw new DadosInvalidosException("Marque pelo menos um item do catálogo.");
        }
        if (retiradaEm.isAfter(LocalDate.now())) {
            throw new DadosInvalidosException("A data de retirada não pode estar no futuro.");
        }
        // sem limite para trás, um ano digitado errado viraria multa de milhares
        LocalDate maisAntiga = LocalDate.now().minusYears(1);
        if (retiradaEm.isBefore(maisAntiga)) {
            throw new DadosInvalidosException(
                    "A data de retirada não pode ser anterior a " + texto(maisAntiga) + ".");
        }
        if (temAtraso(leitor, LocalDate.now())) {
            throw new PendenciaException(leitor.getNome());
        }

        int emMaos = contarItensEmMaos(leitor);
        if (emMaos + obrasEscolhidas.size() > Leitor.LIMITE_EMPRESTIMOS) {
            throw new LimiteEmprestimosException(leitor.getNome(), Leitor.LIMITE_EMPRESTIMOS);
        }

        for (ItemAcervo obra : obrasEscolhidas) {
            if (!obra.estaDisponivel()) {
                throw new ItemIndisponivelException(obra.getTitulo());
            }
            // duas cópias do mesmo título só prendem exemplar que outro poderia usar
            if (jaEstaCom(leitor, obra)) {
                throw new DadosInvalidosException(leitor.getNome()
                        + " já está com um exemplar de \"" + obra.getTitulo() + "\".");
            }
        }

        // o objeto só é criado aqui, depois que todas as regras passaram
        Emprestimo emprestimo = new Emprestimo(leitor, retiradaEm);
        for (ItemAcervo obra : obrasEscolhidas) {
            // o leitor escolhe o título, o sistema separa a cópia
            emprestimo.adicionarExemplar(obra.getExemplarDisponivel());
        }
        emprestimos.add(emprestimo);

        salvarTudo();
        return emprestimo;
    }

    public double devolver(Emprestimo emprestimo, LocalDate dataDevolucao)
            throws DevolucaoInvalidaException, PersistenciaException {

        if (emprestimo == null) {
            throw new DevolucaoInvalidaException("Selecione o empréstimo que será devolvido.");
        }
        if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
            throw new DevolucaoInvalidaException(
                    "O empréstimo " + emprestimo.getId() + " já foi encerrado.");
        }
        if (dataDevolucao.isBefore(emprestimo.getRetiradaEm())) {
            throw new DevolucaoInvalidaException(
                    "A devolução não pode ser anterior à retirada ("
                            + texto(emprestimo.getRetiradaEm()) + ").");
        }
        // data futura só pode ser erro de digitação
        if (dataDevolucao.isAfter(LocalDate.now())) {
            throw new DevolucaoInvalidaException("A devolução não pode ter data futura.");
        }

        double multa = emprestimo.devolver(dataDevolucao);
        salvarTudo();
        return multa;
    }

    private boolean jaEstaCom(Leitor leitor, ItemAcervo obra) {
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getLeitor() != leitor
                    || emprestimo.getStatus() != StatusEmprestimo.ATIVO) {
                continue;
            }
            for (ItemEmprestado emprestado : emprestimo.getItens()) {
                if (!emprestado.estaDevolvido() && emprestado.getObra() == obra) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean temAtraso(Leitor leitor, LocalDate referencia) {
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getLeitor() == leitor && emprestimo.estaAtrasado(referencia)) {
                return true;
            }
        }
        return false;
    }

    public int contarItensEmMaos(Leitor leitor) {
        int total = 0;
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getLeitor() == leitor
                    && emprestimo.getStatus() == StatusEmprestimo.ATIVO) {
                total += emprestimo.getQuantidadeDeItens();
            }
        }
        return total;
    }

    public List<ItemAcervo> getAcervo() {
        return Collections.unmodifiableList(acervo);
    }

    public List<Leitor> getLeitores() {
        return Collections.unmodifiableList(leitores);
    }

    public List<Emprestimo> getEmprestimos() {
        return Collections.unmodifiableList(emprestimos);
    }

    public List<Emprestimo> getEmprestimosDoLeitor(Leitor leitor) {
        List<Emprestimo> doLeitor = new ArrayList<>();
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getLeitor() == leitor) {
                doLeitor.add(emprestimo);
            }
        }
        return doLeitor;
    }

    public double getTotalDeMultas() {
        double total = 0.0;
        for (Emprestimo emprestimo : emprestimos) {
            total += emprestimo.getMultaTotal();
        }
        return total;
    }

    public int contarExemplares() {
        int total = 0;
        for (ItemAcervo item : acervo) {
            total += item.getTotalDeExemplares();
        }
        return total;
    }

    public int contarExemplaresEmprestados() {
        int total = 0;
        for (ItemAcervo item : acervo) {
            total += item.contarEmprestados();
        }
        return total;
    }

    private void validarItem(String codigo, String titulo, String ano)
            throws CadastroDuplicadoException, DadosInvalidosException {

        exigirTexto(codigo, "código");
        exigirTexto(titulo, "título");
        validarAno(ano);

        for (ItemAcervo item : acervo) {
            if (item.getCodigo().equalsIgnoreCase(codigo.trim())) {
                throw new CadastroDuplicadoException("código", codigo.trim());
            }
        }
    }

    private int validarAno(String ano) throws DadosInvalidosException {
        int anoNumerico = numeroPositivo(ano, "ano");
        if (anoNumerico < 1400 || anoNumerico > LocalDate.now().getYear()) {
            throw new DadosInvalidosException(
                    "O ano de publicação deve estar entre 1400 e " + LocalDate.now().getYear() + ".");
        }
        return anoNumerico;
    }

    private int quantidadeDeExemplares(String valor) throws DadosInvalidosException {
        int quantidade = numeroPositivo(valor, "quantidade de exemplares");
        if (quantidade > LIMITE_EXEMPLARES) {
            throw new DadosInvalidosException(
                    "O máximo é " + LIMITE_EXEMPLARES + " exemplares por obra.");
        }
        return quantidade;
    }

    // O ponto e vírgula separa as colunas do arquivo, então não pode passar
    private void exigirTexto(String valor, String campo) throws DadosInvalidosException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new DadosInvalidosException("Preencha o campo " + campo + ".");
        }
        if (valor.contains(";")) {
            throw new DadosInvalidosException("O campo " + campo + " não pode conter ponto e vírgula.");
        }
    }

    // Aceita 1,50 e 1.50, porque ninguém digita ponto por aqui
    private double valorNaoNegativo(String valor, String campo) throws DadosInvalidosException {
        exigirTexto(valor, campo);
        try {
            double numero = Double.parseDouble(valor.trim().replace(",", "."));
            if (numero < 0) {
                throw new DadosInvalidosException("O campo " + campo + " não pode ser negativo.");
            }
            return numero;
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("O campo " + campo + " aceita apenas números.");
        }
    }

    private int numeroPositivo(String valor, String campo) throws DadosInvalidosException {
        exigirTexto(valor, campo);
        try {
            int numero = Integer.parseInt(valor.trim());
            if (numero <= 0) {
                throw new DadosInvalidosException("O campo " + campo + " deve ser maior que zero.");
            }
            return numero;
        } catch (NumberFormatException e) {
            // troca o erro do parse por uma mensagem que dá para entender
            throw new DadosInvalidosException("O campo " + campo + " aceita apenas números.");
        }
    }

    // As mensagens vão para a tela, então a data sai no formato que ele digita
    private String texto(LocalDate data) {
        return data.format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));
    }
}
