package team2.stk.presentation.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.stk.shared.response.ApiResponse;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("서버가 정상 작동 중입니다");
    }

    @GetMapping("/simple")
    public String simple() {
        return "Simple test endpoint working!";
    }
}