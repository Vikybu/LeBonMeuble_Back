package com.LeBonMeuble.backend.filter;

import com.LeBonMeuble.backend.configuration.JwtUtils;
import com.LeBonMeuble.backend.services.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 🔥 Bypass complet des routes publiques
        String path = request.getServletPath();
        if (path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        // 🔎 Extraction du token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                email = jwtUtils.extractEmail(jwt);
            } catch (Exception e) {
                logger.warn("JWT invalide ou expiré : " + e.getMessage());
            }
        }

        // 🚨 Vérification de l'utilisateur uniquement si pas déjà authentifié
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            // 🔐 Vérification du token
            if (jwtUtils.validateToken(jwt, userDetails)) {

                Claims claims = jwtUtils.extractAllClaims(jwt);
                String role = claims.get("role", String.class);  // USER ou ADMIN attendu

                // 🔥 Construction correcte de l'autorité : ROLE_USER, ROLE_ADMIN
                SimpleGrantedAuthority authority =
                        new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                List.of(authority)
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 💾 Injection dans le SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Poursuite du filtre
        filterChain.doFilter(request, response);
    }
}