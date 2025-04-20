package ru.game.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.game.practicum.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {

}
