package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {
    //화면에서 넘어올 때 Validation(memberForm)과 실제 Domain이 원하는 게 다를 수 있으므로 Member 이외에 생성해줌
    //실무에선 input 값 받을 때 복잡함!
    //엔티티는 화면 종속적인 기능은 Form으로 빼놓자
    //JPA 쓸 때 조심해야 하는 것 : Entity를 최대한 순수하게 유지해야 함
    //오직 핵심 비즈니스 로직에만 dependency 있게

    //entity : 실제 테이블과 매핑되는 클래스로 함부로 변경하면 X
    //DTO : 계층 간 데이터 교환을 위한 객체 - Entity는 그대로 두고 DTO를 화면에 전송할 것

    @NotEmpty(message = "회원 이름은 필수입니다.")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
