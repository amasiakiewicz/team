package com.casinoroyale.team.player.domain;

import static javax.persistence.AccessType.FIELD;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.Entity;

import com.casinoroyale.player.player.dto.CreatePlayerNoticeDto;
import com.casinoroyale.team.infrastructure.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Access(FIELD)
@NoArgsConstructor(access = PRIVATE)
class Player extends BaseEntity {

    @Getter(PACKAGE)
    private UUID teamId;

    static Player create(final CreatePlayerNoticeDto createPlayerNoticeDto) {
        return new Player(
                createPlayerNoticeDto.getPlayerId(),
                createPlayerNoticeDto.getTeamId()
        );
    }

    Player(final UUID id, final UUID teamId) {
        super(id);
        this.teamId = teamId;
    }

    void update(final UUID newTeamId) {
        teamId = newTeamId;
    }
}
