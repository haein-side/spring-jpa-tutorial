package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    @Transactional // 어노테이션 추가
    @Rollback(false) // 테스트 후 롤백 방지
    public void testMember() throws Exception {
        //given
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Long savedId = memberRepository.save(member); // 쿼리 동작
        Member findMember = memberRepository.find(savedId); // 쿼리 동작 X (영속성 컨텍스트에서 동일 엔티티 가져옴)
        // [error] No EntityManager with actual transaction available for current thread - cannot reliably process 'persist' call;
        // 엔티티 매니저를 통한 모든 데이터 변경은 항상 트랜잭션 안에서 이루어져야

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member); // 같은 트랜잭션 안에서 저장 및 조회하므로 영속성 컨텍스트 같음
        // 같은 id값을 가지면 같은 엔티티로 인식
        // insert 쿼리만 나가서 영속성 컨텍스트에 담아둔 걸 select 쿼리 안 보내고 바로 가져옴

        System.out.println("findMember == member " + (findMember == member));
    }
}