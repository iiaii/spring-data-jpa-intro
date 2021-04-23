package me.iiaii.springdatajpaintro.repository;

import me.iiaii.springdatajpaintro.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
