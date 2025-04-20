package ru.game.practicum.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "turns")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Turn { //Ход
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;
    @ManyToOne
    private Player player;
    @ManyToOne
    private Card playedCard;
    private Integer scoreChange;
    private Instant timestamp;
}
