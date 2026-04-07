package team2.stk.presentation.excel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team2.stk.application.excel.ImportExcelUseCase;
import team2.stk.presentation.excel.dto.ImportExcelResponse;
import team2.stk.shared.response.ApiResponse;

@Slf4j
@Tag(name = "엑셀", description = "엑셀 파일 처리 API")
@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ExcelController {

    private final ImportExcelUseCase importExcelUseCase;

    @Operation(
        summary = "엑셀 업로드 (데이터 이전)",
        description = """
            기존 재고 관리 엑셀 파일을 업로드하여 데이터를 시스템으로 이전합니다.

            **지원 파일 형식:**
            - 파일명: `YY.MM.DD 재고 확인.xlsx` (예: `25.12.12 재고 확인.xlsx`)
            - 시트: `12월01일-12월05일` 형태의 주간 시트

            **파일 구조:**
            - A열: BOX번호, B열: 자재위치, C열: 자재코드, D열: 자재명
            - E열: 수량(기초재고)
            - F열~: `12월 01일`, `12월 02일` 등 날짜별 출고수량
            - 끝에서 두번째 열: 입고수량 (주간 합계)
            - 마지막 열: 총수량 (계산값, 무시됨)

            **처리 방식:**
            - 신규 자재 자동 등록
            - 날짜별 출고 이력 생성 (OUTBOUND)
            - 주간 입고 이력 생성 (INBOUND, 해당 주 마지막 날짜)
            - 전체 트랜잭션 처리 (오류 발생 시 전체 롤백)
            """
    )
    @PostMapping("/import")
    public ResponseEntity<ApiResponse<ImportExcelResponse>> importExcel(
            @RequestParam("file") MultipartFile file) throws java.io.IOException {

        log.info("엑셀 파일 업로드 요청: {}", file.getOriginalFilename());

        ImportExcelUseCase.ImportResult result = importExcelUseCase.execute(file);
        ImportExcelResponse response = ImportExcelResponse.from(result);

        log.info("엑셀 업로드 완료: 자재 {}개, 입출고 {}개 처리",
                response.getProcessedItemCount(), response.getProcessedMovementCount());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}