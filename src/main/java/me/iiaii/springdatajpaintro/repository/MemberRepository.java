package me.iiaii.springdatajpaintro.repository;

import me.iiaii.springdatajpaintro.dto.MemberDto;
import me.iiaii.springdatajpaintro.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new me.iiaii.springdatajpaintro.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username); // 컬렉션

    Member findMemberByUsername(String username); // 단건

    Optional<Member> findOptionalByUsername(String username); // 단건 O

    Page<Member> findByAge(int age, Pageable pageable);

    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")
    Slice<Member> findByUsername(String username, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //    @EntityGraph(attributePaths = {"team"}) // 페치 조인 쿼리 없이도 페치 조
    @Query("select m from Member m left join fetch m.team t")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);

    List<UsernameOnlyDto> findProjections2ByUsername(@Param("username") String username);

    // 동적 projection
    <T> List<T> findDynamicProjectionsByUsername(@Param("username") String username, Class<T> type);

    // 반환 타입이나, 모든 필드를 적어야 해서 안쓰는게 좋다
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName" +
            "from member m left join team t",
    countQuery = "select count(*) from member", nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
