package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.config.JwtConfig;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.config.SecurityConfig;

// Injetamos as configurações do H2 direto no coração do teste!
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=password",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("testAPI-CORE")
class ApiCoreSipLogApplicationTests {

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private JwtConfig jwtConfig;

    @MockitoBean
    private SecurityConfig securityConfig;

    @Test
    void contextLoads() {
    }
}