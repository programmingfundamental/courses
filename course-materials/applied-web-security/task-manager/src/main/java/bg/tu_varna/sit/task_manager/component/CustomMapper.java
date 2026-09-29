package bg.tu_varna.sit.task_manager.component;

import org.modelmapper.ModelMapper;
import bg.tu_varna.sit.task_manager.model.dto.request.ReportRequestDto;
import bg.tu_varna.sit.task_manager.model.dto.response.ReportResponseDto;
import bg.tu_varna.sit.task_manager.model.entity.Report;
import org.modelmapper.config.Configuration;
import org.springframework.stereotype.Component;
import java.lang.reflect.Type;
import org.modelmapper.TypeToken;
import java.util.List;

/**
 * Добавено в лабораторно упражнение 10
 */
@Component
public class CustomMapper extends ModelMapper {
    public CustomMapper() {
        this.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        typeMap(ReportRequestDto.class, Report.class).addMappings(m -> m.map(ReportRequestDto::getWorkTime, Report::setWorkedTime));
        typeMap(Report.class, ReportResponseDto.class).addMappings(m -> m.map(Report::getWorkedTime, ReportResponseDto::setWorkTime));
    }
}
