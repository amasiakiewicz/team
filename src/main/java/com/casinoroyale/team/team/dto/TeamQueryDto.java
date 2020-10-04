package com.casinoroyale.team.team.dto;

import java.math.BigDecimal;
import java.time.Year;
import java.util.UUID;

import lombok.Value;
import org.joda.money.Money;

@Value
public class TeamQueryDto {

    UUID teamId;

    String name;

    Year establishedYear;

    Money funds;

    String headCoach;

    String stadium;

    BigDecimal commissionRate;

}
