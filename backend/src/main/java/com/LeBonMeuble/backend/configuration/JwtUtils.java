package com.LeBonMeuble.backend.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.expiration-time}")
    private Long expirationTime;

    /**
     * Génère un token JWT avec :
     *  - id utilisateur
     *  - firstname
     *  - role (normalisé en minuscules)
     */
    public String generateToken(UserDetails userDetails, String firstname, Long id) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("firstname", firstname);

        // 🔥 Normalisation du rôle : "ROLE_USER" → "user"
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority().replace("ROLE_", "").toLowerCase())
                .orElse("user");

        claims.put("role", role);

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

    /**
     * Vérification complète du token JWT
     */
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

    /**
     * Méthode générique pour extraire n'importe quel claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    /**
     * Extraction brute des claims
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
