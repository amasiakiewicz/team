package com.casinoroyale.player.player.dto;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlayerNoticeDto {

    UUID playerId;

    UUID teamId;

    LocalDate dateOfBirth;

    LocalDate playStart;

    public UUID getPlayerId() {
        checkArgument(playerId != null);

        return playerId;
    }

    public UUID getTeamId() {
        checkArgument(teamId != null);

        return teamId;
    }

    public LocalDate getDateOfBirth() {
        checkArgument(dateOfBirth != null);

        return dateOfBirth;
    }

    public LocalDate getPlayStart() {
        checkArgument(playStart != null);

        return playStart;
    }

}
