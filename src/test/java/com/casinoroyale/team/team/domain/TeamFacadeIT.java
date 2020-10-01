package com.casinoroyale.team.team.domain;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration.builder;
import static org.joda.money.CurrencyUnit.USD;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.casinoroyale.team.team.dto.CreateTeamDto;
import com.casinoroyale.team.team.dto.TeamCreatedQueryDto;
import com.casinoroyale.team.team.dto.TeamQueryDto;
import com.casinoroyale.team.team.dto.UpdateTeamDto;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class TeamFacadeIT {

    @Autowired
    private TeamFacade teamFacade;

    @Autowired
    private TeamRepository teamRepository;

    private Set<String> names;

    @Test
    void shouldFindTeams() {
        //given
        final Pageable pageable = givenPageable();
        final Set<UUID> teamIds = givenTeamsInDb(pageable);

        //when
        final Page<TeamQueryDto> teams = teamFacade.findTeams(teamIds, pageable);

        //then
        assertThat(teams).isEqualTo(expectedTeams(pageable));
    }

    @Test
    void shouldCreateTeam() {
        //given
        final String name = "name";
        final Year establishedYear = Year.of(1925);
        final String headCoach = "headCoach";
        final String stadium = "stadium";
        final BigDecimal commissionRate = valueOf(0.02);
        final CreateTeamDto createTeamDto = givenCreateTeamDto(name, establishedYear, headCoach, stadium, commissionRate);

        //when
        final TeamCreatedQueryDto createdTeam = teamFacade.createTeam(createTeamDto);

        //then
        assertThat(existingTeamInDb(createdTeam))
                .usingRecursiveComparison(builder().withIgnoredFields("id", "version", "createdDateTime").build())
                .isEqualTo(expectedTeam(name, establishedYear, headCoach, stadium, commissionRate));
    }

    @Test
    void shouldUpdateTeam() {
        //given
        final String name = "name";
        final Year establishedYear = Year.of(1910);
        final String oldHeadCoach = "oldHeadCoach";
        final String oldStadium = "oldStadium";
        final BigDecimal oldCommissionRate = valueOf(0.04);
        final UUID teamId = givenTeamInDb(name, establishedYear, oldHeadCoach, oldStadium, oldCommissionRate);

        final String newHeadCoach = "newHeadCoach";
        final String newStadium = "newStadium";
        final BigDecimal newCommissionRate = valueOf(0.07);
        final UpdateTeamDto updateTeamDto = new UpdateTeamDto(newHeadCoach, newStadium, newCommissionRate);

        //when
        teamFacade.updateTeam(teamId, updateTeamDto);

        //then
        assertThat(existingTeamInDb(teamId))
                .usingRecursiveComparison(builder().withIgnoredFields("version", "createdDateTime").build())
                .isEqualTo(expectedTeam(teamId, name, establishedYear, newHeadCoach, newStadium, newCommissionRate));
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

    private void doesntExistInDb(final UUID teamId) {
        final boolean exists = teamRepository.existsById(teamId);
        assertThat(exists).isFalse();
    }

    private UUID givenTeamInDb() {
        return givenTeamInDb("name", Year.of(1912), "headCoach", "stadium", valueOf(0.03));
    }

    private Team expectedTeam(final String name, final Year establishedYear, final String headCoach, final String stadium, final BigDecimal commissionRate) {
        return expectedTeam(randomUUID(), name, establishedYear, headCoach, stadium, commissionRate);
    }

    private UUID givenTeamInDb(final String name, final Year establishedYear, final String headCoach, final String stadium, final BigDecimal commissionRate) {
        final CreateTeamDto createTeamDto = givenCreateTeamDto(name, establishedYear, headCoach, stadium, commissionRate);
        final Team team = Team.create(createTeamDto);
        teamRepository.save(team);

        return team.getId();
    }

    private Team expectedTeam(
            final UUID teamId, final String name, final Year establishedYear, final String headCoach, final String stadium, final BigDecimal commissionRate
    ) {
        final BigDecimal commissionRateScaled = commissionRate.setScale(4, HALF_UP);
        final LocalDate established = establishedYear.atDay(1);
        return new Team(teamId, name, established, headCoach, stadium, commissionRateScaled);
    }

    private CreateTeamDto givenCreateTeamDto(
            final String name, final Year establishedYear, final String headCoach, final String stadium,
            final BigDecimal commissionRate
    ) {
        teamRepository
                .findByName(name)
                .ifPresent(t -> teamRepository.delete(t));

        final CreateTeamDto createTeamDto = new CreateTeamDto();
        createTeamDto.setName(name);
        createTeamDto.setEstablishedYear(establishedYear);
        createTeamDto.setFunds(Money.of(USD, 123456));
        createTeamDto.setHeadCoach(headCoach);
        createTeamDto.setStadium(stadium);
        createTeamDto.setCommissionRate(commissionRate);

        return createTeamDto;
    }

    private Set<UUID> givenTeamsInDb(final Pageable pageable) {
        final Set<UUID> teamsIds = new HashSet<>();
        names = new HashSet<>();

        for (int i = 0; i < pageable.getPageSize(); i++) {
            final CreateTeamDto createTeamDto = new CreateTeamDto();
            final String name = randomAlphabetic(5);
            names.add(name);
            createTeamDto.setName(name);
            createTeamDto.setEstablishedYear(Year.of(1930));
            createTeamDto.setFunds(Money.of(USD, 123456.89));
            createTeamDto.setHeadCoach("headCoach");
            createTeamDto.setStadium("stadium");
            createTeamDto.setCommissionRate(valueOf(0.03));

            final Team team = Team.create(createTeamDto);
            teamRepository.save(team);

            teamsIds.add(team.getId());
        }

        return teamsIds;
    }

    private PageRequest givenPageable() {
        return PageRequest.of(0, 2);
    }

    private Page<TeamQueryDto> expectedTeams(final Pageable pageable) {
        final List<TeamQueryDto> teams = names
                .stream()
                .map(name -> new TeamQueryDto(name, Year.of(1930), "headCoach", "stadium"))
                .sorted((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()))
                .collect(toList());
        final int totalNumber = names.size();

        return new PageImpl<>(teams, pageable, totalNumber);
    }

    private Team existingTeamInDb(final TeamCreatedQueryDto teamCreatedQueryDto) {
        final UUID teamId = teamCreatedQueryDto.getTeamId();
        return existingTeamInDb(teamId);
    }

    private Team existingTeamInDb(final UUID teamId) {
        return teamRepository
                .findById(teamId)
                .orElseThrow(IllegalStateException::new);
    }

}