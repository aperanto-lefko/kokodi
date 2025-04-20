package ru.game.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.game.practicum.entity.Card;
import ru.game.practicum.entity.GameSession;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class})
public interface GameSessionMapper {
    @Mapping(target = "deckSize", source = "deck", qualifiedByName = "deckToDeckSize")
    GameSessionMapper toDto(GameSession gameSession);

    GameSession toEntity(GameSessionMapper gameSessionDto);

    @Named("deckToDeckSize")
    default Integer deckToDeckSize(List<Card> deck) {
        return deck != null ? deck.size() : null;
    }
}
