package bg.tu_varna.sit.task_manager.repository;

import bg.tu_varna.sit.task_manager.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

/***
 * Добавено в лабораторно упражнение 9
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
}
