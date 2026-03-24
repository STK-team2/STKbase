package team2.stk.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("재고관리 시스템 API")
                        .version("1.0.0")
                        .description("""
                                ## 재고관리 시스템 API 문서

                                사내 재고관리를 위한 웹 애플리케이션의 RESTful API 문서입니다.

                                ### 주요 기능
                                - **인증**: JWT 기반 사용자 인증 (이메일 인증 포함)
                                - **자재 관리**: 자재 등록, 검색, 삭제
                                - **입출고 관리**: 입고, 출고, 반품, 교환 처리
                                - **재고 조회**: 현재재고, 수불현황 실시간 조회
                                - **마감 처리**: 월말 재고 마감 및 취소
                                - **변경 이력**: 모든 변경사항 추적
                                - **엑셀 연동**: 데이터 업로드 및 다운로드

                                ### 인증 방법
                                1. 회원가입 후 이메일 인증 완료
                                2. 로그인으로 Access Token 발급 받기
                                3. API 호출 시 `Authorization: Bearer {token}` 헤더 포함

                                ### 새로운 MovementType
                                - **INBOUND**: 입고 (재고 증가)
                                - **OUTBOUND**: 출고 (재고 감소)
                                - **RETURN_INBOUND**: 입고 반품 (재고 감소)
                                - **RETURN_OUTBOUND**: 출고 반품 (재고 증가)
                                - **EXCHANGE_OUT**: 교환 출고 (재고 감소)
                                - **EXCHANGE_IN**: 교환 입고 (재고 증가)
                                """)
                )
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT 토큰을 입력하세요")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}