package br.com.alura.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DadosAutor (@JsonAlias("name") String nome,
                          @JsonAlias("birth_year") Integer anoDenascimento,
                          @JsonAlias("death_year") Integer anoDeFalecimento) {

}
