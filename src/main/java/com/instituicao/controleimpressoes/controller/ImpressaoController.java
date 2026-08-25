package com.instituicao.controleimpressoes.controller;

import com.instituicao.controleimpressoes.model.Professor;
import com.instituicao.controleimpressoes.model.RegistroImpressaoRequest;
import com.instituicao.controleimpressoes.service.ImpressaoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class ImpressaoController {

    private static final Logger logger = LoggerFactory.getLogger(ImpressaoController.class);

    private final ImpressaoService impressaoService;

    public ImpressaoController(ImpressaoService impressaoService) {
        this.impressaoService = impressaoService;
    }

    @GetMapping("/")
    public String telaInicial(Model model) {
        carregarProfessores(model);
        model.addAttribute("registro", new RegistroImpressaoRequest());
        return "index";
    }

    @PostMapping("/registrar")
    public String registrar(@Valid @ModelAttribute("registro") RegistroImpressaoRequest registro,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            carregarProfessores(model);
            return "index";
        }

        try {
            impressaoService.registrar(registro.getProfessorId(), registro.getQuantidadePB(),
                    registro.getQuantidadeColor());
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Impressão registrada com sucesso!");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            logger.warn("Falha de validação ao registrar impressão", e);
            carregarProfessores(model);
            model.addAttribute("registro", registro);
            model.addAttribute("mensagemErro", e.getMessage());
            return "index";
        } catch (IOException e) {
            logger.error("Erro ao registrar impressão", e);
            carregarProfessores(model);
            model.addAttribute("registro", registro);
            model.addAttribute("mensagemErro",
                    "Não foi possível atualizar a planilha. Tente novamente em instantes.");
            return "index";
        } catch (Exception e) {
            logger.error("Erro inesperado ao registrar impressão", e);
            carregarProfessores(model);
            model.addAttribute("registro", registro);
            model.addAttribute("mensagemErro", "Ocorreu um erro inesperado. Tente novamente em instantes.");
            return "index";
        }
    }

    private void carregarProfessores(Model model) {
        try {
            List<Professor> professores = impressaoService.listarProfessores();
            model.addAttribute("professores", professores);
            model.addAttribute("resumo", impressaoService.calcularResumo(professores));
        } catch (IOException e) {
            logger.warn("Falha ao carregar professores da planilha", e);
            model.addAttribute("professores", List.of());
            model.addAttribute("mensagemErro", "Não foi possível carregar os professores da planilha.");
        }
    }
}
