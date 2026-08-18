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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LivroAPIService {
    public static final String API_URL = "https://openlibrary.org";
    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    private static final int MAX_TENTATIVAS = 3;
    private static final Duration ESPERA_INICIAL = Duration.ofMillis(500);
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
                        .timeout(TIMEOUT)
                    .GET()
                    .build();

                    HttpResponse<String> response = enviarComTentativa(request);

            if (response.statusCode() == 404) {
                return buscarPorIsbnNaPesquisa(isbnNormalizado);
            }
            if (response.statusCode() != 200) {
                throw new IllegalStateException("A API retornou o status " + response.statusCode() + ".");
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
            if (nomeAutor == null || nomeAutor.isBlank()) {
                nomeAutor = buscarAutorPorIsbnNaPesquisa(isbnNormalizado);
            }
            Livro.LivroBuilder builder = Livro.builder()
                    .titulo(dados.title != null ? dados.title : "")
                    .autor(nomeAutor != null ? nomeAutor : "Autor não informado")
                    .paginas(dados.number_of_pages != null ? dados.number_of_pages : 1);

            if (dados.publish_date != null) {
                extrairAno(dados.publish_date).ifPresent(builder::anoPublicacao);
            }

            return Optional.of(builder.build());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("A consulta do livro foi interrompida.", e);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível consultar a API de livros: " + e.getMessage(), e);
        }
    }

    private Optional<Livro> buscarPorIsbnNaPesquisa(String isbn) {
        String url = API_URL + "/search.json?isbn=" + URLEncoder.encode(isbn, StandardCharsets.UTF_8);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                        .timeout(TIMEOUT)
                    .GET()
                    .build();

                    HttpResponse<String> response = enviarComTentativa(request);
            if (response.statusCode() != 200) {
                throw new IllegalStateException("A API retornou o status " + response.statusCode() + ".");
            }

            PesquisaResponse pesquisa = new Gson().fromJson(response.body(), PesquisaResponse.class);
            if (pesquisa == null || pesquisa.docs == null || pesquisa.docs.isEmpty()) {
                return Optional.empty();
            }

            LivroPesquisa livro = pesquisa.docs.get(0);
            String autor = livro.author_name != null && !livro.author_name.isEmpty() ? livro.author_name.get(0) : "";
            int paginas = livro.number_of_pages_median != null ? livro.number_of_pages_median : 1;

            Livro.LivroBuilder builder = Livro.builder()
                    .titulo(livro.title != null ? livro.title : "")
                    .autor(autor)
                    .paginas(paginas);

            if (livro.first_publish_year != null) {
                builder.anoPublicacao(livro.first_publish_year);
            }

            return Optional.of(builder.build());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("A consulta do livro foi interrompida.", e);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível consultar a API de livros: " + e.getMessage(), e);
        }
    }

    private String buscarAutorPorIsbnNaPesquisa(String isbn) {
        String url = API_URL + "/search.json?isbn=" + URLEncoder.encode(isbn, StandardCharsets.UTF_8);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(TIMEOUT)
                    .GET()
                    .build();

            HttpResponse<String> response = enviarComTentativa(request);
            if (response.statusCode() != 200) {
                return null;
            }

            PesquisaResponse pesquisa = new Gson().fromJson(response.body(), PesquisaResponse.class);
            if (pesquisa == null || pesquisa.docs == null || pesquisa.docs.isEmpty()) {
                return null;
            }

            List<String> autores = pesquisa.docs.get(0).author_name;
            return autores != null && !autores.isEmpty() ? autores.get(0) : null;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return null;
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

    static class PesquisaResponse {
        List<LivroPesquisa> docs;
    }

    static class LivroPesquisa {
        String title;
        List<String> author_name;
        Integer number_of_pages_median;
        Integer first_publish_year;
    }

    static class AuthorResponse {
        String name;
    }

    private HttpResponse<String> enviarComTentativa(HttpRequest request) throws IOException, InterruptedException {
        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (!statusTemporario(response.statusCode()) || tentativa == MAX_TENTATIVAS) {
                    return response;
                }
            } catch (java.net.http.HttpTimeoutException e) {
                if (tentativa == MAX_TENTATIVAS) {
                    throw e;
                }
            }

            Thread.sleep(ESPERA_INICIAL.toMillis() * tentativa);
        }

        throw new IllegalStateException("Não foi possível concluir a requisição à API.");
    }

    private boolean statusTemporario(int statusCode) {
        return statusCode == 429 || statusCode >= 500;
    }

    private String buscarNomeAutor(String authorKey) {
        String url = API_URL + authorKey + ".json";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                        .timeout(TIMEOUT)
                    .GET()
                    .build();

                    HttpResponse<String> response = enviarComTentativa(request);
            if (response.statusCode() != 200) {
                return null;
            }

            Gson gson = new Gson();
            AuthorResponse autor = gson.fromJson(response.body(), AuthorResponse.class);
            return autor != null ? autor.name : null;

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
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