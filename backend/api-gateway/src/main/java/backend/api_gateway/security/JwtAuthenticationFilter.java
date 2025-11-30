package backend.api_gateway.security;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import backend.api_gateway.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    
    // Define the list of public paths that should *bypass* this JWT filter entirely
    private static final List<String> PUBLIC_PATHS = List.of(
        "/",
        "/api/users/login",
        "/api/users/signup",
        "/api/products/list",
        "/api/media/getImagesByProductId"
    );

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * The recommended way to skip a filter. If this returns true, 
     * doFilterInternal() is never called, and the request proceeds to the next filter.
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        final String path = request.getRequestURI();
        final String method = request.getMethod();
        
        // 1. Always skip Pre-flight OPTIONS requests (CORS handling)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // 2. Skip for explicitly defined public paths
        return PUBLIC_PATHS.contains(path);
    }
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        // Note: If this method is called, the path is considered PROTECTED.

        String token = jwtService.getTokenFromHeader();
        
        // If a token is present and valid, set the security context.
        if (token != null && jwtService.validateToken(token)) {
            String email = jwtService.getEmailFromToken(token);
            String role = jwtService.getRoleFromToken(token);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                email, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Authentication successful, proceed with the rest of the security chain
            filterChain.doFilter(request, response);
            
        } else {
            // Protected resource access without a valid token. Return 401.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // Do NOT call filterChain.doFilter() after setting 401
        }
    }
}