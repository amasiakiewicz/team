package com.casinoroyale.team.player.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class PlayerConfiguration {

    @Bean
    PlayerFacade playerFacade(final PlayerRepository playerRepository) {
        return new PlayerFacade(playerRepository);
    }

}
