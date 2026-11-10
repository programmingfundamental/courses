package bg.tu_varna.sit.task_manager.model.validator;

import bg.tu_varna.sit.task_manager.model.dto.request.FilterReportDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateRangeValidatorTest {
    private final DateRangeValidator validator = new DateRangeValidator();
    private final LocalDateTime start = LocalDateTime.of(2026, 1, 15, 9, 0);

    @Test
    void acceptsEndAfterStart() {
        assertTrue(validator.isValid(rangeEndingAt(start.plusMinutes(1)), null));
    }

    @Test
    void rejectsEndEqualToStart() {
        assertFalse(validator.isValid(rangeEndingAt(start), null));
    }

    private FilterReportDto rangeEndingAt(LocalDateTime end) {
        var range = new FilterReportDto();
        range.setFrom(start);
        range.setTo(end);
        return range;
    }
}
