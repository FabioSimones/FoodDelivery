package br.com.totem.backend.shared.security;

import br.com.totem.backend.restaurante.entity.Restaurante;
import br.com.totem.backend.usuario.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.security.jwt.secret}")
    private String secret;

    @Value("${app.security.jwt.expiration-minutes}")
    private Long expirationMinutes;

    public String gerarToken(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plusSeconds(getExpirationSeconds());

        Restaurante restaurante = usuario.getRestaurante();

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("usuarioId", usuario.getId().toString())
                .claim("nome", usuario.getNome())
                .claim("perfil", usuario.getPerfil().name())
                .claim("restauranteId", restaurante != null ? restaurante.getId().toString() : null)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiracao))
                .signWith(getSigningKey())
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    public boolean tokenValido(String token, UserDetails userDetails) {
        String email = extrairEmail(token);
        return email.equals(userDetails.getUsername()) && !tokenExpirado(token);
    }

    public Long getExpirationSeconds() {
        return expirationMinutes * 60;
    }

    private boolean tokenExpirado(String token) {
        return extrairClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
