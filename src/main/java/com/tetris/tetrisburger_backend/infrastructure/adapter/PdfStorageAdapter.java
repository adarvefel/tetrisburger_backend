package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.exception.PdfDownloadException;
import com.tetris.tetrisburger_backend.domain.port.out.PdfStoragePort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class PdfStorageAdapter implements PdfStoragePort {

    @Override
    public InputStream downloadPdf(String pdfUrl) {
        try {
            URL url = new URL(pdfUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            return connection.getInputStream();
        } catch (IOException e) {
            throw new PdfDownloadException(
                    "No se pudo descargar el PDF desde: " + pdfUrl, e);
        }
    }
}
