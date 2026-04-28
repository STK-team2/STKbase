package team2.stk.application.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team2.stk.domain.item.Item;
import team2.stk.domain.movement.StockMovement;
import team2.stk.domain.user.User;
import team2.stk.infrastructure.excel.ExcelImporter;
import team2.stk.infrastructure.persistence.item.ItemRepository;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;
import team2.stk.infrastructure.persistence.user.UserRepository;
import team2.stk.shared.util.SecurityUtil;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportExcelUseCase {

    private final ExcelImporter excelImporter;
    private final ItemRepository itemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;

    @Transactional
    public ImportResult execute(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("xlsx 파일만 업로드 가능합니다.");
        }

        UUID currentUserId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        log.info("엑셀 파일 업로드 시작: {} (사용자: {})", filename, user.getName());

        // 엑셀 파일 파싱
        ExcelImporter.ImportResult parseResult;
        try {
            parseResult = excelImporter.parse(file.getInputStream(), filename);
        } catch (Exception e) {
            log.error("엑셀 파일 파싱 실패: {}", filename, e);
            throw new RuntimeException("엑셀 파일 파싱에 실패했습니다: " + e.getMessage());
        }

        if (!parseResult.errors().isEmpty()) {
            log.warn("엑셀 파싱 중 오류 발생: {}개", parseResult.errors().size());
            return new ImportResult(0, 0, parseResult.errors(), "파일 파싱 중 오류가 발생했습니다.");
        }

        // 데이터 검증 및 저장
        List<String> processingErrors = new ArrayList<>();
        Map<String, Item> itemMap = new HashMap<>();

        try {
            // 1. 자재 등록/조회
            int processedItemCount = processItems(parseResult.items(), itemMap, processingErrors);

            // 2. 입출고 등록
            int processedMovementCount = processMovements(parseResult.movements(), itemMap, user, processingErrors);

            if (!processingErrors.isEmpty()) {
                log.warn("데이터 처리 중 오류 발생: {}개", processingErrors.size());
                // @Transactional에 의해 자동 롤백됨
                throw new RuntimeException("데이터 처리 중 오류가 발생했습니다.");
            }

            log.info("엑셀 업로드 완료 - 자재: {}개, 입출고: {}개", processedItemCount, processedMovementCount);

            return new ImportResult(
                processedItemCount,
                processedMovementCount,
                Collections.emptyList(),
                "업로드가 성공적으로 완료되었습니다."
            );

        } catch (Exception e) {
            log.error("데이터 처리 중 오류 발생", e);
            if (!processingErrors.isEmpty()) {
                return new ImportResult(0, 0, processingErrors, "데이터 처리 중 오류가 발생했습니다.");
            } else {
                return new ImportResult(0, 0,
                    List.of("처리 중 예상치 못한 오류가 발생했습니다: " + e.getMessage()),
                    "업로드에 실패했습니다."
                );
            }
        }
    }

    private int processItems(List<ExcelImporter.ItemRow> itemRows, Map<String, Item> itemMap, List<String> errors) {
        Set<String> processedItemCodes = new HashSet<>();
        int count = 0;

        for (ExcelImporter.ItemRow itemRow : itemRows) {
            try {
                String itemCode = itemRow.itemCode();

                // 중복 처리 방지
                if (processedItemCodes.contains(itemCode)) {
                    continue;
                }

                // 기존 자재 확인
                Optional<Item> existingItem = itemRepository.findByItemCodeActive(itemCode);
                if (existingItem.isPresent()) {
                    itemMap.put(itemCode, existingItem.get());
                    processedItemCodes.add(itemCode);
                    continue;
                }

                // 신규 자재 등록
                if (itemRow.itemName().isBlank()) {
                    errors.add(String.format("시트 '%s', 행 %d: 자재명이 비어있습니다. (자재코드: %s)",
                              itemRow.sheetName(), itemRow.rowNumber(), itemCode));
                    continue;
                }

                if (itemRow.location().isBlank()) {
                    errors.add(String.format("시트 '%s', 행 %d: 위치가 비어있습니다. (자재코드: %s)",
                              itemRow.sheetName(), itemRow.rowNumber(), itemCode));
                    continue;
                }

                Item newItem = new Item(itemCode, itemRow.itemName(), itemRow.boxNumber(), itemRow.location(), null, null);
                Item savedItem = itemRepository.save(newItem);
                itemMap.put(itemCode, savedItem);
                processedItemCodes.add(itemCode);
                count++;

                log.debug("신규 자재 등록: {} ({})", itemCode, itemRow.itemName());

            } catch (Exception e) {
                errors.add(String.format("시트 '%s', 행 %d: 자재 처리 중 오류 - %s (자재코드: %s)",
                          itemRow.sheetName(), itemRow.rowNumber(), e.getMessage(), itemRow.itemCode()));
            }
        }

        return count;
    }

    private int processMovements(List<ExcelImporter.MovementRow> movementRows, Map<String, Item> itemMap,
                               User user, List<String> errors) {
        int count = 0;

        for (ExcelImporter.MovementRow movementRow : movementRows) {
            try {
                String itemCode = movementRow.itemCode();
                Item item = itemMap.get(itemCode);

                if (item == null) {
                    errors.add(String.format("시트 '%s', 행 %d: 자재를 찾을 수 없습니다. (자재코드: %s)",
                              movementRow.sheetName(), movementRow.rowNumber(), itemCode));
                    continue;
                }

                if (movementRow.quantity() <= 0) {
                    errors.add(String.format("시트 '%s', 행 %d: 수량이 올바르지 않습니다. (수량: %d)",
                              movementRow.sheetName(), movementRow.rowNumber(), movementRow.quantity()));
                    continue;
                }

                StockMovement movement = new StockMovement(
                    item, user, "DEFAULT", movementRow.type(), movementRow.quantity(),
                    movementRow.movementDate(), movementRow.reference(), movementRow.note()
                );

                stockMovementRepository.save(movement);
                count++;

                log.debug("입출고 등록: {} {} {}개 ({})",
                         itemCode, movementRow.type(), movementRow.quantity(), movementRow.movementDate());

            } catch (Exception e) {
                errors.add(String.format("시트 '%s', 행 %d: 입출고 처리 중 오류 - %s (자재코드: %s)",
                          movementRow.sheetName(), movementRow.rowNumber(), e.getMessage(), movementRow.itemCode()));
            }
        }

        return count;
    }

    public record ImportResult(
        int processedItemCount,
        int processedMovementCount,
        List<String> errors,
        String message
    ) {}
}