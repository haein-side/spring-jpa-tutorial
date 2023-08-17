package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {
    //화면에서 넘어올 때 Validation(memberForm)과 실제 Domain이 원하는 게 다를 수 있으므로 Member 이외에 생성해줌
    //실무에선 input 값 받을 때 복잡함!

    @NotEmpty(message = "회원 이름은 필수입니다.")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
