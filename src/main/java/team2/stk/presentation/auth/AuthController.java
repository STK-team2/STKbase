package team2.stk.presentation.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.stk.application.auth.*;
import team2.stk.presentation.auth.dto.*;
import team2.stk.shared.response.ApiResponse;

@Tag(name = "인증", description = "인증 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignUpUseCase signUpUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final SignInUseCase signInUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final SignOutUseCase signOutUseCase;

    @Operation(summary = "회원가입", description = "사용자 회원가입 후 이메일 인증 코드를 발송합니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody SignUpRequest request) {
        signUpUseCase.execute(request.getEmail(), request.getName(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "이메일 인증", description = "이메일로 발송된 인증 코드를 확인합니다.")
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        verifyEmailUseCase.execute(request.getEmail(), request.getCode());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<SignInResponse>> signIn(@Valid @RequestBody SignInRequest request) {
        SignInUseCase.SignInResult result = signInUseCase.execute(request.getEmail(), request.getPassword());
        SignInResponse response = new SignInResponse(result.accessToken(), result.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새로운 Access Token을 발급받습니다.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        String accessToken = refreshTokenUseCase.execute(request.getRefreshToken());
        RefreshTokenResponse response = new RefreshTokenResponse(accessToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "로그아웃", description = "Refresh Token을 무효화하여 로그아웃합니다.")
    @PostMapping("/sign-out")
    public ResponseEntity<ApiResponse<Void>> signOut(@Valid @RequestBody RefreshTokenRequest request) {
        signOutUseCase.execute(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success());
    }
}