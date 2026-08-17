package com.pedro;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import java.util.List;

public class LivroAPIService {
    public static final String API_URL = "https://openlibrary.org";
    private HttpClient httpClient;

    public LivroAPIService() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public Optional<Livro> buscarPorIsbn(String isbn) {
        String isbnNormalizado = isbn == null ? "" : isbn.replaceAll("[-\\s]", "");
        String url = API_URL + "/isbn/" + isbnNormalizado + ".json";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return Optional.empty();
            }

            Gson gson = new Gson();
            IsbnResponse dados = gson.fromJson(response.body(), IsbnResponse.class);
            if (dados == null) {
                return Optional.empty();
            }

            String nomeAutor = null;
            if (dados.authors != null && !dados.authors.isEmpty()) {
                String authorKey = dados.authors.get(0).key;
                nomeAutor = buscarNomeAutor(authorKey);
            }
            Livro.LivroBuilder builder = Livro.builder()
                    .titulo(dados.title != null ? dados.title : "")
                    .autor(nomeAutor != null ? nomeAutor : "")
                    .paginas(dados.number_of_pages != null ? dados.number_of_pages : 1);

            if (dados.publish_date != null) {
                extrairAno(dados.publish_date).ifPresent(builder::anoPublicacao);
            }

            return Optional.of(builder.build());

        } catch (IOException | InterruptedException e) {
            return Optional.empty();
        }
    }

    static class IsbnResponse {
        String title;
        List<AuthorRef> authors;
        String publish_date;
        Integer number_of_pages;

        static class AuthorRef {
            String key;
        }
    }

    static class AuthorResponse {
        String name;
    }

    private String buscarNomeAutor(String authorKey) {
        String url = API_URL + authorKey + ".json";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }

            Gson gson = new Gson();
            AuthorResponse autor = gson.fromJson(response.body(), AuthorResponse.class);
            return autor != null ? autor.name : null;

        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    private Optional<Integer> extrairAno(String publishDate) {
        if (publishDate == null) {
            return Optional.empty();
        }
        Matcher matcher = Pattern.compile("\\d{4}").matcher(publishDate);
        if (matcher.find()) {
            return Optional.of(Integer.parseInt(matcher.group()));
        }
        return Optional.empty();
    }

}