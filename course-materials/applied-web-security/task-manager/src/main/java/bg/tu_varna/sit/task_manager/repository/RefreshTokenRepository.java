package bg.tu_varna.sit.task_manager.repository;

import bg.tu_varna.sit.task_manager.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/***
 * Добавено в лабораторно упражнение 11
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);
    void deleteByUsername(String username);
}
