package lk.sasindi.smartcity360.util;

import lk.sasindi.smartcity360.patterns.composite.PowerStatus;
import lk.sasindi.smartcity360.patterns.composite.UsageReport;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class UsageReportPdf {

    private static final int PAGE_WIDTH = 595;
    private static final int PAGE_HEIGHT = 842;
    private static final int MARGIN = 42;
    private static final int CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN;

    private static final int ROW_HEIGHT = 18;
    private static final int TABLE_HEADER_HEIGHT = 20;
    private static final int BOTTOM_LIMIT = PAGE_HEIGHT - 58;

    private static final int GRAPHITE = 0x202C27;
    private static final int GRAPHITE_LIGHT = 0x29332D;
    private static final int LIME = 0xB7E47A;
    private static final int TEXT_DARK = 0x202923;
    private static final int TEXT_MUTED = 0x758078;
    private static final int PANEL = 0xEDF1EA;
    private static final int ZEBRA = 0xF4F7F2;
    private static final int LINE = 0xD8E1D5;
    private static final int WHITE = 0xFFFFFF;
    private static final int GREEN = 0x3F7D2A;
    private static final int AMBER = 0xB7791F;
    private static final int RED = 0xB23B3B;

    private static final int COL_COMPONENT = MARGIN + 8;
    private static final int COL_TYPE = 222;
    private static final int COL_CODE = 277;
    private static final int COL_BUILDINGS_RIGHT = 392;
    private static final int COL_POWERED_RIGHT = 444;
    private static final int COL_STATUS = 456;
    private static final int COL_USAGE_RIGHT = PAGE_WIDTH - MARGIN - 8;

    private static final int[] WIDTH_REGULAR = {
            278, 278, 355, 556, 556, 889, 667, 191, 333, 333, 389, 584, 278,
            333, 278, 278, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556,
            278, 278, 584, 584, 584, 556, 1015, 667, 667, 722, 722, 667, 611,
            778, 722, 278, 500, 667, 556, 833, 722, 778, 667, 778, 722, 667,
            611, 722, 667, 944, 667, 667, 611, 278, 278, 278, 469, 556, 333,
            556, 556, 500, 556, 556, 278, 556, 556, 222, 222, 500, 222, 833,
            556, 556, 556, 556, 333, 500, 278, 556, 500, 722, 500, 500, 500,
            334, 260, 334, 584
    };

    private static final int[] WIDTH_BOLD = {
            278, 333, 474, 556, 556, 889, 722, 238, 333, 333, 389, 584, 278,
            333, 278, 278, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556,
            333, 333, 584, 584, 584, 611, 975, 722, 722, 722, 722, 667, 611,
            778, 722, 278, 556, 722, 611, 833, 722, 778, 667, 778, 722, 667,
            611, 722, 667, 944, 667, 667, 611, 333, 278, 333, 584, 556, 333,
            556, 611, 556, 611, 556, 333, 611, 611, 278, 278, 556, 278, 889,
            611, 611, 611, 611, 389, 556, 333, 611, 556, 778, 556, 556, 500,
            389, 280, 389, 584
    };

    private UsageReportPdf() {

    }

    public static byte[] build(UsageReport report, String generatedAt) {

        List<Page> pages = new ArrayList<>();

        String scope = report.getType() + ": " + report.getName();

        Page page = startFirstPage(report, scope, generatedAt);
        pages.add(page);

        double y = drawSummary(page, report);

        y = drawTableHeader(page, y);

        int rowIndex = 0;

        for (RowItem row : flatten(report, 0)) {

            if (y + ROW_HEIGHT > BOTTOM_LIMIT) {

                page = startContinuationPage(scope);
                pages.add(page);

                y = drawTableHeader(page, 64);
            }

            drawRow(page, y, row, rowIndex++);

            y += ROW_HEIGHT;
        }

        y += 14;

        if (y + 26 > BOTTOM_LIMIT) {

            page = startContinuationPage(scope);
            pages.add(page);

            y = 70;
        }

        drawNote(page, y);

        for (int i = 0; i < pages.size(); i++) {
            drawFooter(pages.get(i), i + 1, pages.size(), generatedAt);
        }

        return serialize(pages, "Energy usage report - " + report.getName());
    }

    private static Page startFirstPage(
            UsageReport report,
            String scope,
            String generatedAt
    ) {

        Page page = new Page();

        fillRect(page, 0, 0, PAGE_WIDTH, 88, GRAPHITE);
        fillRect(page, 0, 0, PAGE_WIDTH, 4, LIME);

        text(page, MARGIN, 44, true, 21, WHITE, "Energy usage report");

        text(page, MARGIN, 64, true, 8, LIME,
                "SMARTCITY360  |  COMPOSITE HIERARCHY");

        textRight(page, PAGE_WIDTH - MARGIN, 44, false, 8.5, 0xC0CBC2,
                "Generated " + generatedAt);

        text(page, MARGIN, 116, true, 7.5, TEXT_MUTED, "SCOPE");

        text(page, MARGIN, 136, true, 15, TEXT_DARK,
                fit(scope, true, 15, CONTENT_WIDTH - 110));

        textRight(page, PAGE_WIDTH - MARGIN, 136, false, 9, TEXT_MUTED,
                report.getCode());

        return page;
    }

    private static Page startContinuationPage(String scope) {

        Page page = new Page();

        fillRect(page, 0, 0, PAGE_WIDTH, 40, GRAPHITE);
        fillRect(page, 0, 0, PAGE_WIDTH, 3, LIME);

        text(page, MARGIN, 26, true, 11, WHITE, "Energy usage report");

        textRight(page, PAGE_WIDTH - MARGIN, 26, false, 8.5, 0xC0CBC2,
                fit(scope, false, 8.5, 300));

        return page;
    }

    private static double drawSummary(Page page, UsageReport report) {

        double top = 156;
        double gap = 10;
        double cardWidth = (CONTENT_WIDTH - 3 * gap) / 4;
        double cardHeight = 56;

        String[] labels = {
                "TOTAL USAGE (KWH)",
                "BUILDINGS",
                "POWERED ON",
                "POWER STATUS"
        };

        String[] values = {
                formatKwh(report.getUsageKwh()),
                String.valueOf(report.getTotalBuildings()),
                report.getPoweredBuildings() + " of "
                        + report.getTotalBuildings(),
                report.getPowerStatus().name()
        };

        for (int i = 0; i < 4; i++) {

            double x = MARGIN + i * (cardWidth + gap);

            fillRect(page, x, top, cardWidth, cardHeight, PANEL);

            text(page, x + 12, top + 20, true, 7, TEXT_MUTED, labels[i]);

            int color = i == 3
                    ? statusColor(report.getPowerStatus())
                    : TEXT_DARK;

            text(page, x + 12, top + 42, true, 14, color,
                    fit(values[i], true, 14, cardWidth - 20));
        }

        return top + cardHeight + 28;
    }

    private static double drawTableHeader(Page page, double y) {

        text(page, MARGIN, y - 8, true, 10, TEXT_DARK,
                "Breakdown by component");

        double top = y;

        fillRect(page, MARGIN, top, CONTENT_WIDTH, TABLE_HEADER_HEIGHT,
                GRAPHITE_LIGHT);

        double base = top + 13.5;

        text(page, COL_COMPONENT, base, true, 7, WHITE, "COMPONENT");
        text(page, COL_TYPE, base, true, 7, WHITE, "TYPE");
        text(page, COL_CODE, base, true, 7, WHITE, "CODE");
        textRight(page, COL_BUILDINGS_RIGHT, base, true, 7, WHITE,
                "BUILDINGS");
        textRight(page, COL_POWERED_RIGHT, base, true, 7, WHITE, "POWERED");
        text(page, COL_STATUS, base, true, 7, WHITE, "STATUS");
        textRight(page, COL_USAGE_RIGHT, base, true, 7, WHITE,
                "USAGE (KWH)");

        return top + TABLE_HEADER_HEIGHT;
    }

    private static void drawRow(
            Page page,
            double top,
            RowItem row,
            int rowIndex
    ) {

        UsageReport item = row.report;

        if (rowIndex % 2 == 1) {
            fillRect(page, MARGIN, top, CONTENT_WIDTH, ROW_HEIGHT, ZEBRA);
        }

        boolean group = !"Building".equals(item.getType());

        double base = top + 12.5;

        double indent = Math.min(row.level, 5) * 9;

        double nameWidth = COL_TYPE - COL_COMPONENT - indent - 8;

        text(page, COL_COMPONENT + indent, base, group, 8.5, TEXT_DARK,
                fit(item.getName(), group, 8.5, nameWidth));

        text(page, COL_TYPE, base, false, 8, TEXT_MUTED,
                fit(item.getType(), false, 8, COL_CODE - COL_TYPE - 6));

        text(page, COL_CODE, base, false, 8, TEXT_MUTED,
                fit(item.getCode(), false, 8,
                        COL_BUILDINGS_RIGHT - COL_CODE - 50));

        textRight(page, COL_BUILDINGS_RIGHT, base, false, 8.5, TEXT_DARK,
                String.valueOf(item.getTotalBuildings()));

        textRight(page, COL_POWERED_RIGHT, base, false, 8.5, TEXT_DARK,
                String.valueOf(item.getPoweredBuildings()));

        text(page, COL_STATUS, base, true, 7.5,
                statusColor(item.getPowerStatus()),
                item.getPowerStatus().name());

        textRight(page, COL_USAGE_RIGHT, base, group, 8.5, TEXT_DARK,
                formatKwh(item.getUsageKwh()));

        strokeLine(page, MARGIN, top + ROW_HEIGHT,
                MARGIN + CONTENT_WIDTH, top + ROW_HEIGHT, LINE);
    }

    private static void drawNote(Page page, double y) {

        text(page, MARGIN, y, false, 7.5, TEXT_MUTED,
                "Usage is the sum of successful meter readings, "
                        + "converted to kWh.");

        text(page, MARGIN, y + 11, false, 7.5, TEXT_MUTED,
                "Power status is calculated from the buildings "
                        + "below each component.");
    }

    private static void drawFooter(
            Page page,
            int number,
            int total,
            String generatedAt
    ) {

        strokeLine(page, MARGIN, PAGE_HEIGHT - 40,
                PAGE_WIDTH - MARGIN, PAGE_HEIGHT - 40, LINE);

        text(page, MARGIN, PAGE_HEIGHT - 26, false, 7.5, TEXT_MUTED,
                "SmartCity360  |  Generated " + generatedAt);

        textRight(page, PAGE_WIDTH - MARGIN, PAGE_HEIGHT - 26, false, 7.5,
                TEXT_MUTED, "Page " + number + " of " + total);
    }

    private static final class RowItem {

        private final UsageReport report;
        private final int level;

        private RowItem(UsageReport report, int level) {
            this.report = report;
            this.level = level;
        }
    }

    private static List<RowItem> flatten(UsageReport report, int level) {

        List<RowItem> rows = new ArrayList<>();

        rows.add(new RowItem(report, level));

        for (UsageReport child : report.getChildren()) {
            rows.addAll(flatten(child, level + 1));
        }

        return rows;
    }

    private static int statusColor(PowerStatus status) {

        switch (status) {
            case ACTIVE:
                return GREEN;
            case PARTIAL:
                return AMBER;
            default:
                return RED;
        }
    }

    private static String formatKwh(double value) {

        return String.format(Locale.US, "%,.2f", value);
    }

    private static String clean(String value) {

        if (value == null) {
            return "";
        }

        StringBuilder result = new StringBuilder(value.length());

        for (int i = 0; i < value.length(); i++) {

            char c = value.charAt(i);

            if ((c >= 32 && c <= 126) || (c >= 160 && c <= 255)) {
                result.append(c);
            } else if (c == '\t' || c == '\n' || c == '\r') {
                result.append(' ');
            } else {
                result.append('?');
            }
        }

        return result.toString();
    }

    private static double width(String value, boolean bold, double size) {

        int[] table = bold ? WIDTH_BOLD : WIDTH_REGULAR;

        double total = 0;

        for (int i = 0; i < value.length(); i++) {

            char c = value.charAt(i);

            total += (c >= 32 && c <= 126) ? table[c - 32] : 556;
        }

        return total * size / 1000.0;
    }

    private static String fit(
            String value,
            boolean bold,
            double size,
            double maxWidth
    ) {

        String text = clean(value);

        if (width(text, bold, size) <= maxWidth) {
            return text;
        }

        String ellipsis = "..";

        while (!text.isEmpty()
                && width(text + ellipsis, bold, size) > maxWidth) {

            text = text.substring(0, text.length() - 1);
        }

        return text + ellipsis;
    }

    private static final class Page {

        private final StringBuilder content = new StringBuilder();
    }

    private static void fillRect(
            Page page,
            double x,
            double top,
            double w,
            double h,
            int rgb
    ) {

        page.content.append(color(rgb)).append(" rg ")
                .append(num(x)).append(' ')
                .append(num(PAGE_HEIGHT - top - h)).append(' ')
                .append(num(w)).append(' ')
                .append(num(h)).append(" re f\n");
    }

    private static void strokeLine(
            Page page,
            double x1,
            double top1,
            double x2,
            double top2,
            int rgb
    ) {

        page.content.append(color(rgb)).append(" RG 0.5 w ")
                .append(num(x1)).append(' ')
                .append(num(PAGE_HEIGHT - top1)).append(" m ")
                .append(num(x2)).append(' ')
                .append(num(PAGE_HEIGHT - top2)).append(" l S\n");
    }

    private static void text(
            Page page,
            double x,
            double baselineTop,
            boolean bold,
            double size,
            int rgb,
            String value
    ) {

        String safe = escape(clean(value));

        page.content.append("BT ")
                .append(bold ? "/F2 " : "/F1 ")
                .append(num(size)).append(" Tf ")
                .append(color(rgb)).append(" rg ")
                .append(num(x)).append(' ')
                .append(num(PAGE_HEIGHT - baselineTop)).append(" Td (")
                .append(safe).append(") Tj ET\n");
    }

    private static void textRight(
            Page page,
            double rightEdge,
            double baselineTop,
            boolean bold,
            double size,
            int rgb,
            String value
    ) {

        String safe = clean(value);

        text(page, rightEdge - width(safe, bold, size), baselineTop,
                bold, size, rgb, safe);
    }

    private static String escape(String value) {

        return value.replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    private static String color(int rgb) {

        return num(((rgb >> 16) & 0xFF) / 255.0) + " "
                + num(((rgb >> 8) & 0xFF) / 255.0) + " "
                + num((rgb & 0xFF) / 255.0);
    }

    private static String num(double value) {

        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static byte[] serialize(List<Page> pages, String title) {

        List<byte[]> objects = new ArrayList<>();

        StringBuilder kids = new StringBuilder();

        for (int i = 0; i < pages.size(); i++) {
            kids.append(6 + 2 * i).append(" 0 R ");
        }

        objects.add(bytes("<< /Type /Catalog /Pages 2 0 R >>"));

        objects.add(bytes("<< /Type /Pages /Kids [ " + kids + "] /Count "
                + pages.size() + " >>"));

        objects.add(bytes("<< /Type /Font /Subtype /Type1 "
                + "/BaseFont /Helvetica /Encoding /WinAnsiEncoding >>"));

        objects.add(bytes("<< /Type /Font /Subtype /Type1 "
                + "/BaseFont /Helvetica-Bold "
                + "/Encoding /WinAnsiEncoding >>"));

        objects.add(bytes("<< /Title (" + escape(clean(title))
                + ") /Producer (SmartCity360) >>"));

        for (int i = 0; i < pages.size(); i++) {

            int contentObject = 7 + 2 * i;

            objects.add(bytes("<< /Type /Page /Parent 2 0 R "
                    + "/MediaBox [0 0 " + PAGE_WIDTH + " " + PAGE_HEIGHT
                    + "] /Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> "
                    + "/Contents " + contentObject + " 0 R >>"));

            byte[] stream = bytes(pages.get(i).content.toString());

            ByteArrayOutputStream streamObject =
                    new ByteArrayOutputStream();

            writeBytes(streamObject,
                    "<< /Length " + stream.length + " >>\nstream\n");
            streamObject.write(stream, 0, stream.length);
            writeBytes(streamObject, "\nendstream");

            objects.add(streamObject.toByteArray());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        writeBytes(out, "%PDF-1.4\n");

        int[] offsets = new int[objects.size() + 1];

        for (int i = 0; i < objects.size(); i++) {

            offsets[i + 1] = out.size();

            writeBytes(out, (i + 1) + " 0 obj\n");
            out.write(objects.get(i), 0, objects.get(i).length);
            writeBytes(out, "\nendobj\n");
        }

        int xrefStart = out.size();

        writeBytes(out, "xref\n0 " + (objects.size() + 1) + "\n");
        writeBytes(out, "0000000000 65535 f \n");

        for (int i = 1; i <= objects.size(); i++) {
            writeBytes(out, String.format(Locale.ROOT,
                    "%010d 00000 n \n", offsets[i]));
        }

        writeBytes(out, "trailer\n<< /Size " + (objects.size() + 1)
                + " /Root 1 0 R /Info 5 0 R >>\n");

        writeBytes(out, "startxref\n" + xrefStart + "\n%%EOF\n");

        return out.toByteArray();
    }

    private static byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.ISO_8859_1);
    }

    private static void writeBytes(ByteArrayOutputStream out, String value) {
        byte[] data = bytes(value);
        out.write(data, 0, data.length);
    }
}
