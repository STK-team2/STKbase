package team2.stk.presentation.excel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team2.stk.application.excel.ImportExcelUseCase;

import java.util.List;

@Getter
@AllArgsConstructor
public class ImportExcelResponse {
    private final int processedItemCount;
    private final int processedMovementCount;
    private final List<String> errors;
    private final String message;
    private final boolean success;

    public static ImportExcelResponse from(ImportExcelUseCase.ImportResult result) {
        return new ImportExcelResponse(
                result.processedItemCount(),
                result.processedMovementCount(),
                result.errors(),
                result.message(),
                result.errors().isEmpty()
        );
    }
}