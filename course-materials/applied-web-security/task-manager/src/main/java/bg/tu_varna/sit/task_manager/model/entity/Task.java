package bg.tu_varna.sit.task_manager.model.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

/***
 * Добавено в лабораторно упражнение 9
 */
@Getter
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "summary", length = 255, nullable = false)
    private String summary;
    @Column(name = "description", nullable = false, length = 2500)
    private String description;
    @Column(name = "deadline", length = 50, nullable = false)
    private LocalDateTime deadline;
    @OneToMany(mappedBy = "task")
    private Set<Report> reports;
}
