package com.casinoroyale.team.team.dto;

import static lombok.AccessLevel.NONE;

import java.math.BigDecimal;
import java.time.Year;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.joda.money.Money;

@Data
public class CreateTeamDto {

    @NotBlank(message = "name.required")
    private String name;

    @NotNull(message = "establishedYear.required")
    @Past(message = "establishedYear.past")
    private Year establishedYear;

    @NotNull(message = "funds.required")
    private Money funds;

    @Valid
    @Getter(NONE)
    @Setter(NONE)
    private UpdateTeamDto updateTeamDto;

    public CreateTeamDto() {
        updateTeamDto = new UpdateTeamDto();
    }

    public String getHeadCoach() {
        return updateTeamDto.getHeadCoach();
    }

    public void setHeadCoach(final String headCoach) {
        updateTeamDto.setHeadCoach(headCoach);
    }

    public String getStadium() {
        return updateTeamDto.getStadium();
    }

    public void setStadium(final String stadium) {
        updateTeamDto.setStadium(stadium);
    }

    public BigDecimal getCommissionRate() {
        return updateTeamDto.getCommissionRate();
    }

    public void setCommissionRate(final BigDecimal commissionRate) {
        updateTeamDto.setCommissionRate(commissionRate);
    }

}
