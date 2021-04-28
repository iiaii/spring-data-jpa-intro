package me.iiaii.springdatajpaintro.controller;

import lombok.RequiredArgsConstructor;
import me.iiaii.springdatajpaintro.entity.Member;
import me.iiaii.springdatajpaintro.repository.MemberRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // 조회용으로만 사용해야 함 (영속성 컨텍스트에 등록되지 않아서 변경 감지가 일어나지 않는다)
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @PostConstruct
    public void init() {
        memberRepository.save(new Member("userA"));
    }
}
