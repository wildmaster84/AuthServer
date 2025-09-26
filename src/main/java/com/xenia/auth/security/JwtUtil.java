package com.xenia.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;

import org.springframework.stereotype.Component;
import com.xenia.auth.model.User;

import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET = "42C5877E844A110DEC2AE5AA9F189EB9E7ABCC8A15110F895DD6FAFE529293965EC19B2C7917C7892CB2ABC68BC2A8CC546F3AF79BA8BC9E758AF90663CF72A5E3A68B10BD2A6B0AC29843899347D77D57DAF46F991F4F7EFD1FE1FCB096D8CB852635A03154A30A3AFE4";
    private final long EXPIRATION = 1000 * 60 * 60 * 24; // 24h

    public String generateToken(User user, String ip) {
        String token = Jwts.builder()
                .setSubject(user.get("username").get("username").asText())
                .claim("offlineXuid", user.get("offline_xuid").get("offline_xuid").asText())
                .claim("ip", ip)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();

        // Store token in KeyStore and return 16-char key
        return KeyStore.store(token);
    }

    public Claims extractClaims(String token) {
        if (token == null) throw new IllegalArgumentException("Invalid token");
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String key, String username) {
        String token = KeyStore.retrieve(key);
        if (token == null) return false;

        final String user = extractClaims(key).getSubject();
        return (user.equals(username) && !isTokenExpired(key));
    }

    public boolean isTokenExpired(String key) {
        String token = KeyStore.retrieve(key);
        if (token == null) return true;

        final Date expiration = extractClaims(key).getExpiration();
        boolean expired = expiration.before(new Date());
        if (expired) KeyStore.remove(key);
        return expired;
    }
}
