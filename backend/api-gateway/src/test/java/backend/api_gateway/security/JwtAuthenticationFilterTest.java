package backend.api_gateway.security;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import backend.api_gateway.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_ROLE = "USER";

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private FilterChain mockFilterChain;

    @Mock
    private JwtService mockJwtService;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        jwtAuthenticationFilter = new JwtAuthenticationFilter(mockJwtService);
    }

    @Test
    void shouldAuthenticateWithValidToken() throws ServletException, IOException {
        String token = "valid.jwt.token";
        when(mockJwtService.getTokenFromHeader()).thenReturn(token);
        when(mockJwtService.validateToken(token)).thenReturn(true);
        when(mockJwtService.getEmailFromToken(token)).thenReturn(VALID_EMAIL);
        when(mockJwtService.getRoleFromToken(token)).thenReturn(VALID_ROLE);

        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsNull() throws ServletException, IOException {
        when(mockJwtService.getTokenFromHeader()).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(mockFilterChain, never()).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalid() throws ServletException, IOException {
        String token = "invalid.jwt.token";
        when(mockJwtService.getTokenFromHeader()).thenReturn(token);
        when(mockJwtService.validateToken(token)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(mockFilterChain, never()).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldSetSecurityContextWithAdminRole() throws ServletException, IOException {
        String token = "valid.jwt.token";
        when(mockJwtService.getTokenFromHeader()).thenReturn(token);
        when(mockJwtService.validateToken(token)).thenReturn(true);
        when(mockJwtService.getEmailFromToken(token)).thenReturn("admin@example.com");
        when(mockJwtService.getRoleFromToken(token)).thenReturn("ADMIN");

        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldHandleProtectedResourceWithoutToken() throws ServletException, IOException {
        when(mockJwtService.getTokenFromHeader()).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void shouldPassValidTokenToSecurityContext() throws ServletException, IOException {
        String token = "valid.jwt.token";
        when(mockJwtService.getTokenFromHeader()).thenReturn(token);
        when(mockJwtService.validateToken(token)).thenReturn(true);
        when(mockJwtService.getEmailFromToken(token)).thenReturn("buyer@example.com");
        when(mockJwtService.getRoleFromToken(token)).thenReturn("USER");

        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldHandleMissingTokenForProtectedRoute() throws ServletException, IOException {
        when(mockJwtService.getTokenFromHeader()).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(mockFilterChain, never()).doFilter(mockRequest, mockResponse);
    }
}

