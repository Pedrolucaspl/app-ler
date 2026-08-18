package com.pedro;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Menu {
    private Biblioteca biblioteca;
    private Scanner scanner;

    public Menu(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcao;
        do {
            mostrarMenu();
            opcao = lerInteiro(this.scanner, "Opção: ", 0, 3);
            System.out.println();
            switch (opcao) {
                case 1:
                    limparTela();
                    System.out.println("Opção 1 selecionada: Listar livros\n");
                    listarLivros();
                    break;
                case 2:
                    limparTela();
                    System.out.println("Opção 2 selecionada: Adicionar livro\n");
                    adicionarLivro();
                    break;
                case 3:
                    limparTela();
                    System.out.println("Opção 3 selecionada: Remover livro\n");
                    removerLivro();
                    break;
                case 0:
                    System.out.println("Saindo do programa...");
                    break;
            }
        } while (opcao != 0);
    }

    private void mostrarMenu() {
        System.out.println("=== Menu ===");
        System.out.println("1. Listar livros");
        System.out.println("2. Adicionar livro");
        System.out.println("3. Remover livro");
        System.out.println("0. Sair");
    }

    public void listarLivros() {
        List<Livro> livros = obterLivrosOuAvisar();
        if (!livros.isEmpty()) {
            imprimirLivrosCompleto(livros);
        }
        pausarTela();
        limparTela();
        return;
    }

    public void adicionarLivro() {
        System.out.println("=== Adicionar Livro ===");
        System.out.print("Como deseja adicionar o livro?");
        System.out.println("\n1. Simples (título, autor e páginas apenas)");
        System.out.println("2. Completo (todos os campos)");
        System.out.println("3. Por ISBN (busca automática)");

        int escolha = lerInteiro(this.scanner, "Escolha uma opção: ", 1, 3);
        switch (escolha) {
            case 1:
                adicionarLivroSimples();
                break;
            case 2:
                adicionarLivroCompleto();
                break;
            case 3:
                adicionarLivroPorIsbn();
                break;
        }
    }

    private void adicionarLivroSimples() {
        try {
            System.out.println("Digite o título do livro:");
            String titulo = this.scanner.nextLine();
            System.out.println("Digite o autor do livro:");
            String autor = this.scanner.nextLine();
            int paginas = lerInteiro(this.scanner, "Digite o número de páginas: \n", 1, Integer.MAX_VALUE);

            Livro livro = Livro.builder()
                    .titulo(titulo)
                    .autor(autor)
                    .paginas(paginas)
                    .build();
            biblioteca.adicionarLivro(livro);
            System.out.printf("Livro %s adicionado com sucesso.\n", titulo);
        } catch (Exception e) {
            System.out.println("Erro ao adicionar livro: " + e.getMessage());
        }
    }

    private void adicionarLivroCompleto() {
        try {
            System.out.println("Digite o título do livro:");
            String titulo = this.scanner.nextLine();
            System.out.println("Digite o autor do livro:");
            String autor = this.scanner.nextLine();
            int paginas = lerInteiro(this.scanner, "Digite o número de páginas: \n", 1, Integer.MAX_VALUE);
            System.out.println("Digite o ano de publicação do livro (ou deixe em branco):");
            String anoInput = this.scanner.nextLine();
            Integer anoPublicacao = null;
            if (!anoInput.isBlank()) {
                try {
                    anoPublicacao = Integer.parseInt(anoInput);
                } catch (NumberFormatException e) {
                    System.out.println("Ano de publicação inválido. Será definido como nulo.");
                }
            }

            System.out.println("Digite a editora (ou deixe em branco):");
            String editora = this.scanner.nextLine();
            System.out.println("Digite o formato (ou deixe em branco):");
            String formato = this.scanner.nextLine();
            System.out.println("Você possui o livro? (S/N ou Enter para não informar):");
            String entradaPossuir = this.scanner.nextLine().trim();
            Boolean possuir = entradaPossuir.isBlank()
                    ? null
                    : entradaPossuir.equalsIgnoreCase("S");

            Livro livro = Livro.builder()
                    .titulo(titulo)
                    .autor(autor)
                    .paginas(paginas)
                    .anoPublicacao(anoPublicacao)
                    .editora(editora.isBlank() ? null : editora)
                    .formato(formato.isBlank() ? null : formato)
                    .possuir(possuir)
                    .build();
            biblioteca.adicionarLivro(livro);
            System.out.printf("Livro %s adicionado com sucesso.\n", titulo);
        } catch (Exception e) {
            System.out.println("Erro ao adicionar livro: " + e.getMessage());
        }
    }

    private void adicionarLivroPorIsbn() {
        try {
            System.out.println("Digite o ISBN do livro:");
            String isbn = this.scanner.nextLine();

            LivroAPIService apiService = new LivroAPIService();
            Optional<Livro> resultado = apiService.buscarPorIsbn(isbn);

            if (resultado.isEmpty()) {
                System.out.println("Livro não encontrado na API. Vamos adicionar manualmente.");
                adicionarLivroSimples();
                return;
            }

            Livro encontrado = resultado.get();
            System.out.println("Livro encontrado:");
            System.out.println("Título: " + encontrado.getTitulo());
            System.out.println("Autor: " + encontrado.getAutor());
            System.out.println("Páginas: " + encontrado.getPaginas());
            System.out.println("Você possui o livro? (S/N ou Enter para não informar):");
            String entradaPossuir = this.scanner.nextLine().trim();
            Boolean possuir = entradaPossuir.isBlank()
                    ? null
                    : entradaPossuir.equalsIgnoreCase("S");

            Livro livro = Livro.builder()
                    .titulo(encontrado.getTitulo())
                    .autor(encontrado.getAutor())
                    .paginas(encontrado.getPaginas())
                    .anoPublicacao(encontrado.getAnoPublicacao())
                    .isbn(isbn)
                    .possuir(possuir)
                    .build();

            biblioteca.adicionarLivro(livro);
            System.out.printf("Livro %s adicionado com sucesso.\n", livro.getTitulo());
        } catch (Exception e) {
            System.out.println("Erro ao adicionar livro: " + e.getMessage());
        }
    }

    public void removerLivro() {
        System.out.println("=== Remover Livro ===");
        List<Livro> livros = obterLivrosOuAvisar();
        if (livros.isEmpty()) {
            pausarTela();
            limparTela();
            return;
        }
        imprimirLivrosTitulo(livros);
        int indice = lerInteiro(this.scanner, "Selecione o índice do livro que deseja remover: ", 1, livros.size());
        if (biblioteca.removerLivro(indice - 1)) {
            System.out.println("Livro removido com sucesso.");
        } else {
            System.out.println("Índice inválido.");
        }
        pausarTela();
        limparTela();
        return;
    }

    private List<Livro> obterLivrosOuAvisar() {
        List<Livro> livros = biblioteca.listarLivros();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
        }
        return livros;
    }

    private void imprimirLivrosCompleto(List<Livro> livros) {
        System.out.println("=== Lista de Livros ===");
        for (int i = 0; i < livros.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, livros.get(i));
        }
    }

    private void imprimirLivrosTitulo(List<Livro> livros) {
        for (int i = 0; i < livros.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, livros.get(i).getTitulo());
        }
    }

    public static int lerInteiro(Scanner scanner, String mensagem, int min, int max) {
        int valor;
        while (true) {
            System.out.print(mensagem);
            try {
                valor = Integer.parseInt(scanner.nextLine());
                if (valor >= min && valor <= max) {
                    break;
                } else {
                    System.out.printf("Entrada inválida. Digite um número entre %d e %d.\n", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
            }
        }
        return valor;
    }

    public static void limparTela() {
        // Limpa a tela do console (funciona na maioria dos terminais)
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pausarTela() {
        System.out.println("Pressione Enter para continuar...");
        this.scanner.nextLine(); // Limpar o buffer do scanner
    }

}
