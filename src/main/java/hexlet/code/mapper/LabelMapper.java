package hexlet.code.mapper;

import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpsertDTO;
import hexlet.code.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class LabelMapper {
    public abstract LabelDTO map(Label model);

    public abstract Label map(LabelUpsertDTO dto);

    public abstract void update(LabelUpsertDTO dto, @MappingTarget Label model);
}
