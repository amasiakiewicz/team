package com.casinoroyale.team.team.domain;

import com.casinoroyale.team.player.domain.PlayerFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
class TeamConfiguration {

    @Bean
    TeamFacade teamFacade(
            final TeamRepository teamRepository, final KafkaTemplate<Object, Object> kafkaTemplate, final PlayerFacade playerFacade
    ) {
        return new TeamFacade(teamRepository, kafkaTemplate, playerFacade);
    }

}
