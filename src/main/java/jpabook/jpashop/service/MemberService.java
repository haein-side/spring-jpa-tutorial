package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // JPA 데이터 변경은 다 트랜잭션 안에서 일어나야 함, JPA가 조회 성능 최적화
@RequiredArgsConstructor // final 필드인 애들 생성자 만들어줌
public class MemberService {

    private final MemberRepository memberRepository; // 컴파일 시점에 에러 확인 가능하므로 final

    /**
    * 회원 가입
    */
    @Transactional // 메소드가 우선권 : readOnly default false
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId(); // em.persist(member); 영속성 컨텍스트에 PK가 key로 들어가 있음.
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 단건 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name); // 영속성 컨텍스트에 올라와있는 객체는 변경감지 (after flush and commit)
    }
}
