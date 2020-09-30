package com.casinoroyale.team;

import java.time.Clock;
import java.time.ZoneOffset;

import javax.validation.ClockProvider;

import com.casinoroyale.team.infrastructure.MoneyDeserializer;
import com.casinoroyale.team.infrastructure.MoneySerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.joda.money.Money;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class TeamApplication {

	public static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.UTC;

	public static void main(String[] args) {
		SpringApplication.run(TeamApplication.class, args);
	}

	@Bean
	ClockProvider clockProvider() {
		return () -> Clock.system(DEFAULT_ZONE_OFFSET);
	}

	@Bean
	ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();

		SimpleModule module = new SimpleModule();
		module.addDeserializer(Money.class, new MoneyDeserializer());
		module.addSerializer(Money.class, new MoneySerializer());
		mapper.registerModule(module);

		final JavaTimeModule javaTimeModule = new JavaTimeModule();
		mapper.registerModule(javaTimeModule);

		return mapper;
	}

}
