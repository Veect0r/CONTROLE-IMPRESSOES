package com.instituicao.controleimpressoes.service;

import com.instituicao.controleimpressoes.model.Professor;
import com.instituicao.controleimpressoes.model.ResumoImpressoes;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ImpressaoServiceTest {

    @Test
    void deveContarSomenteDiasUteisNoResumo() {
        ImpressaoService service = new ImpressaoService(mock(GoogleSheetsService.class));

        ResumoImpressoes resumo = service.calcularResumo(List.of(
                new Professor(1, "Professor", "Turma", 10, 5, 15)
        ));

        LocalDate hoje = resumo.getHoje();
        long diasUteisEsperados = 0;

        for (LocalDate dia = hoje.withDayOfMonth(1); !dia.isAfter(hoje); dia = dia.plusDays(1)) {
            switch (dia.getDayOfWeek()) {
                case SATURDAY, SUNDAY -> {
                }
                default -> diasUteisEsperados++;
            }
        }

        assertEquals(diasUteisEsperados, resumo.getDiasTrabalhados());
        assertEquals(10, resumo.getTotalPB());
        assertEquals(5, resumo.getTotalColorida());
        assertEquals(15, resumo.getTotalGeral());
    }
}
