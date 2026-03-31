package team2.stk.shared.util;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * 엑셀 파일 다운로드 응답을 생성하는 공통 헬퍼 클래스
 */
public final class ExcelResponseHelper {

    private ExcelResponseHelper() {}

    public static ResponseEntity<ByteArrayResource> buildResponse(String fileName, ByteArrayResource resource) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
