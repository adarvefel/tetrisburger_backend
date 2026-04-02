package com.tetris.tetrisburger_backend.domain.port.out;

import java.io.InputStream;

public interface PdfStoragePort {
    InputStream downloadPdf(String pdfUrl);
}