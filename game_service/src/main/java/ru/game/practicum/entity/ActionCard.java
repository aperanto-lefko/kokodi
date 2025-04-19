package ru.game.practicum.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("ACTION")
@Data
@EqualsAndHashCode(callSuper = true)
public class ActionCard extends Card {
}
