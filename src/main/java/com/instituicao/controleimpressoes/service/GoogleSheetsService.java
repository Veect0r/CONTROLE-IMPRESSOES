package com.instituicao.controleimpressoes.service;

import com.google.api.client.http.HttpResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.instituicao.controleimpressoes.model.Professor;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

public final class GoogleSheetsService {

    private static final String RANGE_FORMAT = "%s!%s%d:%s";
    private static final String UPDATE_RANGE_FORMAT = "%s!D%d:E%d";
    private static final String EMPTY_VALUE = "";
    private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");
    private static final String CREDENTIALS_HINT = "Adicione o arquivo de credenciais em src/main/resources/credentials/service-account.json.";

    private final Sheets sheetsService;
    private final String spreadsheetId;
    private final boolean enabled;
    private final ReentrantLock registroLock = new ReentrantLock();

    public GoogleSheetsService(@Nullable Sheets sheetsService, String spreadsheetId) {
        this.sheetsService = sheetsService;
        this.spreadsheetId = spreadsheetId;
        this.enabled = sheetsService != null;
    }

    public List<Professor> listarProfessores() throws IOException {
        requireEnabled("Serviço Google Sheets não está configurado. " + CREDENTIALS_HINT);

        String aba = abaDoMesAtual();
        String range = String.format(RANGE_FORMAT, aba, "A", 2, "F");
        ValueRange response;
        try {
            response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
        } catch (HttpResponseException exception) {
            throw translateSheetsException(exception, aba);
        }

        List<List<Object>> linhas = response.getValues();
        if (linhas == null) {
            return List.of();
        }

        List<Professor> professores = new ArrayList<>();
        for (List<Object> linha : linhas) {
            if (linha.isEmpty() || linha.get(0) == null) {
                continue;
            }
            professores.add(fromRow(linha));
        }

        return professores;
    }

    /**
     * Registra a impressão de forma serializada dentro desta instância da aplicação.
     *
     * A API do Google Sheets não oferece uma operação de "incremento atômico" para
     * uma célula. O lock impede que duas requisições do mesmo servidor façam
     * simultaneamente o ciclo ler -> calcular -> escrever e uma atualização seja perdida.
     *
     * Para múltiplas instâncias do sistema, o ideal é persistir os eventos em uma
     * tabela/serviço com transação ou usar uma aba de eventos com append.
     */
    public void registrarImpressao(Integer professorId, int quantidadePB, int quantidadeColor) throws IOException {
        requireEnabled("Serviço Google Sheets não está configurado. Não é possível registrar a impressão.");
        validateProfessorId(professorId);

        registroLock.lock();
        try {
            registrarImpressaoComLock(professorId, quantidadePB, quantidadeColor);
        } finally {
            registroLock.unlock();
        }
    }

    private void registrarImpressaoComLock(Integer professorId, int quantidadePB, int quantidadeColor) throws IOException {
        String aba = abaDoMesAtual();
        String range = String.format(RANGE_FORMAT, aba, "A", 2, "F");

        ValueRange response;
        try {
            response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
        } catch (HttpResponseException exception) {
            throw translateSheetsException(exception, aba);
        }

        List<List<Object>> linhas = response.getValues();
        if (linhas == null) {
            throw new IllegalStateException("Não foi possível localizar dados na aba " + aba);
        }

        for (int i = 0; i < linhas.size(); i++) {
            List<Object> linha = linhas.get(i);
            if (linha.isEmpty() || linha.get(0) == null) {
                continue;
            }

            Integer idLinha = parseInteger(linha.get(0));
            if (!professorId.equals(idLinha)) {
                continue;
            }

            int novoPB = obterValorInteiro(linha, 3) + quantidadePB;
            int novoColor = obterValorInteiro(linha, 4) + quantidadeColor;
            int linhaPlanilha = 2 + i;
            String updateRange = String.format(UPDATE_RANGE_FORMAT, aba, linhaPlanilha, linhaPlanilha);

            ValueRange body = new ValueRange()
                    .setValues(Collections.singletonList(List.of(novoPB, novoColor)));

            try {
                sheetsService.spreadsheets().values()
                        .update(spreadsheetId, updateRange, body)
                        .setValueInputOption("USER_ENTERED")
                        .execute();
            } catch (HttpResponseException exception) {
                throw translateSheetsException(exception, aba);
            }
            return;
        }

        throw new IllegalArgumentException("Professor com ID " + professorId + " não encontrado na aba " + aba);
    }

    private Professor fromRow(List<Object> linha) {
        Integer id = parseInteger(linha.get(0));
        String nome = linha.size() > 1 ? linha.get(1).toString().trim() : EMPTY_VALUE;
        String turma = linha.size() > 2 ? linha.get(2).toString().trim() : EMPTY_VALUE;
        int pb = obterValorInteiro(linha, 3);
        int color = obterValorInteiro(linha, 4);
        int total = obterValorInteiro(linha, 5);
        return new Professor(id, nome, turma, pb, color, total);
    }

    private String abaDoMesAtual() {
        String mes = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, LOCALE_PT_BR);
        return mes.toUpperCase(Locale.ROOT);
    }

    private int obterValorInteiro(List<Object> linha, int indice) {
        if (linha.size() <= indice || linha.get(indice) == null) {
            return 0;
        }

        String valor = linha.get(indice).toString().trim();
        if (valor.isEmpty()) {
            return 0;
        }

        try {
            return (int) Double.parseDouble(valor);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private Integer parseInteger(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value.toString().trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void validateProfessorId(Integer professorId) {
        if (professorId == null) {
            throw new IllegalArgumentException("Selecione um professor antes de registrar a impressão.");
        }
    }

    private IOException translateSheetsException(HttpResponseException exception, String aba) {
        if (exception.getStatusCode() == 404) {
            return new IOException("A aba '" + aba + "' não existe na planilha. Crie a aba do mês atual antes de continuar.", exception);
        }
        return new IOException("Erro ao acessar a planilha do Google Sheets: " + exception.getStatusMessage(), exception);
    }

    private void requireEnabled(String message) throws IOException {
        if (!enabled) {
            throw new IOException(message);
        }
    }
}
