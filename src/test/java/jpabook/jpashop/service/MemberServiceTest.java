package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 데이터 변경 시 필요, Rollback 됨
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    @Rollback(false)
    public void 회원가입 () throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);
        // persist할 때 쿼리 나가는 게 아니라, commit - flush 되면서 영속성 컨텍스트에 있던 것 반영하는 쿼리 나감
        // @Rollback(false) 안해두면 rollback 됨
        // 즉 영속성 컨텍스트를 flush 안 하면서 쿼리문이 안 나가게 되는 것

        //then
        assertEquals(member, memberRepository.findOne(savedId)); // 같은 영속성 컨텍스트에서 pk값이 같은 엔티티는 딱 하나로만 관리됨
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        //given

        //when

        //then
    }

}
