package yuri.petukhov.reminder.business.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yuri.petukhov.reminder.business.dto.CreateCardSetDTO;
import yuri.petukhov.reminder.business.model.CardSet;

@Mapper(componentModel = "spring")
public interface CardSetMapper {

    @Mapping(source = "setName", target = "setName")
    @Mapping(source = "setDescription", target = "setDescription")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "user", ignore = true)
    CardSet toEntityCardSet(CreateCardSetDTO dto);
}
