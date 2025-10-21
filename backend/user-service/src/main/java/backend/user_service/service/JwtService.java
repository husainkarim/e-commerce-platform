package backend.user_service.service;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(String userId, String email, String role) {
        long nowMillis = System.currentTimeMillis();
        long expirationMillis = nowMillis + (1000 * 60 * 60 * 24); // 1 day

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(new Date(expirationMillis))
                .signWith(key)
                .compact();
    }
}
