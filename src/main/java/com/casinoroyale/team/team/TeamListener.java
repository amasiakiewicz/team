package com.casinoroyale.team.team;

import com.casinoroyale.team.team.domain.TeamFacade;
import com.casinoroyale.transfer.team.dto.FeePlayerTransferredNoticeDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class TeamListener {

    private final TeamFacade teamFacade;

    TeamListener(final TeamFacade teamFacade) {
        this.teamFacade = teamFacade;
    }

    @KafkaListener(topics = "FeeAndPlayerTransferred")
    public void listenTransferred(ConsumerRecord<String, FeePlayerTransferredNoticeDto> kafkaMessage) {
        final FeePlayerTransferredNoticeDto feePlayerTransferredNoticeDto = kafkaMessage.value();
        teamFacade.updateFunds(feePlayerTransferredNoticeDto);
    }

}
