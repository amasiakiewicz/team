package com.casinoroyale.team.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class InfrastructureConfiguration {

    @Bean
    IllegalExceptionHandler illegalExceptionHandler() {
        return new IllegalExceptionHandler();
    }

}
