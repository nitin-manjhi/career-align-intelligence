package com.nit.file;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.nit.domain.AIResponse;
import com.nit.repository.AnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumePdfService {
    /* ================= RESUME PDF ================= */

    private static final Font NAME =
            new Font(Font.HELVETICA, 18, Font.BOLD);

    private static final Font ROLE =
            new Font(Font.HELVETICA, 12, Font.BOLD);

    private static final Font HEADER =
            new Font(Font.HELVETICA, 11, Font.BOLD);

    private static final Font NORMAL =
            new Font(Font.HELVETICA, 10);



    public byte[] generateOnePageResume(AIResponse r) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(doc, out);

        doc.open();

        /* ===== NAME ===== */
        Paragraph name = new Paragraph(extractName(r), NAME);
        name.setSpacingAfter(2);
        doc.add(name);

        /* ===== ROLE ===== */
        Paragraph role = new Paragraph(extractRole(r), ROLE);
        role.setSpacingAfter(4);
        doc.add(role);

        /* ===== CONTACT ===== */
        Paragraph contact = new Paragraph(extractContact(r), NORMAL);
        contact.setSpacingAfter(8);
        doc.add(contact);

        /* ===== SUMMARY ===== */
        addSection(doc, "SUMMARY", extractSummary(r));

        /* ===== SKILLS (compact table) ===== */
        addSkillsGrid(doc, extractSkills(r));

        /* ===== EXPERIENCE ===== */
        addSection(doc, "WORK EXPERIENCE", extractExperience(r));

        /* ===== EDUCATION ===== */
        addSection(doc, "EDUCATION", extractEducation(r));

        /* ===== ADDITIONAL ===== */
        addSection(doc, "ADDITIONAL INFORMATION", extractAdditional(r));

        doc.close();
        return out.toByteArray();
    }

    private String extractSummary(AIResponse r) {
        return extractSection(r.getNewResume(), "SUMMARY");
    }

    private List<String> extractSkills(AIResponse r) {

        String raw = extractSection(r.getNewResume(), "SKILLS");

        return Arrays.stream(raw.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private String extractExperience(AIResponse r) {
        return extractSection(r.getNewResume(), "WORK EXPERIENCE");
    }

    private String extractEducation(AIResponse r) {
        return extractSection(r.getNewResume(), "EDUCATION");
    }

    private String extractAdditional(AIResponse r) {
        return extractSection(r.getNewResume(), "ADDITIONAL INFORMATION");
    }

    private void addSection(Document doc, String title, String content) throws Exception {

        Paragraph header = new Paragraph(title, HEADER);
        header.setSpacingBefore(6);
        header.setSpacingAfter(2);
        doc.add(header);

        for (String line : content.split("\\r?\\n")) {
            Paragraph p = new Paragraph(line, NORMAL);
            p.setSpacingAfter(1);
            doc.add(p);
        }
    }

    private void addSkillsGrid(Document doc, List<String> skills) throws Exception {

        Paragraph header = new Paragraph("SKILLS", HEADER);
        header.setSpacingBefore(6);
        header.setSpacingAfter(4);
        doc.add(header);

        PdfPTable table = new PdfPTable(3); // 3 columns → compact
        table.setWidthPercentage(100);

        for (String skill : skills) {
            PdfPCell cell = new PdfPCell(new Phrase(skill, NORMAL));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(2);
            table.addCell(cell);
        }

        doc.add(table);
    }

    private String extractName(AIResponse r) {
        return firstLine(r.getNewResume());
    }

    private String extractRole(AIResponse r) {
        return secondLine(r.getNewResume());
    }

    private String extractContact(AIResponse r) {
        return thirdLine(r.getNewResume());
    }

    private String firstLine(String text) {
        return text.split("\\n")[0];
    }

    private String secondLine(String text) {
        return text.split("\\n").length > 1 ? text.split("\\n")[1] : "";
    }

    private String thirdLine(String text) {
        return text.split("\\n").length > 2 ? text.split("\\n")[2] : "";
    }

    private String extractSection(String resume, String sectionName) {

        String upper = resume.toUpperCase();

        int start = upper.indexOf(sectionName);
        if (start == -1) return "";

        start += sectionName.length();

        // find next section header
        int end = upper.length();

        String[] headers = {
                "SUMMARY",
                "SKILLS",
                "WORK EXPERIENCE",
                "EDUCATION",
                "ADDITIONAL INFORMATION"
        };

        for (String h : headers) {
            if (h.equals(sectionName)) continue;

            int idx = upper.indexOf(h, start);
            if (idx != -1 && idx < end) {
                end = idx;
            }
        }

        return resume.substring(start, end).trim();
    }
}
