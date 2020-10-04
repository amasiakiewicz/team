package com.casinoroyale.team.team.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static lombok.AccessLevel.PACKAGE;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import com.casinoroyale.team.player.domain.PlayerFacade;
import com.casinoroyale.team.team.dto.CreateTeamDto;
import com.casinoroyale.team.team.dto.CreateTeamNoticeDto;
import com.casinoroyale.team.team.dto.TeamQueryDto;
import com.casinoroyale.team.team.dto.UpdateTeamDto;
import com.casinoroyale.transfer.team.dto.FeePlayerTransferredNoticeDto;
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

    private final PlayerFacade playerFacade;

    public Page<TeamQueryDto> findTeamsByPlayers(final Set<UUID> playerIds, final Pageable pageable) {
        checkArgument(playerIds != null);

        return teamRepository
                .findAllByPlayerIdIn(playerIds, pageable)
                .map(Team::toQueryDto);
    }

    public TeamQueryDto createTeam(final CreateTeamDto createTeamDto) {
        checkArgument(createTeamDto != null);

        final String name = createTeamDto.getName();
        final boolean teamNameExists = teamRepository.existsByName(name);
        checkState(!teamNameExists, format("Team %s already exists", name));

        final Team team = Team.create(createTeamDto);
        teamRepository.save(team);

        final Money funds = createTeamDto.getFunds();
        final CreateTeamNoticeDto createTeamNoticeDto = team.toCreateNoticeDto(funds);
        kafkaTemplate.send(TEAM_CREATED_TOPIC, "", createTeamNoticeDto);

        return team.toQueryDto();
    }

    public TeamQueryDto updateTeam(final UUID teamId, final UpdateTeamDto updateTeamDto) {
        checkArgument(teamId != null);
        checkArgument(updateTeamDto != null);

        final Team team = findTeam(teamId);
        team.update(updateTeamDto);

        final BigDecimal newCommissionRate = updateTeamDto.getCommissionRate();
        kafkaTemplate.send(TEAM_UPDATED_TOPIC, teamId, newCommissionRate);

        return team.toQueryDto();
    }

    public void deleteTeam(final UUID teamId) {
        checkArgument(teamId != null);

        final Team team = findTeam(teamId);
        teamRepository.delete(team);

        kafkaTemplate.send(TEAM_DELETED_TOPIC, "", teamId);
    }

    public void updateFundsAndPlayer(final FeePlayerTransferredNoticeDto feePlayerTransferredNoticeDto) {
        checkArgument(feePlayerTransferredNoticeDto != null);

        final UUID sellerTeamId = feePlayerTransferredNoticeDto.getSellerTeamId();
        final UUID buyerTeamId = feePlayerTransferredNoticeDto.getBuyerTeamId();

        final Team sellerTeam = findTeam(sellerTeamId);
        final Team buyerTeam = findTeam(buyerTeamId);

        final Money sellerTeamFunds = feePlayerTransferredNoticeDto.getSellerTeamFunds();
        final Money buyerTeamFunds = feePlayerTransferredNoticeDto.getBuyerTeamFunds();

        final UUID playerId = feePlayerTransferredNoticeDto.getPlayerId();

        sellerTeam.updateFunds(sellerTeamFunds);
        buyerTeam.updateFunds(buyerTeamFunds);
        playerFacade.updatePlayer(playerId, buyerTeamId);
    }

    private Team findTeam(final UUID teamId) {
        return teamRepository
                .findById(teamId)
                .orElseThrow(() -> new IllegalStateException(format("Team %s doesn't exist", teamId)));
    }
}
