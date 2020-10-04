package com.casinoroyale.team.player.domain;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface PlayerRepository extends JpaRepository<Player, UUID> {

}
