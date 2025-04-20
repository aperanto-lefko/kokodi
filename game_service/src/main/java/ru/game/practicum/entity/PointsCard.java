package ru.game.practicum.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("POINTS")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PointsCard extends Card {
}
