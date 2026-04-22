package br.mackenzie.labEngenhaSW.sipLogBFF;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("testBFF")
class SipLogBFFApplicationTests {

	@MockitoBean
    private JwtDecoder jwtDecoder;

	@Test
	void contextLoads() {
	}

}
