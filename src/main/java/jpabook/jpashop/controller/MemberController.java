package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) { //@NotEmpty같은 validation 해줌!, BindingResult 안에 오류 담김

        if (result.hasErrors()) {
            return "members/createMemberForm"; //BindingResult를 통해 어떤 에러가 있는지 화면에 뿌릴 수 있음
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode()); //MemberForm 안에서 만들어도 됨!

        Member member = new Member();
        member.setName(form.getName()); //중간에 정제
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/"; //PRG 패턴 : 저장 후 재로딩되면 안 좋기 때문에 redirect를 많이 씀! - 첫번째 페이지로 감 (멱등성 보장)
    }

    @GetMapping("/members")
    public String list(Model model) { //API를 만들 땐 : 반드시 Entity를 DTO로 변환해서 화면에 꼭 필요한 데이터들만 전송할 것 (엔티티 변동 시, API 스팩이 변해버리기 때문)
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }

}
