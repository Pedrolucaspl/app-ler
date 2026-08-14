package com.pedro;

import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        App.limparTela();
        Biblioteca biblioteca = null;
        try {
            biblioteca = new Biblioteca();
        } catch (Exception e) {
            System.out.println("Erro ao inicializar a biblioteca: " + e.getMessage());
            return;
        }
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("=== Bem-vindo à Biblioteca ===");
            System.out.println("Escolha uma opção:");
            System.out.println("1. Listar livros");
            System.out.println("2. Adicionar livro");
            System.out.println("3. Remover livro");
            System.out.println("4. Atualizar status de leitura");
            System.out.println("0. Sair\n");
            opcao = App.lerInteiro(scanner, "Opção: ");

            switch (opcao) {
                case 1:
                    // Listar livros
                    List<Livro> livros = biblioteca.listarLivros();
                    if (livros.isEmpty()) {
                        System.out.println("Nenhum livro cadastrado.");
                        App.pausarTela(scanner);
                        App.limparTela();
                        break;
                    } else {
                        System.out.println("=== Lista de Livros ===");

                        for (int i = 0; i < livros.size(); i++) {
                            Livro livro = livros.get(i);
                            System.out.printf("%d. %s\n", i + 1, livro);
                        }
                    }
                    App.pausarTela(scanner);
                    App.limparTela();
                    break;
                case 2:
                    // Adicionar livro
                    System.out.println("\nDigite o título do livro:");
                    String titulo = scanner.nextLine();
                    System.out.println("Digite o autor do livro:");
                    String autor = scanner.nextLine();
                    int paginas = App.lerInteiro(scanner, "Digite o número de páginas: \n");

                    Livro livro = Livro.builder()
                            .titulo(titulo)
                            .autor(autor)
                            .paginas(paginas)
                            .build();
                    biblioteca.adicionarLivro(livro);
                    App.pausarTela(scanner);
                    App.limparTela();
                    break;

                case 3:
                    // Remover livro
                    List<Livro> livrosRemover = App.listarLivrosTitulo(biblioteca);
                    if (livrosRemover.isEmpty()) {
                        App.pausarTela(scanner);
                        App.limparTela();
                        break;
                    }
                    int indice = App.lerInteiro(scanner, "\nÍndice do livro a ser removido: ");
                    if (biblioteca.removerLivro(indice - 1)) {
                        System.out.println("Livro removido com sucesso.");
                    } else {
                        System.out.println("Índice inválido.");
                    }
                    App.pausarTela(scanner);
                    App.limparTela();
                    break;

                case 4:
                    // Atualizar status de leitura
                    List<Livro> livrosStatus = App.listarLivrosTitulo(biblioteca);
                    if (livrosStatus.isEmpty()) {
                        App.pausarTela(scanner);
                        App.limparTela();
                        break;
                    }
                    int indiceStatus = App.lerInteiro(scanner, "\nÍndice do livro para atualizar o status de leitura: ");
                    App.limparTela();
                    System.out.println("Escolha o novo status de leitura:");
                    System.out.println("1. QUERO_LER");
                    System.out.println("2. LENDO");
                    System.out.println("3. LIDO");
                    System.out.println("4. ABANDONADO");
                    int statusOpcao = App.lerInteiro(scanner, "Opção de status: ");
                    StatusLeitura novoStatus = null;
                    switch (statusOpcao) {
                        case 1:
                            novoStatus = StatusLeitura.QUERO_LER;
                            break;
                        case 2:
                            novoStatus = StatusLeitura.LENDO;
                            break;
                        case 3:
                            novoStatus = StatusLeitura.LIDO;
                            break;
                        case 4:
                            novoStatus = StatusLeitura.ABANDONADO;
                            break;
                        default:
                            System.out.println("Opção de status inválida.");
                            App.pausarTela(scanner);
                            App.limparTela();
                            continue; // Volta para o menu principal
                    }
                    if (biblioteca.atualizarStatusLeitura(indiceStatus - 1, novoStatus)) {
                        System.out.println("\nStatus de leitura atualizado com sucesso.");
                    } else {
                        System.out.println("\nÍndice inválido.");
                    }
                    App.pausarTela(scanner);
                    App.limparTela();
                    break;

                default:
                    if (opcao != 0) {
                        System.out.println("Opção inválida. Tente novamente.");
                        App.pausarTela(scanner);
                        App.limparTela();
                    }
                    break;

            }
        }
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

    public static List<Livro> listarLivrosTitulo(Biblioteca biblioteca) {
        List<Livro> livros = biblioteca.listarLivros();
        if (livros.isEmpty()) {
            System.out.println("\nNenhum livro cadastrado.");
        } else {
            System.out.println("\n=== Lista de Livros ===");
            for (int i = 0; i < livros.size(); i++) {
                Livro livro = livros.get(i);
                System.out.printf("%d. %s\n", i + 1, livro.getTitulo());
            }
        }
        return livros;
    }

}