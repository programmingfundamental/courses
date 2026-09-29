package bg.tu_varna.sit.task_manager.model.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;

/**
 *  Добавено в лабораторно упражнение 8
 */
public class AfterMidnightValidator implements ConstraintValidator<AfterMidnight, LocalTime> {

    @Override
    public boolean isValid(LocalTime value, ConstraintValidatorContext context) {
        if (value == null) return true; // @NotNull ще го хване
        return value.isAfter(LocalTime.MIDNIGHT); // MIDNIGHT = 00:00
    }
}
