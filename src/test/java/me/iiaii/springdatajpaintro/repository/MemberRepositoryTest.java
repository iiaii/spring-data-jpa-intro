package me.iiaii.springdatajpaintro.repository;

import me.iiaii.springdatajpaintro.dto.MemberDto;
import me.iiaii.springdatajpaintro.entity.Member;
import me.iiaii.springdatajpaintro.entity.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("testMember")
    public void testMember() throws Exception {
        // given
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    @DisplayName("basic CRUD")
    public void basicCRUD() throws Exception {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        // then
        // 멤버 검증
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(1);

        member2.setUsername("!@#!@#");
    }

    @Test
    @DisplayName("findByUsernameAndAgeGreaterThan")
    public void findByUsernameAndAgeGreaterThan() throws Exception {
        // given
        // 이름은 같고 나이는 다른 멤
        Member aaa = new Member("aaa", 10);
        Member bbb = new Member("aaa", 20);
        memberRepository.save(aaa);
        memberRepository.save(bbb);

        // when버
        // 이름은 같고 나이는 age 보다 작은 멤버 조회
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("aaa", 15);

        // then
        assertThat(result.get(0).getUsername()).isEqualTo("aaa");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("testQuery")
    public void testQuery() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> findUser = memberRepository.findUser("AAA", 10);

        // then
        assertThat(findUser.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("find username list")
    public void findUsernameList() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<String> usernameList = memberRepository.findUsernameList();

        // then
        assertThat(usernameList.get(0)).isEqualTo("AAA");
        assertThat(usernameList.get(1)).isEqualTo("BBB");
    }

    @Test
    @DisplayName("findMemberDto")
    public void findMemberDto() throws Exception {
        // given
        Team team = new Team("TeamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        // when
        MemberDto memberDto = memberRepository.findMemberDto().get(0);

        // then
        assertThat(memberDto.getUsername()).isEqualTo("AAA");
        assertThat(memberDto.getTeamName()).isEqualTo("TeamA");
    }

    @Test
    @DisplayName("findByNames")
    public void findByNames() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> names = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        // then
        assertThat(names.get(0).getUsername()).isEqualTo("AAA");
        assertThat(names.get(1).getUsername()).isEqualTo("BBB");
    }

    @Test
    @DisplayName("returnType")
    public void returnType() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> result = memberRepository.findListByUsername("AAA");
        Member result1 = memberRepository.findMemberByUsername("AAA");
        Optional<Member> result2 = memberRepository.findOptionalByUsername("AAA");  // 결과가 2개 이상이면 exception 발생

        // then
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result1.getUsername()).isEqualTo("AAA");
        assertThat(result2.get().getUsername()).isEqualTo("AAA");
    }

    @Test
    @DisplayName("Paging")
    public void Paging() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

//        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        // then
        List<Member> members = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(members.size()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    @DisplayName("Slice")
    public void Slice() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member1", 20));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member1", 40));

        String name = "member1";
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "age"));

        // when
        Slice<Member> page = memberRepository.findByUsername(name, pageRequest);

        // then
        List<Member> members = page.getContent();

        assertThat(members.size()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isFalse();
    }

    @Test
    @DisplayName("bulkUpdate")
    public void bulkUpdate() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // 사실 jpql 쿼리를 실행하기 전 이전 영속성 컨텍스트의 내용을 플러시 해서 db에 적용해준다

        // when
        int resultCount = memberRepository.bulkAgePlus(20);

        // 벌크 연산은 영속성 컨텍스트에 적용되지 않으므로 데이터 갱신을 위해 비워준다
//        em.flush();
//        em.clear();

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    @DisplayName("findMemberLazy")
    public void findMemberLazy() throws Exception {
        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findMemberFetchJoin();
        List<Member> all = memberRepository.findAll();  // @EntityGraph 페치조인 간편하게 사용

        // then

    }

    @Test
    @DisplayName("queryHint")
    public void queryHint() throws Exception {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername()); // Dirty Checking (변경감지)가 발생하지 않음
        findMember.setUsername("member2");

        em.flush();

        // then

    }

    @Test
    @DisplayName("lock")
    public void lock() throws Exception {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        List<Member> lockByUsername = memberRepository.findLockByUsername(member1.getUsername());// Dirty Checking (변경감지)가 발생하지 않음

        em.flush();

        // then

    }

    @Test
    @DisplayName("callCustom")
    public void callCustom() throws Exception {
        // given
        List<Member> result = memberRepository.findMemberCustom();

        // when


        // then

    }

    @Test
    @DisplayName("queryByExample")
    public void queryByExample() throws Exception {
        // given
        Team team = new Team("teamA");
        em.persist(team);

        Member m1 = new Member("m1", 0, team);
        Member m2 = new Member("m2", 0, team);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        Member member = new Member("m1");

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);
        List<Member> result = memberRepository.findAll(example);

        // then
        assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }
}
