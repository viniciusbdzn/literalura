package br.com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nome" ,unique = true)
    private String nome;

    @ManyToMany(mappedBy = "autores", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Livro> livros = new ArrayList<>();
    @Column(name = "ano_nascimento")
    private int anoDeNascimento;
    @Column(name = "ano_falecimento")
    private int anoDeFalecimento;

    public Autor(String nome, Integer anoDeNascimento, Integer anoDeFalecimento) {
        this.nome = nome;
        this.anoDeNascimento = anoDeNascimento;
        this.anoDeFalecimento = anoDeFalecimento;
    }

    public Autor() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    @OneToMany(mappedBy = "autor")
    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }

    public int getAnoDeNascimento() {
        return anoDeNascimento;
    }

    public void setAnoDeNascimento(int anoDeNascimento) {
        this.anoDeNascimento = anoDeNascimento;
    }

    public int getAnoDeFalecimento() {
        return anoDeFalecimento;
    }

    public void setAnoDeFalecimento(int anoDeFalecimento) {
        this.anoDeFalecimento = anoDeFalecimento;
    }

    @Override
    public String toString() {
        return "nome= '" + nome +
                ", livro= " + livros +
                ", anoDeNascimento= " + anoDeNascimento +
                ", anoDeFalecimento= " + anoDeFalecimento;
    }
}
