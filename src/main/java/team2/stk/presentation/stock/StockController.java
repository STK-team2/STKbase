package team2.stk.presentation.stock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team2.stk.application.stock.GetCurrentStockUseCase;
import team2.stk.application.stock.GetLedgerUseCase;
import team2.stk.application.stock.DownloadCurrentStockExcelUseCase;
import team2.stk.application.stock.DownloadLedgerExcelUseCase;
import team2.stk.application.stock.DownloadMovementExcelUseCase;
import team2.stk.presentation.stock.dto.CurrentStockResponse;
import team2.stk.presentation.stock.dto.LedgerResponse;
import team2.stk.shared.response.ApiResponse;
import team2.stk.shared.util.ExcelResponseHelper;
import org.springframework.core.io.ByteArrayResource;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "재고조회", description = "재고 조회 API")
@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class StockController {

    private final GetCurrentStockUseCase getCurrentStockUseCase;
    private final GetLedgerUseCase getLedgerUseCase;
    private final DownloadCurrentStockExcelUseCase downloadCurrentStockExcelUseCase;
    private final DownloadLedgerExcelUseCase downloadLedgerExcelUseCase;

    @Operation(summary = "현재 재고 조회", description = "모든 자재의 현재 재고를 실시간으로 조회합니다.")
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<List<CurrentStockResponse>>> getCurrentStock() {
        List<GetCurrentStockUseCase.CurrentStockResult> results = getCurrentStockUseCase.execute();
        List<CurrentStockResponse> responses = results.stream()
                .map(CurrentStockResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "수불 조회", description = "지정된 기간의 수불 내역을 조회합니다. (기초재고/입고/출고/기말재고)")
    @GetMapping("/ledger")
    public ResponseEntity<ApiResponse<List<LedgerResponse>>> getLedger(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<GetLedgerUseCase.LedgerResult> results = getLedgerUseCase.execute(from, to);
        List<LedgerResponse> responses = results.stream()
                .map(LedgerResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "현재 재고 엑셀 다운로드", description = "현재 재고 현황을 엑셀 파일로 다운로드합니다.")
    @GetMapping("/current/download")
    public ResponseEntity<ByteArrayResource> downloadCurrentStock() {
        DownloadMovementExcelUseCase.ExcelDownloadResult result =
                downloadCurrentStockExcelUseCase.execute();

        return ExcelResponseHelper.buildResponse(result.fileName(), result.resource());
    }

    @Operation(summary = "수불 엑셀 다운로드", description = "수불 현황을 엑셀 파일로 다운로드합니다.")
    @GetMapping("/ledger/download")
    public ResponseEntity<ByteArrayResource> downloadLedger(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        DownloadMovementExcelUseCase.ExcelDownloadResult result =
                downloadLedgerExcelUseCase.execute(from, to);

        return ExcelResponseHelper.buildResponse(result.fileName(), result.resource());
    }
}