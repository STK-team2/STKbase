package team2.stk.infrastructure.excel;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Component
public class ExcelExporter {

    private final ExcelService excelService;

    public <T> ByteArrayResource export(
            List<String> headers,
            List<T> rows,
            List<Function<T, Object>> columns
    ) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();

        CellStyle headerStyle = excelService.createHeaderStyle(wb);
        CellStyle defaultStyle = excelService.createDefaultStyle(wb);
        CellStyle rightStyle   = excelService.createRightStyle(wb);
        CellStyle dateStyle    = excelService.createDateStyle(wb);

        // 헤더 행
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            createCell(headerRow, i, headers.get(i), headerStyle);
        }

        // 데이터 행
        for (int i = 0; i < rows.size(); i++) {
            Row row = sheet.createRow(i + 1);
            T item = rows.get(i);
            for (int j = 0; j < columns.size(); j++) {
                Object value = columns.get(j).apply(item);
                CellStyle style = (value instanceof Number) ? rightStyle
                        : (value instanceof LocalDate) ? dateStyle : defaultStyle;
                createCell(row, j, value, style);
            }
        }

        // 컬럼 너비 자동 조정
        for (int i = 0; i < headers.size(); i++) {
            sheet.autoSizeColumn(i);
            // 최대/최소 너비 제한
            int width = sheet.getColumnWidth(i);
            if (width > 15000) {
                sheet.setColumnWidth(i, 15000); // 최대 너비 제한
            } else if (width < 3000) {
                sheet.setColumnWidth(i, 3000);  // 최소 너비 보장
            }
        }

        return excelService.toResource(wb);
    }

    private void createCell(Row row, int idx, Object value, CellStyle style) {
        Cell cell = row.createCell(idx);
        if (value instanceof Integer)       cell.setCellValue((Integer) value);
        else if (value instanceof Long)     cell.setCellValue((Long) value);
        else if (value instanceof Double)   cell.setCellValue((Double) value);
        else if (value instanceof LocalDate) cell.setCellValue((LocalDate) value);
        else if (value instanceof Boolean)  cell.setCellValue((Boolean) value);
        else cell.setCellValue(value != null ? value.toString() : "");
        cell.setCellStyle(style);
    }
}