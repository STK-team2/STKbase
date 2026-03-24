package team2.stk.infrastructure.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import team2.stk.domain.movement.MovementType;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ExcelImporter {

    // 시트명 패턴: "12월01일-12월05일"
    private static final Pattern SHEET_NAME_PATTERN =
        Pattern.compile("(\\d+)월(\\d+)일-(\\d+)월(\\d+)일");

    // 컬럼 헤더 패턴: "12월 01일" (공백 있을 수도 없을 수도)
    private static final Pattern DATE_COLUMN_PATTERN =
        Pattern.compile("(\\d+)월\\s*(\\d+)일");

    // 파일명에서 연도 파싱: "25.12.12 재고 확인" → 2025
    public int parseYearFromFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return LocalDate.now().getYear(); // 기본값으로 현재 연도 사용
        }

        try {
            String[] parts = filename.split("\\.");
            if (parts.length > 0) {
                String yearPart = parts[0].trim();
                // 숫자만 추출
                String numbers = yearPart.replaceAll("\\D", "");
                if (numbers.length() >= 2) {
                    int shortYear = Integer.parseInt(numbers.substring(0, 2));
                    return (shortYear < 100) ? 2000 + shortYear : shortYear;
                }
            }
        } catch (Exception e) {
            log.warn("파일명에서 연도 파싱 실패: {}, 기본값 사용", filename);
        }
        return LocalDate.now().getYear(); // 파싱 실패 시 현재 연도 사용
    }

    public ImportResult parse(InputStream inputStream, String filename) throws IOException {
        int year = parseYearFromFilename(filename);
        List<MovementRow> movements = new ArrayList<>();
        List<ItemRow> items = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (Workbook wb = new XSSFWorkbook(inputStream)) {
            for (int s = 0; s < wb.getNumberOfSheets(); s++) {
                Sheet sheet = wb.getSheetAt(s);
                String sheetName = sheet.getSheetName();

                log.debug("시트 처리 중: {}", sheetName);

                // 주간 시트 여부 확인 ("n월n일-n월n일" 패턴)
                Matcher sheetMatcher = SHEET_NAME_PATTERN.matcher(sheetName);
                if (!sheetMatcher.find()) {
                    log.debug("스킵된 시트: {} (패턴 불일치)", sheetName);
                    continue; // warehouse layout, Sheet1 등 스킵
                }

                try {
                    parseWeeklySheet(sheet, year, sheetMatcher, movements, items, errors);
                } catch (Exception e) {
                    String error = String.format("시트 '%s' 처리 중 오류: %s", sheetName, e.getMessage());
                    errors.add(error);
                    log.error(error, e);
                }
            }
        }

        log.info("엑셀 파싱 완료 - 자재: {}개, 입출고: {}개, 오류: {}개",
                items.size(), movements.size(), errors.size());

        return new ImportResult(items, movements, errors);
    }

    private void parseWeeklySheet(Sheet sheet, int year, Matcher sheetMatcher,
                                 List<MovementRow> movements, List<ItemRow> items, List<String> errors) {

        // 해당 주의 마지막 날짜 추출 (입고수량 날짜로 사용)
        int endMonth = Integer.parseInt(sheetMatcher.group(3));
        int endDay = Integer.parseInt(sheetMatcher.group(4));
        LocalDate weekEndDate = LocalDate.of(year, endMonth, endDay);

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            errors.add(String.format("시트 '%s': 헤더 행이 없습니다.", sheet.getSheetName()));
            return;
        }

        // 날짜 컬럼 감지 (OUTBOUND)
        Map<Integer, LocalDate> dateColumns = new LinkedHashMap<>();
        int inboundColIdx = -1;
        int lastColIdx = headerRow.getLastCellNum() - 1;

        for (int c = 0; c <= lastColIdx; c++) {
            String header = getCellString(headerRow, c);
            Matcher m = DATE_COLUMN_PATTERN.matcher(header);
            if (m.find()) {
                try {
                    int month = Integer.parseInt(m.group(1));
                    int day = Integer.parseInt(m.group(2));
                    dateColumns.put(c, LocalDate.of(year, month, day));
                } catch (Exception e) {
                    errors.add(String.format("시트 '%s': 날짜 컬럼 파싱 오류 - %s",
                              sheet.getSheetName(), header));
                }
            } else if (header.contains("입고수량") || header.contains("입고")) {
                inboundColIdx = c;
            }
        }

        log.debug("시트 '{}': 날짜 컬럼 {}개, 입고 컬럼: {}",
                 sheet.getSheetName(), dateColumns.size(), inboundColIdx);

        // 데이터 행 파싱
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                parseDataRow(row, i, dateColumns, inboundColIdx, weekEndDate,
                           movements, items, sheet.getSheetName(), errors);
            } catch (Exception e) {
                errors.add(String.format("시트 '%s', 행 %d: %s",
                          sheet.getSheetName(), i + 1, e.getMessage()));
            }
        }
    }

    private void parseDataRow(Row row, int rowNum, Map<Integer, LocalDate> dateColumns,
                             int inboundColIdx, LocalDate weekEndDate,
                             List<MovementRow> movements, List<ItemRow> items,
                             String sheetName, List<String> errors) {

        String boxNumber = getCellString(row, 0);   // A열: BOX번호
        String location = getCellString(row, 1);    // B열: 자재위치
        String itemCode = getCellString(row, 2);    // C열: 자재코드
        String itemName = getCellString(row, 3);    // D열: 자재명

        if (itemCode.isBlank()) return; // 빈 행 스킵

        // 자재 정보 추가 (중복 체크는 UseCase에서 처리)
        items.add(new ItemRow(itemCode, itemName, boxNumber, location,
                             sheetName, rowNum + 1));

        // 날짜별 출고수량 → OUTBOUND 레코드
        for (Map.Entry<Integer, LocalDate> entry : dateColumns.entrySet()) {
            int qty = getCellInt(row, entry.getKey());
            if (qty > 0) {
                movements.add(new MovementRow(
                    itemCode, itemName, qty, entry.getValue(),
                    null, null, MovementType.OUTBOUND,
                    sheetName, rowNum + 1
                ));
            }
        }

        // 입고수량 → INBOUND 레코드 (주 마지막 날짜로 기록)
        if (inboundColIdx >= 0) {
            int inboundQty = getCellInt(row, inboundColIdx);
            if (inboundQty > 0) {
                movements.add(new MovementRow(
                    itemCode, itemName, inboundQty, weekEndDate,
                    null, null, MovementType.INBOUND,
                    sheetName, rowNum + 1
                ));
            }
        }
    }

    // 셀 타입별 안전한 읽기 (타입 불일치 예외 방지)
    private String getCellString(Row row, int idx) {
        Cell cell = row.getCell(idx);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BLANK   -> "";
            default      -> "";
        };
    }

    private int getCellInt(Row row, int idx) {
        Cell cell = row.getCell(idx);
        if (cell == null) return 0;
        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING  -> {
                try {
                    String value = cell.getStringCellValue().trim();
                    yield value.isEmpty() ? 0 : Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    yield 0;
                }
            }
            default -> 0;
        };
    }

    public record MovementRow(
        String itemCode, String itemName, int quantity,
        LocalDate movementDate, String reference, String note,
        MovementType type, String sheetName, int rowNumber
    ) {}

    public record ItemRow(
        String itemCode, String itemName, String boxNumber, String location,
        String sheetName, int rowNumber
    ) {}

    public record ImportResult(
        List<ItemRow> items,
        List<MovementRow> movements,
        List<String> errors
    ) {}
}