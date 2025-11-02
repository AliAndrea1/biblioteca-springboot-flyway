package com.example.biblioteca;

import com.example.biblioteca.model.Emprestimo;
import com.example.biblioteca.model.Livro;
import com.example.biblioteca.model.Usuario;
import com.example.biblioteca.repository.EmprestimoRepository;
import com.example.biblioteca.repository.LivroRepository;
import com.example.biblioteca.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class BibliotecaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliotecaApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(
			UsuarioRepository usuarioRepo,
			LivroRepository livroRepo,
			EmprestimoRepository emprestimoRepo) {

		return args -> {

			if (usuarioRepo.count() == 0) {
				usuarioRepo.saveAll(List.of(
						new Usuario("Ali Andrea Mamani Molle", "alimoland27@gmail.com"),
						new Usuario("Lucas Rodrigues", "lucasr@gmail.com"),
						new Usuario("Laura Souza", "laura.s@gmail.com")
				));
			}

			if (livroRepo.count() == 0) {
				livroRepo.saveAll(List.of(
						new Livro("O Pequeno Príncipe", "Antoine de Saint-Exupéry", 1943, false),
						new Livro("Dom Casmurro", "Machado de Assis", 1899, false),
						new Livro("1984", "George Orwell", 1949, false)
				));
			}

			if (emprestimoRepo.count() == 0) {

				Usuario ali = usuarioRepo.findAll().get(0);
				Usuario lucas = usuarioRepo.findAll().get(1);

				Livro pequenoPrincipe = livroRepo.findAll().get(0);
				Livro domCasmurro = livroRepo.findAll().get(1);

				Emprestimo e1 = new Emprestimo();
				e1.setLivro(pequenoPrincipe);
				e1.setUsuario(ali);
				e1.setDataRetirada(LocalDate.of(2025, 10, 1));
				e1.setDataPrevistaDevolucao(LocalDate.of(2025, 10, 10));
				e1.setDevolvido(false);

				pequenoPrincipe.setEmprestado(true);

				Emprestimo e2 = new Emprestimo();
				e2.setLivro(domCasmurro);
				e2.setUsuario(lucas);
				e2.setDataRetirada(LocalDate.of(2025, 10, 20));
				e2.setDataPrevistaDevolucao(LocalDate.of(2025, 11, 5));
				e2.setDevolvido(false);

				domCasmurro.setEmprestado(true);

				livroRepo.saveAll(List.of(pequenoPrincipe, domCasmurro));

				emprestimoRepo.saveAll(List.of(e1, e2));
			}

			emprestimoRepo.findByDevolvidoFalse().forEach(e -> {
				Livro livro = e.getLivro();
				if (!livro.isEmprestado()) {
					livro.setEmprestado(true);
					livroRepo.save(livro);
				}
			});

			livroRepo.findAll().forEach(l -> {
				boolean emprestadoAtivo = emprestimoRepo.findByDevolvidoFalse().stream()
						.anyMatch(e -> e.getLivro().getId().equals(l.getId()));
				if (!emprestadoAtivo && l.isEmprestado()) {
					l.setEmprestado(false);
					livroRepo.save(l);
				}
			});

			System.out.println("✅ Banco inicializado com dados de exemplo!");
		};
	}
}
