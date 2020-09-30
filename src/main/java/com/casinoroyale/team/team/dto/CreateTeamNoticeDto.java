package com.casinoroyale.team.team.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.casinoroyale.team.infrastructure.MoneyDeserializer;
import com.casinoroyale.team.infrastructure.MoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.money.Money;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTeamNoticeDto {

    UUID teamId;

    BigDecimal commissionRate;

    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    Money funds;

}
