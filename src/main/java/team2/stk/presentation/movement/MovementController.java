package team2.stk.presentation.movement;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.stk.application.movement.*;
import team2.stk.application.stock.DownloadMovementExcelUseCase;
import team2.stk.domain.movement.MovementType;
import team2.stk.domain.movement.StockMovement;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;
import team2.stk.presentation.movement.dto.*;
import team2.stk.shared.response.ApiResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "입출고", description = "입출고 관리 API")
@RestController
@RequestMapping("/movements")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MovementController {

    private final RegisterInboundUseCase registerInboundUseCase;
    private final RegisterItemAndInboundUseCase registerItemAndInboundUseCase;
    private final RegisterOutboundUseCase registerOutboundUseCase;
    private final UpdateMovementUseCase updateMovementUseCase;
    private final DeleteMovementUseCase deleteMovementUseCase;
    private final StockMovementRepository stockMovementRepository;
    private final DownloadMovementExcelUseCase downloadMovementExcelUseCase;

    @Operation(summary = "입고 등록", description = "기존 자재에 대한 입고를 등록합니다.")
    @PostMapping("/inbound")
    public ResponseEntity<ApiResponse<MovementResponse>> registerInbound(@Valid @RequestBody RegisterInboundRequest request) {
        StockMovement movement = registerInboundUseCase.execute(
                request.getSite(),
                request.getItemId(),
                request.getQuantity(),
                request.getMovementDate(),
                request.getReference(),
                request.getNote()
        );
        MovementResponse response = MovementResponse.from(movement);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "신규 자재 입고", description = "새로운 자재를 등록하면서 동시에 입고를 등록합니다.")
    @PostMapping("/inbound/new-item")
    public ResponseEntity<ApiResponse<NewItemInboundResponse>> registerItemAndInbound(@Valid @RequestBody RegisterItemAndInboundRequest request) {
        RegisterItemAndInboundUseCase.RegisterResult result = registerItemAndInboundUseCase.execute(
                request.getItemCode(),
                request.getItemName(),
                request.getBoxNumber(),
                request.getLocation(),
                request.getSite(),
                request.getQuantity(),
                request.getMovementDate(),
                request.getReference(),
                request.getNote()
        );

        NewItemInboundResponse response = new NewItemInboundResponse(
                result.item().getId(),
                result.item().getItemCode(),
                result.item().getItemName(),
                MovementResponse.from(result.stockMovement())
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "출고 등록", description = "자재 출고를 등록합니다. 재고 부족 시 경고합니다.")
    @PostMapping("/outbound")
    public ResponseEntity<ApiResponse<MovementResponse>> registerOutbound(@Valid @RequestBody RegisterOutboundRequest request) {
        StockMovement movement = registerOutboundUseCase.execute(
                request.getSite(),
                request.getItemId(),
                request.getQuantity(),
                request.getMovementDate(),
                request.getReference(),
                request.getNote(),
                request.isAllowNegativeStock()
        );
        MovementResponse response = MovementResponse.from(movement);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "입출고 수정", description = "입출고 내역을 수정합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MovementResponse>> updateMovement(@PathVariable UUID id,
                                                                       @Valid @RequestBody UpdateMovementRequest request) {
        StockMovement movement = updateMovementUseCase.execute(
                id,
                request.getSite(),
                request.getQuantity(),
                request.getMovementDate(),
                request.getReference(),
                request.getNote()
        );
        MovementResponse response = MovementResponse.from(movement);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "입출고 삭제", description = "입출고 내역을 삭제합니다. (Soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovement(@PathVariable UUID id) {
        deleteMovementUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "입출고 내역 조회", description = "입출고 내역을 조회합니다. (검색/날짜 필터 지원)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MovementResponse>>> getMovements(
            @RequestParam(required = false) MovementType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false, defaultValue = "") String query) {

        List<StockMovement> movements = stockMovementRepository.searchMovements(type, from, to, query.trim());
        List<MovementResponse> responses = movements.stream()
                .map(MovementResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "입출고 내역 엑셀 다운로드", description = "입출고 내역을 엑셀 파일로 다운로드합니다.")
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadMovements(
            @RequestParam(required = false) MovementType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false, defaultValue = "") String query) {

        try {
            DownloadMovementExcelUseCase.ExcelDownloadResult result =
                    downloadMovementExcelUseCase.execute(type, from, to, query.trim());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", result.fileName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(result.resource());
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 생성에 실패했습니다.", e);
        }
    }

    public record NewItemInboundResponse(UUID itemId, String itemCode, String itemName, MovementResponse movement) {}
}