package com.casinoroyale.team.team.dto;

import java.time.Year;
import java.util.UUID;

import lombok.Value;

@Value
public class TeamQueryDto {

    UUID teamId;

    String name;

    Year establishedYear;

    String headCoach;

    String stadium;

}
