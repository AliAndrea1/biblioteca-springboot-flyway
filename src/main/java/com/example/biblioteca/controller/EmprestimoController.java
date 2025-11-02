package com.example.biblioteca.controller;

import com.example.biblioteca.model.Emprestimo;
import com.example.biblioteca.model.Livro;
import com.example.biblioteca.repository.EmprestimoRepository;
import com.example.biblioteca.repository.LivroRepository;
import com.example.biblioteca.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoRepository emprestimoRepository;
    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("emprestimos", emprestimoRepository.findByDevolvidoFalse());
        return "emprestimos";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("emprestimo", new Emprestimo());
        model.addAttribute("livros", livroRepository.findByEmprestadoFalse());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "emprestimo-form";
    }

    @PostMapping
    public String salvar(@Valid Emprestimo emprestimo, BindingResult result, Model model) {
        if (emprestimo.getDataPrevistaDevolucao() != null &&
                emprestimo.getDataRetirada() != null &&
                !emprestimo.getDataPrevistaDevolucao().isAfter(emprestimo.getDataRetirada())) {

            result.rejectValue("dataPrevistaDevolucao", "error.emprestimo",
                    "A data de devolução deve ser posterior à data de retirada.");
        }

        if (result.hasErrors()) {
            model.addAttribute("livros", livroRepository.findByEmprestadoFalse());
            model.addAttribute("usuarios", usuarioRepository.findAll());
            return "emprestimo-form";
        }

        Livro livro = emprestimo.getLivro();
        livro.setEmprestado(true);
        livroRepository.save(livro);

        emprestimoRepository.save(emprestimo);
        return "redirect:/emprestimos";
    }


    @GetMapping("/devolver/{id}")
    public String devolver(@PathVariable Long id) {
        Emprestimo emp = emprestimoRepository.findById(id).orElseThrow();
        emp.setDevolvido(true);
        Livro livro = emp.getLivro();
        livro.setEmprestado(false);
        livroRepository.save(livro);
        emprestimoRepository.save(emp);
        return "redirect:/emprestimos";
    }
}
