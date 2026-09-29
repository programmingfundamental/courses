package bg.tu_varna.sit.task_manager.model.validator;

import bg.tu_varna.sit.task_manager.model.dto.request.FilterReportDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 *  Добавено в лабораторно упражнение 8
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, FilterReportDto> {

    @Override
    public boolean isValid(FilterReportDto dto, ConstraintValidatorContext context) {
        if (dto.getFrom() == null || dto.getTo() == null) {
            return true; // @NotNull ще хване това
        }
        return dto.getTo().isAfter(dto.getFrom());
    }
}
