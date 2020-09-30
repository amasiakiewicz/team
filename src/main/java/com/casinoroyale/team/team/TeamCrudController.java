package com.casinoroyale.team.team;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import com.casinoroyale.team.team.domain.TeamFacade;
import com.casinoroyale.team.team.dto.CreateTeamDto;
import com.casinoroyale.team.team.dto.TeamQueryDto;
import com.casinoroyale.team.team.dto.UpdateTeamDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teams")
class TeamCrudController {

    private final TeamFacade teamFacade;

    @Autowired
    TeamCrudController(final TeamFacade teamFacade) {
        this.teamFacade = teamFacade;
    }

    @GetMapping
    Page<TeamQueryDto> findTeams(
            @RequestParam final Set<UUID> teamIds,
            final Pageable pageable
    ) {
        return teamFacade.findTeams(teamIds, pageable);
    }

    @ResponseStatus(CREATED)
    @PostMapping
    UUID createTeam(@Valid @RequestBody final CreateTeamDto createTeamDto) {
        return teamFacade.createTeam(createTeamDto);
    }

    @PutMapping("/{teamId}")
    void updateTeam(
            @PathVariable final UUID teamId,
            @Valid @RequestBody final UpdateTeamDto updateTeamDto) {
        teamFacade.updateTeam(teamId, updateTeamDto);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{teamId}")
    void deleteTeam(@PathVariable final UUID teamId) {
        teamFacade.deleteTeam(teamId);
    }

}
