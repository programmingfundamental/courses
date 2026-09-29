package bg.tu_varna.sit.task_manager.repository;

import bg.tu_varna.sit.task_manager.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/***
 * Добавено в лабораторно упражнение 11
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
