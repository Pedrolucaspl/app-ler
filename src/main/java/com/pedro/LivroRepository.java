package com.pedro;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LivroRepository {
    private static final String ARQUIVO = "data/livros.json";
    private Gson gson = new Gson();
    Path caminho = Path.of(ARQUIVO);
    
    public void salvar(List<Livro> livros) {
        String json = gson.toJson(livros);
        try {
            Files.createDirectories(caminho.getParent());
            Files.writeString(caminho, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Livro> carregar() throws IOException {
    
    if (!Files.exists(caminho)) {
        return new ArrayList<>();
    }
    String json = Files.readString(caminho);
    Type tipoLista = new TypeToken<List<Livro>>(){}.getType();
    List<Livro> livros = gson.fromJson(json, tipoLista);
    return livros;
    }
}
