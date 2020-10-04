package com.casinoroyale.team.team.domain;

import static com.casinoroyale.team.TeamApplication.DEFAULT_ZONE_OFFSET;
import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration.builder;
import static org.joda.money.CurrencyUnit.EUR;
import static org.joda.money.CurrencyUnit.USD;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.casinoroyale.player.player.dto.CreatePlayerNoticeDto;
import com.casinoroyale.team.player.domain.PlayerFacade;
import com.casinoroyale.team.player.domain.TestPlayerFacade;
import com.casinoroyale.team.team.dto.CreateTeamDto;
import com.casinoroyale.team.team.dto.TeamQueryDto;
import com.casinoroyale.team.team.dto.UpdateTeamDto;
import com.casinoroyale.transfer.team.dto.FeePlayerTransferredNoticeDto;
import com.google.common.collect.ImmutableSet;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SpringBootTest
class TeamFacadeIT {

    @Autowired //SUT
    private TeamFacade teamFacade;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerFacade playerFacade;

    @Autowired
    private TestPlayerFacade testPlayerFacade;

    @Test
    void shouldFindTeamsByPlayers() {
        //given
        final UUID team1 = givenTeamInDb("team1");
        final UUID team2 = givenTeamInDb("team2");
        final UUID player1 = givenPlayerInDbInTeam(team1);
        final UUID player2 = givenPlayerInDbInTeam(team1);
        final UUID player3 = givenPlayerInDbInTeam(team2);
        final Set<UUID> playerIds = ImmutableSet.of(player1, player2, player3);
        final Pageable pageable = givenPageable(playerIds.size());

        //when
        final Page<TeamQueryDto> teams = teamFacade.findTeamsByPlayers(playerIds, pageable);

        //then
        assertThat(teams).isEqualTo(expectedTeams(pageable, team1, team2));
    }

    @Test
    void shouldCreateTeam() {
        //given
        final String name = randomAlphabetic(5);
        final Year establishedYear = Year.of(1925);
        final Money funds = Money.of(USD, 123456);
        final String headCoach = "headCoach";
        final String stadium = "stadium";
        final BigDecimal commissionRate = valueOf(0.02);
        final CreateTeamDto createTeamDto = givenCreateTeamDto(name, establishedYear, funds, headCoach, stadium, commissionRate);

        //when
        final TeamQueryDto createdTeam = teamFacade.createTeam(createTeamDto);

        //then
        assertThat(createdTeam)
                .isEqualTo(existingTeamInDb(createdTeam.getTeamId()))
                .usingRecursiveComparison(builder().withIgnoredFields("teamId").build())
                .isEqualTo(expectedTeam(name, establishedYear, funds, headCoach, stadium, commissionRate));
    }

    @Test
    void shouldUpdateTeam() {
        //given
        final String name = randomAlphabetic(5);
        final Year establishedYear = Year.of(1910);
        final Money funds = Money.of(USD, 123456);
        final String oldHeadCoach = "oldHeadCoach";
        final String oldStadium = "oldStadium";
        final BigDecimal oldCommissionRate = valueOf(0.04);
        final UUID teamId = givenTeamInDb(name, establishedYear, funds, oldHeadCoach, oldStadium, oldCommissionRate);

        final String newHeadCoach = "newHeadCoach";
        final String newStadium = "newStadium";
        final BigDecimal newCommissionRate = valueOf(0.07);
        final UpdateTeamDto updateTeamDto = new UpdateTeamDto(newHeadCoach, newStadium, newCommissionRate);

        //when
        final TeamQueryDto updatedTeam = teamFacade.updateTeam(teamId, updateTeamDto);

        //then
        assertThat(updatedTeam)
                .isEqualTo(existingTeamInDb(updatedTeam.getTeamId()))
                .usingRecursiveComparison(builder().withIgnoredFields("teamId").build())
                .isEqualTo(expectedTeam(name, establishedYear, funds, newHeadCoach, newStadium, newCommissionRate));
    }

    @Test
    void shouldDeleteTeam() {
        //given
        final UUID teamId = givenTeamInDb();

        //when
        teamFacade.deleteTeam(teamId);

        //then
        assertThat(teamId).satisfies(this::doesntExistInDb);
    }

