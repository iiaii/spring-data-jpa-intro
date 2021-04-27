package me.iiaii.springdatajpaintro.repository;

import me.iiaii.springdatajpaintro.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> findMemberCustom();
}
