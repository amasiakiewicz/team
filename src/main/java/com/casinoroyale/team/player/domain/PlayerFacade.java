package com.casinoroyale.team.player.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static lombok.AccessLevel.PACKAGE;

import java.util.UUID;

import com.casinoroyale.player.player.dto.CreatePlayerNoticeDto;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AllArgsConstructor(access = PACKAGE)
public class PlayerFacade {

    private final PlayerRepository playerRepository;

    public void createPlayer(final CreatePlayerNoticeDto createPlayerNoticeDto) {
        checkArgument(createPlayerNoticeDto != null);

        final Player player = Player.create(createPlayerNoticeDto);
        playerRepository.save(player);
    }

    public void updatePlayer(final UUID playerId, final UUID newTeamId) {
        checkArgument(playerId != null);
        checkArgument(newTeamId != null);

        final Player player = findPlayer(playerId);
        player.update(newTeamId);
    }

    public void deletePlayer(final UUID playerId) {
        checkArgument(playerId != null);

        final Player player = findPlayer(playerId);
        playerRepository.delete(player);
    }

    private Player findPlayer(final UUID playerId) {
        return playerRepository
                .findById(playerId)
                .<IllegalArgumentException> orElseThrow(() -> {
                    throw new IllegalArgumentException(format("Player %s does not exist", playerId));
                });
    }
}
