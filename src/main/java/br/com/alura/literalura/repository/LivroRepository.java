package br.com.alura.literalura.repository;

import br.com.alura.literalura.model.Autor;
import br.com.alura.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface LivroRepository extends JpaRepository<Livro, Long> {
    Optional<Livro> findByTituloContainingIgnoreCase(String titulo);

    @Query("SELECT a FROM Livro b JOIN b.autores a WHERE a.nome ILIKE %:nomeAutor%")
    Optional<Autor> findAllAutoresByNome(String nomeAutor);

    @Query("SELECT a FROM Livro b JOIN b.autores a")
    List<Autor> findAllAutores();

    @Query("SELECT a FROM Livro b JOIN b.autores a WHERE :ano >= a.anoDeNascimento AND :ano <= a.anoDeFalecimento")
    List<Autor> findAllAutoresByAno(int ano);

    @Query("SELECT b FROM Livro b JOIN b.idiomas l WHERE l ILIKE %:idioma%")
    List<Livro> findAllLivrosByIdioma(String idioma);
}
