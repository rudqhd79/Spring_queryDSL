package study.querydsl;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.test.annotation.Commit;
import study.querydsl.Entity.QTestEntity;
import study.querydsl.Entity.TestEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Commit
class QuerydslApplicationTests {

	@Autowired
	EntityManager em;
/*
	@Test
	void contextLoads() {
		TestEntity testEntity = new TestEntity();
		em.persist(testEntity);
		JPAQueryFactory query = new JPAQueryFactory(em);
		QTestEntity QHello = QTestEntity.testEntity; // Querydsl Q타입 동작 확인
		TestEntity result = (TestEntity) query.selectFrom(QHello).fetchOne();
		assertThat(result).isEqualTo(testEntity);
		// lombok 동작 확인 (hello.getId())
		assertThat(result.getId()).isEqualTo(testEntity.getId());
	}
*/
}
