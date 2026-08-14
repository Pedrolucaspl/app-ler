package com.pedro;

import java.util.List;
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
            opcao = lerInteiro(this.scanner, "Opção: ");
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
                default:
                    System.out.println("Opção inválida. Tente novamente.");
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
        List<Livro> livros = biblioteca.listarLivros();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            pausarTela(this.scanner);
            limparTela();
            return;
        } else {
            System.out.println("=== Lista de Livros ===");

            for (int i = 0; i < livros.size(); i++) {
                Livro livro = livros.get(i);
                System.out.printf("%d. %s\n", i + 1, livro);
            }
        }
        pausarTela(this.scanner);
        limparTela();
    }

    public void adicionarLivro() {
        try {
            System.out.println("Digite o título do livro:");
            String titulo = this.scanner.nextLine();
            System.out.println("Digite o autor do livro:");
            String autor = this.scanner.nextLine();
            int paginas = lerInteiro(this.scanner, "Digite o número de páginas: \n");

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
        pausarTela(this.scanner);
        limparTela();
        return;
    }

    public void removerLivro() {
        System.out.println("=== Remover Livro ===");
        List<Livro> livrosRemover = listarLivrosTitulo(biblioteca);
        if (livrosRemover.isEmpty()) {
            pausarTela(this.scanner);
            limparTela();
            return;
        }
        int indice = lerInteiro(this.scanner, "\nSelecione o índice do livro que deseja remover: ");
        if (biblioteca.removerLivro(indice - 1)) {
            System.out.println("Livro removido com sucesso.");
        } else {
            System.out.println("Índice inválido.");
        }
        pausarTela(this.scanner);
        limparTela();
        return;
    }

    public static int lerInteiro(Scanner scanner, String mensagem) {
        int valor;
        while (true) {
            System.out.print(mensagem);
            try {
                valor = Integer.parseInt(scanner.nextLine());
                break;
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

    public static void pausarTela(Scanner scanner) {
        System.out.println("Pressione Enter para continuar...");
        scanner.nextLine(); // Limpar o buffer do scanner
    }

    public static List<Livro> listarLivrosTitulo(Biblioteca biblioteca) {
        List<Livro> livros = biblioteca.listarLivros();
        if (livros.isEmpty()) {
            System.out.println("\nNenhum livro cadastrado.");
        } else {
            for (int i = 0; i < livros.size(); i++) {
                Livro livro = livros.get(i);
                System.out.printf("%d. %s\n", i + 1, livro.getTitulo());
            }
        }
        return livros;
    }
}
