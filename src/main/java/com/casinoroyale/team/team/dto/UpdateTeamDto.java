package com.casinoroyale.team.team.dto;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeamDto {

    @NotBlank(message = "headCoach.required")
    private String headCoach;

    @NotBlank(message = "stadium.required")
    private String stadium;

    @NotNull(message = "commissionRate.required")
    @Digits(integer = 1, fraction = 4, message = "commissionRate.format")
    @DecimalMin(value = "0.0", message = "commissionRate.min")
    @DecimalMax(value = "1.0", message = "commissionRate.max")
    private BigDecimal commissionRate;

}
