package br.com.alura.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BuscarDados(@JsonAlias("results") List<DadosLivro> livros) {

    @Override
    public String toString() {
        StringBuilder listaDeLivros = new StringBuilder();

        for (var i = 0; i < livros().size(); i++) {
            listaDeLivros.append("\n").append(i + 1).append("-\n").append(livros().get(i).toString());
        }

        return listaDeLivros.toString();
    }
}
