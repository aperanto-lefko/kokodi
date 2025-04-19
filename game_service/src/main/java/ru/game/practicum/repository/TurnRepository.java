package ru.game.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.game.practicum.entity.Turn;

import java.util.UUID;
@Repository
public interface TurnRepository extends JpaRepository<Turn, UUID> {
}
