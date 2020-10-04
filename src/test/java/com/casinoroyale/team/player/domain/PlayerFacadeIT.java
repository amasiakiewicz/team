package com.casinoroyale.team.player.domain;

import static com.casinoroyale.team.TeamApplication.DEFAULT_ZONE_OFFSET;
import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration.builder;

import java.time.LocalDate;
import java.util.UUID;

import com.casinoroyale.player.player.dto.CreatePlayerNoticeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PlayerFacadeIT {

    @Autowired //SUT
    private PlayerFacade playerFacade;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void shouldCreatePlayer() {
        //given
        final UUID playerId = randomUUID();
        final UUID teamId = randomUUID();
        final CreatePlayerNoticeDto createPlayerNoticeDto = givenCreatePlayerNoticeDto(playerId, teamId);

        //when
        playerFacade.createPlayer(createPlayerNoticeDto);

        //then
        assertThat(existingPlayerInDb(playerId))
                .usingRecursiveComparison(builder().withIgnoredFields("version", "createdDateTime").build())
                .isEqualTo(expectedPlayer(playerId, teamId));
    }

    @Test
    void shouldUpdatePlayerOnlyWithTeamId() {
        //given
        final UUID playerId = randomUUID();
        final UUID oldTeamId = randomUUID();

        givenPlayerInDb(playerId, oldTeamId);

        final UUID newTeamId = randomUUID();

        //when
        playerFacade.updatePlayer(playerId, newTeamId);

        //then
        assertThat(existingPlayerInDb(playerId))
                .usingRecursiveComparison(builder().withIgnoredFields("version", "createdDateTime").build())
                .isEqualTo(expectedPlayer(playerId, newTeamId));
    }

    @Test
    void shouldDeletePlayer() {
        //given
        final UUID playerId = givenPlayerInDb();

        //when
        playerFacade.deletePlayer(playerId);

        //then
        assertThat(playerId).satisfies(this::doesntExistInDb);
    }

    private void doesntExistInDb(final UUID playerId) {
        final boolean exists = playerRepository.existsById(playerId);
        assertThat(exists).isFalse();
    }

    private Player expectedPlayer(final UUID playerId, final UUID teamId) {
        return new Player(playerId, teamId);
    }

    private Player existingPlayerInDb(final UUID playerId) {
        return playerRepository
                .findById(playerId)
                .orElseThrow(IllegalStateException::new);
    }

    private UUID givenPlayerInDb() {
        final UUID teamId = randomUUID();
        return givenPlayerInDb(teamId);
    }

    private UUID givenPlayerInDb(final UUID teamId) {
        final UUID playerId = randomUUID();
        return givenPlayerInDb(playerId, teamId);
    }

    private UUID givenPlayerInDb(final UUID playerId, final UUID teamId) {
        final CreatePlayerNoticeDto createPlayerNoticeDto = givenCreatePlayerNoticeDto(playerId, teamId);

        final Player player = Player.create(createPlayerNoticeDto);
        playerRepository.save(player);

        return playerId;
    }

    private CreatePlayerNoticeDto givenCreatePlayerNoticeDto(final UUID playerId, final UUID teamId) {
        final LocalDate dateOfBirth = now(DEFAULT_ZONE_OFFSET).minusYears(5);
        final LocalDate playStart = now(DEFAULT_ZONE_OFFSET).minusMonths(3);

        return new CreatePlayerNoticeDto(playerId, teamId, dateOfBirth, playStart);
    }

}