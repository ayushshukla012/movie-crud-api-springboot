package com.moviefilx.auth.service;

import com.moviefilx.auth.entities.RefreshToken;
import com.moviefilx.auth.entities.User;
import com.moviefilx.auth.repositories.RefreshTokenRepository;
import com.moviefilx.auth.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + username));

        RefreshToken refreshToken = user.getRefreshToken();

        if (refreshToken == null) {
            //In real life scenario, the refresh token validity should be for couple of days.
            //For 30 sec validity(should not use as refresh token validity should be more than access token validity)
            long refreshTokenValidity = 30 * 1000;
            //For 5hrs, use below validity
            // long refreshTokenValidity = 5*60*60*1000;
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found!"));

        if (refToken.getExpirationTime().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refToken);

            throw new RuntimeException("Refresh Token expired");
        }
        return refToken;
    }
}
