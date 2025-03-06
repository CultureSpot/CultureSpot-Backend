package com.culturespot.culturespotdomain.auth.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
  private final Key key;

  public JwtUtil(@Value("${jwt.secretKey}") String secretKey) {
    this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  }

  public String createToken(String email, String role, String provider, long expiration) {
    return Jwts.builder()
        .setSubject(email)
        .claim("email", email)
        .claim("role", role)
        .claim("provider", provider)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
        .signWith(key, SignatureAlgorithm.HS512)
        .compact();
  }

  Claims extractClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public String getEmail(String token) {
    return extractClaims(token).get("email", String.class);
  }

  public String getRole(String token) {
    return extractClaims(token).get("role", String.class);
  }

  public String getProvider(String token) {
    return extractClaims(token).get("provider", String.class);
  }

  public Date getExpiration(String token) {
    return extractClaims(token).getExpiration();
  }

}
