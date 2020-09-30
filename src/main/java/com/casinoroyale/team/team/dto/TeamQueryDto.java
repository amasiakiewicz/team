package com.casinoroyale.team.team.dto;

import java.time.Year;

import lombok.Value;

@Value
public class TeamQueryDto {

    String name;

    Year establishedYear;

    String headCoach;

    String stadium;

}
