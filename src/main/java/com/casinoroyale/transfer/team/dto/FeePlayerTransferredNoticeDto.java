package com.casinoroyale.transfer.team.dto;

import static com.google.common.base.Preconditions.checkArgument;

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
public class FeePlayerTransferredNoticeDto {

    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    Money sellerTeamFunds;

    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    Money buyerTeamFunds;

    UUID sellerTeamId;

    UUID buyerTeamId;

    UUID playerId;

    public Money getSellerTeamFunds() {
        checkArgument(sellerTeamFunds != null);

        return sellerTeamFunds;
    }

    public Money getBuyerTeamFunds() {
        checkArgument(buyerTeamFunds != null);

        return buyerTeamFunds;
    }

    public UUID getSellerTeamId() {
        checkArgument(sellerTeamId != null);

        return sellerTeamId;
    }

    public UUID getBuyerTeamId() {
        checkArgument(buyerTeamId != null);

        return buyerTeamId;
    }

    public UUID getPlayerId() {
        checkArgument(playerId != null);

        return playerId;
    }

}
