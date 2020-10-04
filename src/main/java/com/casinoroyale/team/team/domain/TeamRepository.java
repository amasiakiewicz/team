package com.casinoroyale.team.team.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface TeamRepository extends JpaRepository<Team, UUID> {

    @Query(
            value = "select distinct t.* from team t join player p on t.id = p.team_id where p.id in ?1",
            countQuery = "select count(distinct t.*) from team t join player p on t.id = p.team_id where p.id in ?1",
            nativeQuery = true
    )
    Page<Team> findAllByPlayerIdIn(final Iterable<UUID> playerIds, final Pageable pageable);

    boolean existsByName(final String name);

    Optional<Team> findByName(final String name);
}
