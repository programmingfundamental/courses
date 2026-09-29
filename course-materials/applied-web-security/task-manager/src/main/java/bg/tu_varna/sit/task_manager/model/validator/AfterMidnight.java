package bg.tu_varna.sit.task_manager.model.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 *  Добавено в лабораторно упражнение 8
 */
@Documented
@Constraint(validatedBy = AfterMidnightValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterMidnight {
    String message() default "Часът трябва да е по-голям от 00:00";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
