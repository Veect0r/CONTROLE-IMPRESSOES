package com.instituicao.controleimpressoes.service;

import com.instituicao.controleimpressoes.model.Professor;
import com.instituicao.controleimpressoes.model.ResumoImpressoes;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
public class ImpressaoService {

    private final GoogleSheetsService googleSheetsService;

    public ImpressaoService(GoogleSheetsService googleSheetsService) {
        this.googleSheetsService = googleSheetsService;
    }

    public List<Professor> listarProfessores() throws IOException {
        return googleSheetsService.listarProfessores();
    }

    public void registrar(Integer professorId, Integer quantidadePB, Integer quantidadeColor) throws IOException {
        int pb = quantidadePB != null ? Math.max(quantidadePB, 0) : 0;
        int color = quantidadeColor != null ? Math.max(quantidadeColor, 0) : 0;

        googleSheetsService.registrarImpressao(professorId, pb, color);
    }

    public ResumoImpressoes calcularResumo(List<Professor> professores) {
        int totalPB = 0;
        int totalColorida = 0;
        int totalGeral = 0;

        for (Professor p : professores) {
            totalPB += p.getPb();
            totalColorida += p.getColorida();
            totalGeral += p.getTotal();
        }

        LocalDate hoje = LocalDate.now();
        long diasTrabalhados = contarDiasUteisAteHoje(hoje);
        String nomeMes = hoje.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
        nomeMes = nomeMes.substring(0, 1).toUpperCase(Locale.ROOT) + nomeMes.substring(1);

        return new ResumoImpressoes(totalPB, totalColorida, totalGeral, diasTrabalhados, hoje, nomeMes);
    }

    private long contarDiasUteisAteHoje(LocalDate data) {
        LocalDate inicioDoMes = data.withDayOfMonth(1);
        long diasUteis = 0;

        for (LocalDate dia = inicioDoMes; !dia.isAfter(data); dia = dia.plusDays(1)) {
            DayOfWeek diaDaSemana = dia.getDayOfWeek();
            if (diaDaSemana != DayOfWeek.SATURDAY && diaDaSemana != DayOfWeek.SUNDAY) {
                diasUteis++;
            }
        }

        return diasUteis;
    }
}
