package com.pedro;

import java.util.List;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args )
    {
        Biblioteca biblioteca = new Biblioteca();
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("=== Bem-vindo à Biblioteca ===");
            System.out.println( "Escolha uma opção:" );
            System.out.println("1. Listar livros");
            System.out.println("2. Adicionar livro");
            System.out.println("3. Remover livro");
            System.out.println("4. Atualizar status de leitura");
            System.out.println("0. Sair");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer do scanner
            
            switch (opcao) {
                case 1:
                    // Listar livros
                    List<Livro> livros = biblioteca.listarLivros();
                    for (int i = 0; i < livros.size(); i++) {
                        Livro livro = livros.get(i);
                        System.out.printf("%d. %s\n", i+1, livro);
                    }
                    break;
                case 2:
                    // Adicionar livro
                    System.out.println("Digite o título do livro:");
                    String titulo = scanner.nextLine();
                    System.out.println("Digite o autor do livro:");
                    String autor = scanner.nextLine();
                    System.out.println("Digite o número de páginas:");
                    int paginas = scanner.nextInt();
                    

                    Livro livro = Livro.builder()
                            .titulo(titulo)
                            .autor(autor)
                            .paginas(paginas)
                            .build();
                    biblioteca.adicionarLivro(livro);
                    break;
                case 3:
                    // Remover livro
                    System.out.println("Digite o índice do livro a ser removido:");
                    int indice = scanner.nextInt();
                    if (biblioteca.removerLivro(indice - 1)) {
                        System.out.println("Livro removido com sucesso.");
                    } else {
                        System.out.println("Índice inválido.");
                    }
                    break;
                case 4:
                    // Atualizar status de leitura
                    System.out.println("Digite o índice do livro para atualizar o status de leitura:");
                    int indiceStatus = scanner.nextInt();
                    System.out.println("Escolha o novo status de leitura:");
                    System.out.println("1. QUERO_LER");
                    System.out.println("2. LENDO");
                    System.out.println("3. LIDO");
                    System.out.println("4. ABANDONADO");
                    int statusOpcao = scanner.nextInt();
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
                            continue; // Volta para o menu principal
                    }
                    if (biblioteca.atualizarStatusLeitura(indiceStatus - 1, novoStatus)) {
                        System.out.println("Status de leitura atualizado com sucesso.");
                    } else {
                        System.out.println("Índice inválido.");
                    }
                    break;
                    
            
                default:
                    break;

            }
        }
    }
}
