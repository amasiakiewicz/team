package com.casinoroyale.team.team.domain;

import static javax.persistence.AccessType.FIELD;
import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.casinoroyale.team.infrastructure.BaseEntity;
import com.casinoroyale.team.team.dto.CreateTeamDto;
import com.casinoroyale.team.team.dto.CreateTeamNoticeDto;
import com.casinoroyale.team.team.dto.TeamQueryDto;
import com.casinoroyale.team.team.dto.UpdateTeamDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.joda.money.Money;

@Entity
@Access(FIELD)
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@ToString
class Team extends BaseEntity {

    private String name;

    private LocalDate established;

    @Columns(columns = { @Column(name = "fundsCurrency"), @Column(name = "fundsAmount") })
    @Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmountAndCurrency")
    private Money funds;

    private String headCoach;

    private String stadium;

    private BigDecimal commissionRate;

    static Team create(final CreateTeamDto createTeamDto) {
        final LocalDate established = createTeamDto.getEstablishedYear().atDay(1);
        return new Team(
                createTeamDto.getName(),
                established,
                createTeamDto.getFunds(),
                createTeamDto.getHeadCoach(),
                createTeamDto.getStadium(),
                createTeamDto.getCommissionRate()
        );
    }

    void update(final UpdateTeamDto updateTeamDto) {
        headCoach = updateTeamDto.getHeadCoach();
        stadium = updateTeamDto.getStadium();
        commissionRate = updateTeamDto.getCommissionRate();
    }

    void updateFunds(final Money funds) {
        this.funds = funds;
    }

    CreateTeamNoticeDto toCreateNoticeDto(final Money funds) {
        final UUID teamId = getId();
        return new CreateTeamNoticeDto(teamId, commissionRate, funds);
    }

    TeamQueryDto toQueryDto() {
        final UUID teamId = getId();
        final Year establishedYear = Year.from(established);
        final BigDecimal commissionRateStriped = commissionRate.stripTrailingZeros();

        return new TeamQueryDto(teamId, name, establishedYear, funds, headCoach, stadium, commissionRateStriped);
    }
}
