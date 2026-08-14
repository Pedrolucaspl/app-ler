package com.pedro;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    private LivroRepository repository;
    private List<Livro> livros;

    public Biblioteca() throws Exception {
        this.repository = new LivroRepository();
        try {
            this.livros = repository.carregar(); // carrega o que já existia
        } catch (IOException e) {
            throw new Exception("Erro ao carregar os livros: " + e.getMessage());
        }
    }
    public void adicionarLivro(Livro livro) {
        livros.add(livro);
        repository.salvar(livros);
    }
    public List<Livro> listarLivros() {
        List<Livro> livrosCopList = new ArrayList<>(livros);
        return livrosCopList;
    }
    public boolean removerLivro(int indice) {
        if (indice >= 0 && indice < livros.size()) {
            livros.remove(indice);
            repository.salvar(livros);
            return true;
        } else {
            return false;
        }
    }
    public boolean atualizarStatusLeitura(int indice, StatusLeitura novoStatus) {
        if (indice >= 0 && indice < livros.size()) {
            Livro livro = livros.get(indice);
            livro.setStatus(novoStatus);
            repository.salvar(livros);
            return true;
        } else {
            return false;
        }
    }

}