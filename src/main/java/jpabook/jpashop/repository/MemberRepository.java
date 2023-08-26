package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    @PersistenceContext
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
        // 처음엔 영속성 컨텍스트에 member 넣어두고 transaction이 commit 날라가는 시점에 db에 반영
        // Key 값으로 PK가 그대로 들어감
        // 동시에 private Long id에 값이 들어감
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id); // 단건조회
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class) // JPQL : Member 엔티티를 대상으로 함
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
