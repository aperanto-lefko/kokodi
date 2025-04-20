package ru.game.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.GameState;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, UUID> {

}
