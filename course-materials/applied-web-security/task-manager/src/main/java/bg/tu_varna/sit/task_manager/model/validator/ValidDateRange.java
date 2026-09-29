package bg.tu_varna.sit.task_manager.model.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 *  Добавено в лабораторно упражнение 8
 */
@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    String message() default "Крайната дата трябва да е след началната";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
