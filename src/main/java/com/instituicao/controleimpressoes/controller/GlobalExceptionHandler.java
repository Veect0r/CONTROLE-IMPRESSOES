package com.instituicao.controleimpressoes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleUnexpectedException(Exception exception, Model model) {
        logger.error("Unhandled exception while processing request", exception);
        model.addAttribute("mensagemErro", "Ocorreu um problema inesperado. Tente novamente em alguns instantes.");
        return "error";
    }
}
