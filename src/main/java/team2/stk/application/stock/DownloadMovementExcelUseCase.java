package team2.stk.application.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.movement.MovementType;
import team2.stk.domain.movement.StockMovement;
import team2.stk.infrastructure.excel.ExcelExporter;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class DownloadMovementExcelUseCase {

    private final StockMovementRepository stockMovementRepository;
    private final ExcelExporter excelExporter;

    @Transactional(readOnly = true)
    public ExcelDownloadResult execute(MovementType type, LocalDate startDate, LocalDate endDate, String query) {
        List<StockMovement> movements = stockMovementRepository.searchMovements(type, startDate, endDate, query);

        String typeKorean = getTypeKorean(type);
        String fileName = generateFileName(typeKorean, startDate, endDate);

        List<String> headers = Arrays.asList(
                "자재코드", "자재명", "구분", "수량", "날짜", "참고", "비고", "등록자", "등록일시"
        );

        List<Function<StockMovement, Object>> columns = Arrays.asList(
                movement -> movement.getItem().getItemCode(),
                movement -> movement.getItem().getItemName(),
                movement -> movement.getType() == MovementType.INBOUND ? "입고" : "출고",
                StockMovement::getQuantity,
                StockMovement::getMovementDate,
                movement -> movement.getReference() != null ? movement.getReference() : "",
                movement -> movement.getNote() != null ? movement.getNote() : "",
                movement -> movement.getUser().getName(),
                movement -> movement.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );

        ByteArrayResource resource = excelExporter.export(headers, movements, columns);
        return new ExcelDownloadResult(fileName, resource);
    }

    private String getTypeKorean(MovementType type) {
        return switch (type) {
            case INBOUND -> "입고";
            case OUTBOUND -> "출고";
            case RETURN_INBOUND -> "입고반품";
            case RETURN_OUTBOUND -> "출고반품";
            case EXCHANGE_OUT -> "교환출고";
            case EXCHANGE_IN -> "교환입고";
            case null -> "입출고";
        };
    }

    private String generateFileName(String typeKorean, LocalDate startDate, LocalDate endDate) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        if (startDate != null && endDate != null) {
            String period = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                           "_" + endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            return String.format("%s_내역_%s_%s.xlsx", typeKorean, period, today);
        } else {
            return String.format("%s_내역_%s.xlsx", typeKorean, today);
        }
    }

    public record ExcelDownloadResult(String fileName, ByteArrayResource resource) {}
}