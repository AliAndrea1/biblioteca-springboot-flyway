package com.example.biblioteca.controller;

import com.example.biblioteca.model.Livro;
import com.example.biblioteca.repository.LivroRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.biblioteca.repository.EmprestimoRepository;

@Controller
@RequestMapping("/livros")
public class LivroController {
    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("livros", livroRepository.findAll());
        return "livros"; // era "livros/listar"
    }

    @GetMapping("/novo")
    public String novo(Livro livro) {
        return "livro-form"; // era "livros/form"
    }

    @PostMapping
    public String salvar(@Valid Livro livro, BindingResult result) {
        if (result.hasErrors()) {
            return "livro-form";
        }
        livroRepository.save(livro);
        return "redirect:/livros";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livro inválido: " + id));
        model.addAttribute("livro", livro);
        return "livro-form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, Model model) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livro inválido: " + id));

        boolean emprestimoAtivo = emprestimoRepository.findByDevolvidoFalse().stream()
                .anyMatch(e -> e.getLivro().getId().equals(id));

        if (emprestimoAtivo) {
            model.addAttribute("erro", "❌ Este livro está emprestado e não pode ser excluído até ser devolvido.");
            model.addAttribute("livros", livroRepository.findAll());
            return "livros";
        }

        emprestimoRepository.findAll().stream()
                .filter(e -> e.getLivro().getId().equals(id) && e.isDevolvido())
                .forEach(emprestimoRepository::delete);

        livroRepository.delete(livro);

        model.addAttribute("mensagem", "✅ Livro excluído com sucesso!");
        model.addAttribute("livros", livroRepository.findAll());
        return "livros";
    }

    @GetMapping("/disponiveis")
    public String listarDisponiveis(Model model) {
        model.addAttribute("livros", livroRepository.findByEmprestadoFalse());
        model.addAttribute("titulo", "Livros Disponíveis");
        return "livros";
    }

    @GetMapping("/emprestados")
    public String listarEmprestados(Model model) {
        model.addAttribute("livros", livroRepository.findByEmprestadoTrue());
        model.addAttribute("titulo", "Livros Emprestados");
        return "livros";
    }

}