package team2.stk.presentation.image;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team2.stk.domain.item.Item;
import team2.stk.infrastructure.persistence.item.ItemRepository;
import team2.stk.shared.response.ApiResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Tag(name = "이미지", description = "자재 이미지 업로드 API")
@RestController
@RequestMapping("/items/{id}/image")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ImageController {

    @Value("${app.image.storage-path:/app/images}")
    private String storagePath;

    @Value("${app.image.base-url}")
    private String baseUrl;

    private final ItemRepository itemRepository;

    @Operation(summary = "자재 이미지 업로드")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) throws IOException {

        Item item = itemRepository.findByIdActive(id)
                .orElseThrow(() -> new IllegalArgumentException("자재를 찾을 수 없습니다."));

        String ext = getExtension(file.getOriginalFilename());
        String filename = id + "." + ext;

        Path dir = Paths.get(storagePath);
        Files.createDirectories(dir);
        Files.write(dir.resolve(filename), file.getBytes());

        String imageUrl = baseUrl.stripTrailing() + "/images/" + filename;
        item.updateImage(imageUrl);
        itemRepository.save(item);

        return ResponseEntity.ok(ApiResponse.success(Map.of("imageUrl", imageUrl)));
    }

    @Operation(summary = "자재 이미지 삭제")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable UUID id) throws IOException {
        Item item = itemRepository.findByIdActive(id)
                .orElseThrow(() -> new IllegalArgumentException("자재를 찾을 수 없습니다."));

        if (item.getImageUrl() != null) {
            String filename = Paths.get(item.getImageUrl()).getFileName().toString();
            Path file = Paths.get(storagePath, filename);
            Files.deleteIfExists(file);
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
