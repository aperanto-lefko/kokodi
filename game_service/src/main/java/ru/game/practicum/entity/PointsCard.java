package ru.game.practicum.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("POINTS")
@Data
@EqualsAndHashCode(callSuper = true)
public class PointsCard extends Card {

}
