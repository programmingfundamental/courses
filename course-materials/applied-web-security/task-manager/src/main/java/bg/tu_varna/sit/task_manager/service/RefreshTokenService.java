package bg.tu_varna.sit.task_manager.service;

import bg.tu_varna.sit.task_manager.model.entity.RefreshToken;
import bg.tu_varna.sit.task_manager.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/***
 * Добавено в лабораторно упражнение 11
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshExpirationMs;

    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID() + "." + UUID.randomUUID())
                .username(username)
                .createdAt(Instant.now())
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(token)
                .orElseThrow(() -> new RuntimeException("Невалиден refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token е изтекъл");
        }

        return refreshToken;
    }

    @org.springframework.transaction.annotation.Transactional
    public void revokeAllUserTokens(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }
}
