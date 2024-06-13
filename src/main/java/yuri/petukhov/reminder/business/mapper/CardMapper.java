package yuri.petukhov.reminder.business.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yuri.petukhov.reminder.business.dto.CardDTO;
import yuri.petukhov.reminder.business.dto.CardUpdate;
import yuri.petukhov.reminder.business.model.Card;
@Mapper(componentModel = "spring")
public interface CardMapper {
    @Mapping(source = "updatedCard.cardName", target = "cardName")
    @Mapping(source = "updatedCard.cardMeaning", target = "cardMeaning")
    Card updateCard(Card card, CardUpdate updatedCard);

    @Mapping(source = "cardName", target = "title")
    @Mapping(source = "cardMeaning", target = "content")
    CardDTO toCardDTO(Card card);
}
