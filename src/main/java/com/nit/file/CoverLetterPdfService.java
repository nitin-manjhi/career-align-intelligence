package com.nit.file;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.nit.domain.AIResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
@Service
public class CoverLetterPdfService {
    private static final Font TITLE = new Font(Font.HELVETICA, 16, Font.BOLD);
    private static final Font NORMALCOVERLETTER = new Font(Font.HELVETICA, 11);
    /* ================= COVER LETTER PDF ================= */

    public byte[] generateCoverLetterPdf(AIResponse response) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
        PdfWriter.getInstance(doc, out);

        doc.open();

        doc.add(new Paragraph("Cover Letter", TITLE));
        doc.add(Chunk.NEWLINE);

        addMultilineText(doc, response.getCoverLetter());

        doc.close();

        return out.toByteArray();
    }

    /* ================= COMMON HELPER ================= */

    private void addMultilineText(Document doc, String text) throws Exception {
        if (text == null) return;

        String[] lines = text.split("\\r?\\n");

        for (String line : lines) {
            doc.add(new Paragraph(line, NORMALCOVERLETTER));
        }
    }
}
