package com.LeBonMeuble.backend.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.expiration-time}")
    private Long expirationTime;

    /**
     * Génère un token JWT :
     *  - id
     *  - firstname
     *  - role formaté en ROLE_ADMIN / ROLE_USER
     *  - authorities compatible Spring Security
     */
    public String generateToken(UserDetails userDetails, String firstname, Long id) {

        Map<String, Object> claims = new HashMap<>();

        // Récupération du rôle Spring Security (e.g. ROLE_ADMIN)
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority().toUpperCase())  // 🔥 FORCÉ en majuscules
                .orElse("ROLE_USER");

        // Claims utiles
        claims.put("id", id);
        claims.put("firstname", firstname);
        claims.put("role", role.replace("ROLE_", "").toLowerCase()); // admin / user
        claims.put("authorities", List.of(role)); // e.g. ["ROLE_ADMIN"]

        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    /** Vérification complète du token JWT */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            String email = extractEmail(token);
            boolean expired = isTokenExpired(token);

            System.out.println("---- VALIDATION ----");
            System.out.println("Token email   = " + email);
            System.out.println("User username = " + userDetails.getUsername());
            System.out.println("Expired       = " + expired);
            System.out.println("---------------------");

            return (email.equals(userDetails.getUsername()) && !expired);

        } catch (Exception e) {
            System.err.println("Erreur validateToken : " + e.getMessage());
            return false;
        }
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    /** Méthode générique pour extraire n'importe quel claim */
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    /** Extraction brute des claims */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}