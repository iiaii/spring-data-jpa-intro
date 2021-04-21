package me.iiaii.springdatajpaintro.repository;

import me.iiaii.springdatajpaintro.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
