package com.casinoroyale.team.player.domain;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class TestPlayerFacade {

    private final PlayerRepository playerRepository;

    TestPlayerFacade(final PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public UUID findTeamByPlayer(final UUID playerId) {
        final Player player = playerRepository
                .findById(playerId)
                .orElseThrow(IllegalStateException::new);

        return player.getTeamId();
    }
}
