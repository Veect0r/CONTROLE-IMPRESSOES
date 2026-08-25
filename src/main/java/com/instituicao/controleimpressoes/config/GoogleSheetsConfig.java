package com.instituicao.controleimpressoes.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.instituicao.controleimpressoes.service.GoogleSheetsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import org.springframework.util.StringUtils;


@Configuration
public class GoogleSheetsConfig {

    private static final Logger logger = LoggerFactory.getLogger(GoogleSheetsConfig.class);
    private static final String APPLICATION_NAME = "Controle de Impressões";
    private static final String SHEETS_SCOPE = SheetsScopes.SPREADSHEETS;

    @Value("${google.sheets.credentials-path}")
    private Resource credentialsResource;

    @Value("${google.sheets.spreadsheet-id}")
    private String spreadsheetId;

    @Bean
    public GoogleSheetsService googleSheetsService() {
        Sheets sheets = buildSheetsService();
        return new GoogleSheetsService(sheets, spreadsheetId);
    }

    private Sheets buildSheetsService() {
        if (!StringUtils.hasText(spreadsheetId)) {
            logger.warn("Google Sheets integration disabled because GOOGLE_SHEETS_SPREADSHEET_ID is not configured.");
            return null;
        }

        if (!credentialsResource.exists() || !credentialsResource.isReadable()) {
            logger.warn("Google Sheets credentials not found at {}. The application will run without Google Sheets integration.", credentialsResource);
            return null;
        }

        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleCredentials credentials;
            try (InputStream in = credentialsResource.getInputStream()) {
                credentials = GoogleCredentials.fromStream(in)
                        .createScoped(Collections.singletonList(SHEETS_SCOPE));
            }
            return new Sheets.Builder(httpTransport, GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException | GeneralSecurityException exception) {
            logger.warn("Google Sheets integration disabled because credentials could not be loaded: {}", exception.getMessage());
            return null;
        }
    }
}
