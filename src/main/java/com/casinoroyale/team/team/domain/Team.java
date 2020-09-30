package com.casinoroyale.team.team.domain;

import static javax.persistence.AccessType.FIELD;
import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.Entity;

import com.casinoroyale.team.infrastructure.BaseEntity;
import com.casinoroyale.team.team.dto.CreateTeamDto;
import com.casinoroyale.team.team.dto.CreateTeamNoticeDto;
import com.casinoroyale.team.team.dto.TeamQueryDto;
import com.casinoroyale.team.team.dto.UpdateTeamDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.money.Money;

@Entity
@Access(FIELD)
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@ToString
class Team extends BaseEntity {

    private String name;

    private LocalDate established;

    private String headCoach;

    private String stadium;

    private BigDecimal commissionRate;

    static Team create(final CreateTeamDto createTeamDto) {
        final LocalDate established = createTeamDto.getEstablishedYear().atDay(1);
        return new Team(
                createTeamDto.getName(),
                established,
                createTeamDto.getHeadCoach(),
                createTeamDto.getStadium(),
                createTeamDto.getCommissionRate()
        );
    }

    Team(final UUID id, final String name, final LocalDate established, final String headCoach, final String stadium, final BigDecimal commissionRate) {
        super(id);
        this.name = name;
        this.established = established;
        this.headCoach = headCoach;
        this.stadium = stadium;
        this.commissionRate = commissionRate;
    }

    CreateTeamNoticeDto toCreateNoticeDto(final Money funds) {
        final UUID teamId = getId();
        return new CreateTeamNoticeDto(teamId, commissionRate, funds);
    }

    void update(final UpdateTeamDto updateTeamDto) {
        headCoach = updateTeamDto.getHeadCoach();
        stadium = updateTeamDto.getStadium();
        commissionRate = updateTeamDto.getCommissionRate();
    }

    TeamQueryDto toQueryDto() {
        final Year establishedYear = Year.from(established);
        return new TeamQueryDto(name, establishedYear, headCoach, stadium);
    }
}
