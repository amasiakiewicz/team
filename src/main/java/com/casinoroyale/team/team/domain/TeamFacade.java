package com.casinoroyale.team.team.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static lombok.AccessLevel.PACKAGE;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import com.casinoroyale.team.team.dto.CreateTeamDto;
import com.casinoroyale.team.team.dto.CreateTeamNoticeDto;
import com.casinoroyale.team.team.dto.TeamQueryDto;
import com.casinoroyale.team.team.dto.UpdateTeamDto;
import lombok.AllArgsConstructor;
import org.joda.money.Money;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AllArgsConstructor(access = PACKAGE)
public class TeamFacade {

    private static final String TEAM_CREATED_TOPIC = "TeamCreated";
    private static final String TEAM_UPDATED_TOPIC = "TeamUpdated";
    private static final String TEAM_DELETED_TOPIC = "TeamDeleted";

    private final TeamRepository teamRepository;

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public Page<TeamQueryDto> findTeams(final Set<UUID> teamIds, final Pageable pageable) {
        checkArgument(teamIds != null);

        return teamRepository
                .findAllByIdInOrderByName(teamIds, pageable)
                .map(Team::toQueryDto);
    }

    public UUID createTeam(final CreateTeamDto createTeamDto) {
        checkArgument(createTeamDto != null);

        final String name = createTeamDto.getName();
        checkState(!teamRepository.existsByName(name), format("Team %s already exists", name));

        final Team team = Team.create(createTeamDto);
        teamRepository.save(team);

        final Money funds = createTeamDto.getFunds();
        final CreateTeamNoticeDto createTeamNoticeDto = team.toCreateNoticeDto(funds);
        kafkaTemplate.send(TEAM_CREATED_TOPIC, "", createTeamNoticeDto);

        return team.getId();
    }

    public void updateTeam(final UUID teamId, final UpdateTeamDto updateTeamDto) {
        checkArgument(teamId != null);
        checkArgument(updateTeamDto != null);

        final Team team = findTeam(teamId);
        team.update(updateTeamDto);

        final BigDecimal newCommissionRate = updateTeamDto.getCommissionRate();
        kafkaTemplate.send(TEAM_UPDATED_TOPIC, teamId, newCommissionRate);
    }

    public void deleteTeam(final UUID teamId) {
        checkArgument(teamId != null);

        final Team team = findTeam(teamId);
        teamRepository.delete(team);

        kafkaTemplate.send(TEAM_DELETED_TOPIC, "", teamId);
    }

    private Team findTeam(final UUID teamId) {
        return teamRepository
                .findById(teamId)
                .orElseThrow(() -> new IllegalStateException(format("Team %s doesn't exist", teamId)));
    }
}
