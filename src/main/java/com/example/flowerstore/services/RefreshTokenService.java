package com.example.flowerstore.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.flowerstore.exception.InvalidTokenException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpirationMs;

    // save refresh token to redis
    public void saveRefreshToken(String userId, String refreshToken) {
    String key = "refreshTokens:" + userId;

    // save refresh token to redis with expiration time
    redisTemplate.opsForHash().put(key, refreshToken, String.valueOf(System.currentTimeMillis() + refreshTokenExpirationMs));
    redisTemplate.expire(key, refreshTokenExpirationMs, TimeUnit.MILLISECONDS);

    // limit refresh token count
    long maxTokens = 3;
    long tokenCount = redisTemplate.opsForHash().size(key);

    if (tokenCount > maxTokens) {
        // get the oldest tokens to delete
        List<Object> tokens = new ArrayList<>(redisTemplate.opsForHash().keys(key));
        for (int i = 0; i < tokenCount - maxTokens; i++) {
            redisTemplate.opsForHash().delete(key, tokens.get(i));
        }
    }
    }

    // get all refresh tokens of userId
    public Map<Object, Object> getRefreshTokens(String userId) {
        String key = "refreshTokens:" + userId;
        return redisTemplate.opsForHash().entries(key);
    }

    // revoke one refresh token
    public void revokeRefreshToken(String userId, String refreshToken) {
        String key = "refreshTokens:" + userId;
        redisTemplate.opsForHash().delete(key, refreshToken);
    }

    // revoke all refresh tokens of userId
    public void revokeAllTokens(String userId) {
        String key = "refreshTokens:" + userId;
        redisTemplate.delete(key);
    }

    // check refresh token is exist and valid
    public boolean validateRefreshToken(String userId, String refreshToken) {
        String key = "refreshTokens:" + userId;
    
        // get expireTime from redis
        Object expireTimeObj = redisTemplate.opsForHash().get(key, refreshToken);
    
        if (expireTimeObj == null) {
            return false;
        }
    
        // convert expireTime to String
        String expireTimeStr = expireTimeObj.toString();
    
        // check expireTime
        long expireTime = Long.parseLong(expireTimeStr);
        return expireTime > System.currentTimeMillis();
    }

    //update ttl
    public void updateTokenTTL(String userId, String refreshToken) {
    String key = "refreshTokens:" + userId;

    if (!redisTemplate.opsForHash().hasKey(key, refreshToken)) {
        throw new InvalidTokenException("Refresh Token does not exist or is expired");
    }

    // set ttl for key
    redisTemplate.expire(key, refreshTokenExpirationMs, TimeUnit.MILLISECONDS);
    }

    
}
