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
        // ?????? ??????
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // ????????? ?????? ??????
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // ????????? ??????
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // ?????? ??????
        memberRepository.delete(member1);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(1);

        member2.setUsername("!@#!@#");
    }

    @Test
    @DisplayName("findByUsernameAndAgeGreaterThan")
    public void findByUsernameAndAgeGreaterThan() throws Exception {
        // given
        // ????????? ?????? ????????? ?????? ???
        Member aaa = new Member("aaa", 10);
        Member bbb = new Member("aaa", 20);
        memberRepository.save(aaa);
        memberRepository.save(bbb);

        // when???
        // ????????? ?????? ????????? age ?????? ?????? ?????? ??????
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
        Optional<Member> result2 = memberRepository.findOptionalByUsername("AAA");  // ????????? 2??? ???????????? exception ??????

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

        // ?????? jpql ????????? ???????????? ??? ?????? ????????? ??????????????? ????????? ????????? ?????? db??? ???????????????

        // when
        int resultCount = memberRepository.bulkAgePlus(20);

        // ?????? ????????? ????????? ??????????????? ???????????? ???????????? ????????? ????????? ?????? ????????????
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
        List<Member> all = memberRepository.findAll();  // @EntityGraph ???????????? ???????????? ??????

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
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername()); // Dirty Checking (????????????)??? ???????????? ??????
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
        List<Member> lockByUsername = memberRepository.findLockByUsername(member1.getUsername());// Dirty Checking (????????????)??? ???????????? ??????

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
    
    @Test
    @DisplayName("projections")
    public void projections() throws Exception {
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
        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");

        System.out.println("======================");
        for (UsernameOnly usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly.getUsername());
        }

        // then
        
    }

    @Test
    @DisplayName("projections2")
    public void projections2() throws Exception {
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
        List<UsernameOnlyDto> result = memberRepository.findProjections2ByUsername("m1");

        System.out.println("======================");
        for (UsernameOnlyDto usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly.getUsername());
        }

        // then

    }

    @Test
    @DisplayName("dynamicProjection")
    public void dynamicProjection() throws Exception {
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
        List<UsernameOnlyDto> result = memberRepository.findDynamicProjectionsByUsername("m1", UsernameOnlyDto.class); // ????????? ????????? ???????????? ?????? ??????

        System.out.println("======================");
        for (UsernameOnlyDto usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly.getUsername());
        }

        // then

    }

    @Test
    @DisplayName("closedProjection")
    public void closedProjection() throws Exception {
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
        List<NestedClosedProjections> result = memberRepository.findDynamicProjectionsByUsername("m1", NestedClosedProjections.class); // ????????? ????????? ???????????? ?????? ??????

        System.out.println("======================");
        for (NestedClosedProjections usernameOnly : result) {
            System.out.println("username = " + usernameOnly.getUsername());
            System.out.println("team name = " + usernameOnly.getTeam().getName());
        }

        // then

    }

    @Test
    @DisplayName("nativeQuery")
    public void nativeQuery() throws Exception {
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
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection.getUsername() = " + memberProjection.getUsername());
            System.out.println("memberProjection.getTeamname() = " + memberProjection.getTeamname());
        }

        // then

    }
}
