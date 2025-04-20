package ru.game.practicum.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.game.practicum.dto.game_service.GameState;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "game_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private GameState state;
    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL)
    private List<Player> players;
    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL)
    private List<Card> deck;
    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL)
    private List<Turn> turns;
    private Integer currentPlayerIndex;
}
