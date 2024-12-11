package br.com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "titulo" ,unique = true)
    private String titulo;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "livro_autor",
            joinColumns = @JoinColumn(name = "livro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> autores;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = String.class)
    @Column(name = "idiomas")
    private List<String> idiomas;

    @Column(name = "numero_downloads")
    private Integer numeroDeDownloads = 0;

    public Livro(String titulo, List<String> idiomas, Integer numeroDeDownloads) {
        this.titulo = titulo;
        this.idiomas = idiomas;
        this.numeroDeDownloads = numeroDeDownloads;
    }

    public Livro () {}

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    public void setNumeroDeDownloads(Integer numeroDeDownloads) {
        this.numeroDeDownloads = numeroDeDownloads;
    }

    public List<String> getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(List<String> idioma) {
        this.idiomas = idiomas;
    }

    public int getNumeroDeDownloads() {
        return numeroDeDownloads;
    }

    public void setNumeroDeDownloads(int numeroDeDownloads) {
        this.numeroDeDownloads = numeroDeDownloads;
    }

    @Override
    public String toString() {
        return "titulo= '" + this.getTitulo() + '\'' +
                ", autore= '" + this.getAutores().stream().map(Autor::getNome).collect(Collectors.joining(", ")) + '\'' +
                ", idiomas= '" + this.getIdiomas() + '\'' +
                ", numeroDeDownloads= " + this.getNumeroDeDownloads();
    }
}
