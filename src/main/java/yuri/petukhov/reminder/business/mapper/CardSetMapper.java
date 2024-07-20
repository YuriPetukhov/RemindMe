package yuri.petukhov.reminder.business.mapper;

import org.mapstruct.Mapper;
import yuri.petukhov.reminder.business.dto.CreateCardSetDTO;
import yuri.petukhov.reminder.business.model.CardSet;

@Mapper(componentModel = "spring")
public interface CardSetMapper {

    CardSet toEntityCardSet(CreateCardSetDTO dto);
}
