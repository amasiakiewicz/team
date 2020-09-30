package com.casinoroyale.team.team.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface TeamRepository extends JpaRepository<Team, UUID> {

    Page<Team> findAllByIdInOrderByName(final Iterable<UUID> ids, final Pageable pageable);

    boolean existsByName(final String name);

    Optional<Team> findByName(final String name);
}
