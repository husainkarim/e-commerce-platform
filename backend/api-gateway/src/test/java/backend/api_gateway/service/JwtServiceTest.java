package backend.api_gateway.service;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String SECRET_KEY = Base64.getEncoder().encodeToString("my-secret-key-that-is-at-least-256-bits-long-for-hs256".getBytes());
    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_ROLE = "USER";
    private static final String VALID_USER_ID = "12345";

    @Mock
    private HttpServletRequest mockRequest;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(mockRequest, SECRET_KEY);
    }

    @Test
    void shouldThrowExceptionWhenSecretIsNull() {
        HttpServletRequest request = new org.mockito.Mockito().mock(HttpServletRequest.class);
        assertThatThrownBy(() -> new JwtService(request, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JWT_SECRET is missing");
    }

    @Test
    void shouldThrowExceptionWhenSecretIsEmpty() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        assertThatThrownBy(() -> new JwtService(request, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JWT_SECRET is missing");
    }

    @Test
    void shouldGenerateValidToken() {
        String token = jwtService.generateToken(VALID_USER_ID, VALID_EMAIL, VALID_ROLE);
        
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void shouldValidateGeneratedToken() {
        String token = jwtService.generateToken(VALID_USER_ID, VALID_EMAIL, VALID_ROLE);
        
        boolean isValid = jwtService.validateToken(token);
        
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        boolean isValid = jwtService.validateToken(invalidToken);
        
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseForNullToken() {
        boolean isValid = jwtService.validateToken(null);
        
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldExtractEmailFromToken() {
        String token = jwtService.generateToken(VALID_USER_ID, VALID_EMAIL, VALID_ROLE);
        
        String extractedEmail = jwtService.getEmailFromToken(token);
        
        assertThat(extractedEmail).isEqualTo(VALID_EMAIL);
    }

    @Test
    void shouldExtractRoleFromToken() {
        String token = jwtService.generateToken(VALID_USER_ID, VALID_EMAIL, VALID_ROLE);
        
        String extractedRole = jwtService.getRoleFromToken(token);
        
        assertThat(extractedRole).isEqualTo(VALID_ROLE);
    }

    @Test
    void shouldGetClaimsFromToken() {
        String token = jwtService.generateToken(VALID_USER_ID, VALID_EMAIL, VALID_ROLE);
        
        Claims claims = jwtService.getClaims(token);
        
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(VALID_EMAIL);
        assertThat(claims.get("userId")).isEqualTo(VALID_USER_ID);
        assertThat(claims.get("role")).isEqualTo(VALID_ROLE);
    }

    @Test
    void shouldReturnNullWhenAuthorizationHeaderIsAbsent() {
        when(mockRequest.getHeader("Authorization")).thenReturn(null);
        
        String token = jwtService.getTokenFromHeader();
        
        assertThat(token).isNull();
    }

    @Test
    void shouldReturnNullWhenAuthorizationHeaderDoesNotStartWithBearer() {
        when(mockRequest.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");
        
        String token = jwtService.getTokenFromHeader();
        
        assertThat(token).isNull();
    }

    @Test
    void shouldExtractTokenFromAuthorizationHeader() {
        String expectedToken = jwtService.generateToken(VALID_USER_ID, VALID_EMAIL, VALID_ROLE);
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + expectedToken);
        
        String extractedToken = jwtService.getTokenFromHeader();
        
        assertThat(extractedToken).isEqualTo(expectedToken);
    }

    @Test
    void shouldHandleMultipleDifferentRoles() {
        String adminToken = jwtService.generateToken("1", "admin@example.com", "ADMIN");
        String userToken = jwtService.generateToken("2", "user@example.com", "USER");
        
        assertThat(jwtService.getRoleFromToken(adminToken)).isEqualTo("ADMIN");
        assertThat(jwtService.getRoleFromToken(userToken)).isEqualTo("USER");
    }

    @Test
    void shouldThrowExceptionWhenExtractingEmailFromInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        assertThatThrownBy(() -> jwtService.getEmailFromToken(invalidToken))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldThrowExceptionWhenExtractingRoleFromInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        assertThatThrownBy(() -> jwtService.getRoleFromToken(invalidToken))
                .isInstanceOf(Exception.class);
    }
}
