package team2.stk.application.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.infrastructure.excel.ExcelExporter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class DownloadLedgerExcelUseCase {

    private final GetLedgerUseCase getLedgerUseCase;
    private final ExcelExporter excelExporter;

    @Transactional(readOnly = true)
    public DownloadMovementExcelUseCase.ExcelDownloadResult execute(LocalDate startDate, LocalDate endDate) {
        List<GetLedgerUseCase.LedgerResult> results = getLedgerUseCase.execute(startDate, endDate);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String period = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                       "_" + endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileName = String.format("수불_현황_%s_%s.xlsx", period, today);

        List<String> headers = Arrays.asList(
                "자재코드", "자재명", "자재위치", "기초재고", "입고수량", "출고수량", "기말재고"
        );

        List<Function<GetLedgerUseCase.LedgerResult, Object>> columns = Arrays.asList(
                GetLedgerUseCase.LedgerResult::itemCode,
                GetLedgerUseCase.LedgerResult::itemName,
                result -> result.location() != null ? result.location() : "",
                GetLedgerUseCase.LedgerResult::openingStock,
                GetLedgerUseCase.LedgerResult::inboundQty,
                GetLedgerUseCase.LedgerResult::outboundQty,
                GetLedgerUseCase.LedgerResult::closingStock
        );

        ByteArrayResource resource = excelExporter.export(headers, results, columns);
        return new DownloadMovementExcelUseCase.ExcelDownloadResult(fileName, resource);
    }
}