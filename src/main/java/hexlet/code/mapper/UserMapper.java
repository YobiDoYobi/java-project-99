package hexlet.code.mapper;

import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpsertDTO;
import hexlet.code.model.User;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {
    @Autowired
    private PasswordEncoder passwordEncoder;

    public abstract UserDTO map(User model);

    public abstract User map(UserUpsertDTO dto);

    public abstract void update(UserUpsertDTO dto, @MappingTarget User model);

    @BeforeMapping
    public void encryptPassword(UserUpsertDTO data) {
        if (data.getPassword() != null) {
            String passwordStr = String.valueOf(data.getPassword().get());
            String passwordEncoded = passwordEncoder.encode(passwordStr);
            data.setPassword(JsonNullable.of(passwordEncoded));
        }
    }
}
