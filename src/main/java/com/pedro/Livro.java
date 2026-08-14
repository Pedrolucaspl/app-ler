package com.pedro;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Livro{
    @NonNull
    private String titulo;
    @NonNull
    private String autor;
    @NonNull
    private Integer paginas;
    private Integer anoPublicacao;
    private int paginasLidas;
    private String genero;
    private String editora;
    private String idioma;
    private String formato;
    private String isbn;
    private boolean possuir;
    private Double nota;
    private StatusLeitura status;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(titulo).append(" | ");
        sb.append(autor).append("\n");
        sb.append("Páginas Lidas: ").append(paginasLidas).append("/").append(paginas).append("\n");
        if (anoPublicacao != null) {
            sb.append("Ano de Publicação: ").append(anoPublicacao).append("\n");
        }
        if (genero != null) {
            sb.append("Gênero: ").append(genero).append("\n");
        }
        if (editora != null) {
            sb.append("Editora: ").append(editora).append("\n");
        }
        if (idioma != null) {
            sb.append("Idioma: ").append(idioma).append("\n");
        }
        if (formato != null) {
            sb.append("Formato: ").append(formato).append("\n");
        }
        if (isbn != null) {
            sb.append("ISBN: ").append(isbn).append("\n");
        }
        if (possuir) {
            sb.append("Tenho").append("\n");
        } else {
            sb.append("Não tenho").append("\n");
        }
        if (nota != null) {
            sb.append("Nota: ").append(nota).append("\n");
        }
        if (status != null) {
            sb.append("Status: ").append(status).append("\n");
        }
        return sb.toString();
    }
}