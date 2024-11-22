package com.itone.it_one_authenticationservice.service;

import com.itone.it_one_authenticationservice.auth.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class BlacklistService {
    private final RedisTemplate<String, Object> redisTemplate;

    // Добавление токена в блэклист
    public void addToBlacklist(String token, Date expiration) {
        String tokenId = extractTokenId(token); // Метод для извлечения jti
        long ttl = expiration.getTime() - System.currentTimeMillis();
        redisTemplate.opsForValue().set("Blacklist:" + tokenId, "blacklisted", Duration.ofMillis(ttl));
    }

    // Проверка токена в блэклисте
    public boolean isTokenBlacklisted(String token) {
        String tokenId = extractTokenId(token);
        return Boolean.TRUE.equals(redisTemplate.hasKey("Blacklist:" + tokenId));
    }

    // Извлечение идентификатора токена (jti)
    private String extractTokenId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode("f2f8896cf49a08be69cef838de3b51d38ddeb7fadcf9e0e30f7181a9c2394d9d20a04d9257ebbcc696181154900280cbb3adcc68dbccf167903f9f78e36acfe8")))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getId();
    }
}
