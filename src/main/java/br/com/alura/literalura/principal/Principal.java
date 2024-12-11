package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.Autor;
import br.com.alura.literalura.model.BuscarDados;
import br.com.alura.literalura.model.DadosLivro;
import br.com.alura.literalura.model.Livro;
import br.com.alura.literalura.repository.LivroRepository;
import br.com.alura.literalura.service.ConsumoAPI;
import br.com.alura.literalura.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.IntStream;
import org.springframework.dao.DataIntegrityViolationException;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI("https://gutendex.com/books");
    private ConverteDados conversor = new ConverteDados();
    private LivroRepository repositorio;


    public Principal(LivroRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibirMenu() {
        var opcao = -1;

        while (opcao != 0) {
            System.out.println(("""
                       -----------------------
                       Escolha o número de sua opção:
                       1 - Buscar livro pelo título
                       2 - Listar livros registrados
                       3 - Listar autores registrados
                       4 - Listar autores vivos em um determinado ano
                       5 - Listar livros em um determinado idioma
                    
                       0 - Sair
                    """));

            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarLivroPorTitulo();
                    break;
                case 2:
                    listarLivros();
                    break;
                case 3:
                    listarAutores();
                    break;
                case 4:
                    listarAutoresVivosEmAno();
                    break;
                case 5:
                    listarLivrosEmIdioma();
                    break;
                case 0:
                    System.out.println("\nSaindo...");
                    break;
            }
        }
    }

    private void buscarLivroPorTitulo() {
        System.out.println("Digite o nome do livro para pesquisa: ");
        String nomeLivro = leitura.nextLine();

        String json = consumo.getData(nomeLivro);
        BuscarDados resultados = conversor.obterDados(json, BuscarDados.class);
        DadosLivro dadosLivro = null;

        if (resultados.livros().isEmpty()) {
            System.out.printf("Nenhum livro \"%s\" encontrado! Tente outro livro.%n", nomeLivro);
        } else if (resultados.livros().size() > 1) {
            dadosLivro = buscarVariosLivros(resultados);
            System.out.println("Livro selecionado:\n" + dadosLivro);
        } else {
            dadosLivro = resultados.livros().get(0);
            System.out.println(dadosLivro);
        }

        if (dadosLivro != null) {
            List<Livro> livros = new ArrayList<>();

            List<Autor> autores = dadosLivro.autores()
                    .stream()
                    .map(a -> new Autor(a.nome(), a.anoDenascimento(), a.anoDeFalecimento())).toList();

            livros.add(new Livro(dadosLivro.titulo(), dadosLivro.idiomas(), dadosLivro.numeroDeDownloads()));
            autores.forEach(a -> a.setLivros(livros));
            livros.forEach(b -> b.setAutores(autores));

            try {
                livros.forEach(b -> repositorio.save(b));
            } catch (DataIntegrityViolationException e) {
                boolean livrosEstaPresente = repositorio.findByTituloContainingIgnoreCase(dadosLivro.titulo()).isPresent();
                boolean todosAutoresPresentes = autores.stream()
                        .map(a -> repositorio.findAllAutoresByNome(a.getNome())).allMatch(Optional::isPresent);
                if (livrosEstaPresente && todosAutoresPresentes) {
                    System.out.println("Este livro e autor já foram registrados!");
                } else {
                    List<Livro> novosLivros = handlerDataIntegrityViolationException(livros, autores);
                    novosLivros.forEach(b -> repositorio.saveAndFlush(b));
                }
            }
        }
    }

    private void listarLivros() {
        List<Livro> livrosEncontrados = repositorio.findAll();
        System.out.println("LIVROS REGISTRADOS: \n");
        livrosEncontrados.forEach(System.out::println);
    }

    private void listarAutores() {
        List<Autor> autoresEncontrados = repositorio.findAllAutores();

        if (autoresEncontrados.isEmpty()) {
            System.out.println("Nenhum autor foi registrado até o momento!");
        } else {
            System.out.println("AUTORES REGISTRADOS: \n");
            autoresEncontrados.forEach(System.out::println);
        }

    }

    private void listarAutoresVivosEmAno() {
        System.out.println("Digite o ano para a pesquisa dos autores vivos: ");
        var ano = leitura.nextInt();
        leitura.nextLine();

        List<Autor> autoresEncontrados = repositorio.findAllAutoresByAno(ano);

        if (autoresEncontrados.isEmpty()) {
            System.out.println("Não foram encontrados autores vivos em " + ano + "!");
        } else {
            System.out.println("AUTORES REGISTRADOS VIVOS EM " + ano + ":\n");
            autoresEncontrados.forEach(System.out::println);
        }
    }

    private void listarLivrosEmIdioma() {
        System.out.println("Digite a sigla do idioma para pesquisa: ");
        String idioma = leitura.nextLine();

        List<Livro> livrosEncontrados = repositorio.findAllLivrosByIdioma(idioma);

        if (livrosEncontrados.isEmpty()) {
            System.out.println("Não foram encontrados livros no idioma " + idioma + "!");
        } else {
            System.out.println("LIVROS REGISTRADOS NESSE IDIOMA: \n");
            livrosEncontrados.forEach(System.out::println);
        }
    }

    private List<Livro> handlerDataIntegrityViolationException(List<Livro> livros, List<Autor> autores) {
        Optional<Livro> livroOptional = repositorio.findByTituloContainingIgnoreCase(livros.get(0).getTitulo());
        List<Optional<Autor>> autoresOptional = autores.stream()
                .map(a -> repositorio.findAllAutoresByNome(a.getNome())).toList();

        List<Livro> listaDeLivros = new ArrayList<>();
        if (livroOptional.isPresent()) {
            listaDeLivros.add(livroOptional.get());
        } else {
            listaDeLivros.addAll(livros);
        }

        listaDeLivros.forEach(b -> b.setAutores(IntStream.range(0, autoresOptional.size()).mapToObj(a -> {
            if (autoresOptional.get(a).isPresent()) {
                return autoresOptional.get(a).get();
            }
            return autores.get(a);
        }).toList()));

        return listaDeLivros;
    }

    private DadosLivro buscarVariosLivros(BuscarDados resultados) { // selecting one of several books result
        System.out.println("Encontramos mais de um livro! Escolha um deles para registro: ");
        var opcao = -1;

        while (opcao < 1 || opcao > resultados.livros().size()) {
            System.out.println(resultados);
            System.out.println("Escolha uma opção:");
            opcao = leitura.nextInt();
            leitura.nextLine();

            if (opcao < 1 || opcao > resultados.livros().size()) {
                System.out.println("Opção incorreta! Tente novamente.");
            }
        }
        return resultados.livros().get(opcao - 1);
    }
}
