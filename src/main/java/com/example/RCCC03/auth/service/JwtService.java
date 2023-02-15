package com.example.RCCC03.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String ACCESS_TOKEN_SECRET = "";
    private static final int ACCESS_TOKEN_EXPIRATION_MILLISECONDS = 1000*60;

    public Date extractExpiration(String jwt){
        return extractClaim(jwt,Claims::getExpiration);
    }
    public String extractEmail(String jwt){
        return extractClaim(jwt,Claims::getSubject);
    }
    public boolean isTokenValid(String jwt,UserDetails userDetails){
        String email = extractEmail(jwt);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    public String generateToken(
            UserDetails userDetails
    ){
      return generateToken(new HashMap<>(),userDetails);
    }
    public String generateToken(
           Map<String, Object> payload,
          UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(payload)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ACCESS_TOKEN_EXPIRATION_MILLISECONDS))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public <T> T extractClaim(String jwt, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String jwt){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJwt(jwt).getBody();
    }

    private Key getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(ACCESS_TOKEN_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
