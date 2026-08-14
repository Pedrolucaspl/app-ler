package com.pedro;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    private List<Livro> livros = new ArrayList<>();

    public void adicionarLivro(Livro livro) {
        livros.add(livro);
    }
    public List<Livro> listarLivros() {
        List<Livro> livrosCopList = new ArrayList<>(livros);
        return livrosCopList;
    }
    public boolean removerLivro(int indice) {
        if (indice >= 0 && indice < livros.size()) {
            livros.remove(indice);
            return true;
        } else {
            return false;
        }
    }
    public boolean atualizarStatusLeitura(int indice, StatusLeitura novoStatus) {
        if (indice >= 0 && indice < livros.size()) {
            Livro livro = livros.get(indice);
            livro.setStatus(novoStatus);
            return true;
        } else {
            return false;
        }
    }
}
