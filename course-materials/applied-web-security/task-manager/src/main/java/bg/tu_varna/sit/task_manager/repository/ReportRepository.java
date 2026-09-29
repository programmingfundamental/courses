package bg.tu_varna.sit.task_manager.repository;

import bg.tu_varna.sit.task_manager.model.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/***
 * Добавено в лабораторно упражнение 10
 */
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByTaskId(Long taskId);
    List<Report> findByWorkedTimeAndDateCreatedBetween(LocalTime workedTime, LocalDateTime startDate, LocalDateTime endDate);
    @Query("""
        SELECT r FROM Report r 
        WHERE r.task.id = :taskId 
        ORDER BY r.workedTime DESC
    """)
    List<Report> findTop1ByTaskIdOrderByWorkedTimeDesc(@Param("taskId") Long taskId);

}