    @Test
    void shouldUpdateFundsAndPlayer() {
        //given
        final Money oldSellerTeamFunds = Money.of(USD, 20.15);
        final Money oldBuyerTeamFunds = Money.of(EUR, 185.35);

        final UUID sellerTeamId = givenTeamInDb(oldSellerTeamFunds);
        final UUID buyerTeamId = givenTeamInDb(oldBuyerTeamFunds);

        final Money newSellerTeamFunds = Money.of(USD, 120.15);
        final Money newBuyerTeamFunds = Money.of(EUR, 789.12);

        final UUID playerId = givenPlayerInDbInTeam(sellerTeamId);

        final FeePlayerTransferredNoticeDto feePlayerTransferredNoticeDto = new FeePlayerTransferredNoticeDto(
                newSellerTeamFunds, newBuyerTeamFunds, sellerTeamId, buyerTeamId, playerId
        );

        //when
        teamFacade.updateFundsAndPlayer(feePlayerTransferredNoticeDto);

        //then
        assertThatFundsAndPlayerChanged(sellerTeamId, buyerTeamId, newSellerTeamFunds, newBuyerTeamFunds, playerId);
    }

    private Page<TeamQueryDto> expectedTeams(final Pageable pageable, final UUID... teamIds) {
        final List<TeamQueryDto> teams = Arrays
                .stream(teamIds)
                .map(this::existingTeamInDb)
                .collect(Collectors.toList());
        return new PageImpl<>(teams, pageable, teamIds.length);
    }

    private UUID givenTeamInDb(final String name) {
        return givenTeamInDb(name, Year.of(1920), Money.of(USD, 123), "", "", valueOf(0.02));
    }

    private PageRequest givenPageable(final int pageSize) {
        return PageRequest.of(0, pageSize, Sort.by("name"));
    }

    private UUID givenPlayerInDbInTeam(final UUID teamId) {
        final UUID playerId = randomUUID();
        final LocalDate dateOfBirth = now(DEFAULT_ZONE_OFFSET).minusYears(15);
        final LocalDate playStart = now(DEFAULT_ZONE_OFFSET).minusMonths(10);

        final CreatePlayerNoticeDto createPlayerNoticeDto = new CreatePlayerNoticeDto(playerId, teamId, dateOfBirth, playStart);
        playerFacade.createPlayer(createPlayerNoticeDto);

        return playerId;
    }

    private void assertThatFundsAndPlayerChanged(
            final UUID sellerTeamId, final UUID buyerTeamId, final Money sellerTeamFunds, final Money buyerTeamFunds,
            final UUID playerId
    ) {
        final TeamQueryDto sellerTeam = existingTeamInDb(sellerTeamId);
        assertThat(sellerTeam.getFunds()).isEqualTo(sellerTeamFunds);

        final TeamQueryDto buyerTeam = existingTeamInDb(buyerTeamId);
        assertThat(buyerTeam.getFunds()).isEqualTo(buyerTeamFunds);

        final UUID playersTeamId = testPlayerFacade.findTeamByPlayer(playerId);
        assertThat(playersTeamId).isEqualTo(buyerTeamId);
    }

    private void doesntExistInDb(final UUID teamId) {
        final boolean exists = teamRepository.existsById(teamId);
        assertThat(exists).isFalse();
    }

    private UUID givenTeamInDb() {
        return givenTeamInDb(Money.of(USD, 123456));
    }

    private UUID givenTeamInDb(final Money funds) {
        return givenTeamInDb(randomAlphabetic(5), Year.of(1912), funds, "headCoach", "stadium", valueOf(0.03));
    }

    private TeamQueryDto expectedTeam(
            final String name, final Year establishedYear, final Money funds, final String headCoach, final String stadium,
            final BigDecimal commissionRate
    ) {
        return new TeamQueryDto(randomUUID(), name, establishedYear, funds, headCoach, stadium, commissionRate);
    }

    private UUID givenTeamInDb(
            final String name, final Year establishedYear, final Money funds, final String headCoach, final String stadium,
            final BigDecimal commissionRate
    ) {
        final CreateTeamDto createTeamDto = givenCreateTeamDto(name, establishedYear, funds, headCoach, stadium, commissionRate);
        final Team team = Team.create(createTeamDto);
        teamRepository.save(team);

        return team.getId();
    }

    private CreateTeamDto givenCreateTeamDto(
            final String name, final Year establishedYear, final Money funds, final String headCoach, final String stadium,
            final BigDecimal commissionRate
    ) {
        teamRepository
                .findByName(name)
                .ifPresent(t -> teamRepository.delete(t));

        final CreateTeamDto createTeamDto = new CreateTeamDto();
        createTeamDto.setName(name);
        createTeamDto.setEstablishedYear(establishedYear);
        createTeamDto.setFunds(funds);
        createTeamDto.setHeadCoach(headCoach);
        createTeamDto.setStadium(stadium);
        createTeamDto.setCommissionRate(commissionRate);

        return createTeamDto;
    }

    private TeamQueryDto existingTeamInDb(final UUID teamId) {
        final Team team = teamRepository
                .findById(teamId)
                .orElseThrow(IllegalStateException::new);
        return team.toQueryDto();
    }

}