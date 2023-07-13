package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import static org.assertj.core.api.Assertions.*;
import static study.querydsl.Entity.QMember.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.querydsl.Entity.Member;
import study.querydsl.Entity.Team;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }
    @Test
    public void startJPQL() {
        Member findMember = (Member) em.createQuery("select m from Member m where m.username = :username")
                .setParameter("username", "member1")
                .getSingleResult();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQeurydsl() {
//        QMember m = new QMember("m");
        Member findMember = queryFactory
                .select(member)  // static import라고 QMember 클래스를 select(member)식으로 넣을 수 있음
                .from(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)),
                        member.age.eq(30)) // .and와 , 중 2개를 선택해서 사용 가능
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl2() {
        //member1을 찾아라.
//        QMember m = new QMember("m");
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void resultFetch() {
        // fetch 란? : 리스트 조회, 데이터 없으면 null 리스트 반환
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();   // 여러개 조회
        Member fetchOne = queryFactory
                .selectFrom(member)
                .fetchOne();    // 한가지만 조회
        Member fetchFirst = queryFactory
                .selectFrom(member)
                .limit(1)       // 첫번째부터 몇번 째 까지만 조회 설정
                .fetchFirst();  // 첫번째만 조회
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();    // 페이징 정보 포함

        results.getTotal();
        List<Member> content = results.getResults();

        long total = queryFactory
                .selectFrom(member)
                .fetchCount();
    }

    // 회원 정렬 순서
    // 나이는 내림차순(desc)
    // 이름 올림차순 (asc)
    // 2에서 회원 이름이 없으면 마지막에 출력 (nulls last)
    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("Member5", 100));
        em.persist(new Member("Member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    public void paging() {
       QueryResults<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)  // 기본적으로 시작은 0부터 시작함
                .limit(2)   // 1 ~ 2번 순서까지 가져옴
                .fetchResults();   // 전체 조회는 fetchResults()를 사용하면 된다

       assertThat(result.getTotal()).isEqualTo(2);
       assertThat(result.getLimit()).isEqualTo(2);
        assertThat(result.getOffset()).isEqualTo(1);
        assertThat(result.getResults().size()).isEqualTo(2);
    }

}
