package team2.stk.presentation.image;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team2.stk.domain.item.Item;
import team2.stk.infrastructure.persistence.item.ItemRepository;
import team2.stk.infrastructure.storage.S3Service;
import team2.stk.shared.response.ApiResponse;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Tag(name = "이미지", description = "자재 이미지 업로드 API")
@RestController
@RequestMapping("/items/{id}/image")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ImageController {

    private final ItemRepository itemRepository;
    private final S3Service s3Service;

    @Operation(summary = "자재 이미지 업로드")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) throws IOException {

        Item item = itemRepository.findByIdActive(id)
                .orElseThrow(() -> new IllegalArgumentException("자재를 찾을 수 없습니다."));

        String ext = getExtension(file.getOriginalFilename());
        String s3Key = "items/" + id + "." + ext;

        s3Service.upload(s3Key, file);
        item.updateImage(s3Key);
        itemRepository.save(item);

        // 업로드 직후 1시간짜리 Presigned URL 반환
        String presignedUrl = s3Service.getPresignedUrl(s3Key);
        return ResponseEntity.ok(ApiResponse.success(Map.of("imageUrl", presignedUrl)));
    }

    @Operation(summary = "자재 이미지 Presigned URL 조회", description = "1시간 유효한 이미지 접근 URL 발급")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> getImageUrl(@PathVariable UUID id) {
        Item item = itemRepository.findByIdActive(id)
                .orElseThrow(() -> new IllegalArgumentException("자재를 찾을 수 없습니다."));

        if (item.getImageUrl() == null) {
            return ResponseEntity.ok(ApiResponse.success(Map.of()));
        }

        String presignedUrl = s3Service.getPresignedUrl(item.getImageUrl());
        return ResponseEntity.ok(ApiResponse.success(Map.of("imageUrl", presignedUrl)));
    }

    @Operation(summary = "자재 이미지 삭제")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable UUID id) {
        Item item = itemRepository.findByIdActive(id)
                .orElseThrow(() -> new IllegalArgumentException("자재를 찾을 수 없습니다."));

        if (item.getImageUrl() != null) {
            s3Service.delete(item.getImageUrl());
            item.updateImage(null);
            itemRepository.save(item);
        }
        return ResponseEntity.ok(ApiResponse.success());
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
