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
public class DownloadCurrentStockExcelUseCase {

    private final GetCurrentStockUseCase getCurrentStockUseCase;
    private final ExcelExporter excelExporter;

    @Transactional(readOnly = true)
    public DownloadMovementExcelUseCase.ExcelDownloadResult execute() {
        List<GetCurrentStockUseCase.CurrentStockResult> results = getCurrentStockUseCase.execute();

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileName = String.format("재고_현황_%s.xlsx", today);

        List<String> headers = Arrays.asList(
                "자재코드", "자재명", "재고수량", "자재위치"
        );

        List<Function<GetCurrentStockUseCase.CurrentStockResult, Object>> columns = Arrays.asList(
                GetCurrentStockUseCase.CurrentStockResult::itemCode,
                GetCurrentStockUseCase.CurrentStockResult::itemName,
                GetCurrentStockUseCase.CurrentStockResult::currentStock,
                result -> result.location() != null ? result.location() : ""
        );

        ByteArrayResource resource = excelExporter.export(headers, results, columns);
        return new DownloadMovementExcelUseCase.ExcelDownloadResult(fileName, resource);
    }
}