package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers(); // 엔티티에 있는 정보가 다 노출됨 -> 필드에 @JsonIgnore하면 사라지긴 함 but 다른 api에서 해당 엔티티 사용 시 문제 생김
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDTO> collect = findMembers.stream()
                .map(m -> new MemberDTO(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect); // data 필드의 값으로 list가 나감 - list를 바로 컬렉션으로 내놓으면 JSON 배열 타입으로 나가기 때문에 유연성이 확 떨어짐
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDTO {
        private String name; // 딱 노출할 것만 스팩에 노출하는 것
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        // 엔티티와 API의 스택이 1:1 매핑되면 안 됨
        // API 스팩을 위한 별도의 DTO를 만들어야
        // 엔티티를 외부에서 바인딩해서 그대로 쓰면 안 됨
        // API 만들 때는 엔티티를 파라미터로 만들면 안 되고 외부에 노출해서도 안 됨
        // 엔티티를 들어가보지 않는 이상, API 문서 까보지 않는 이상 Member에서 어느 값이 파라미터로 넘어오는지 모름
        // 만약 Member 엔티티에서 username으로 필드명 변경 시, 요청하는 api 스펙에서 JSON을 만들 때 name이 아니라 username으로 모두 변경해줘야 함 (API 스팩이 변동됨)
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName()); // 엔티티 변경 시 여기서 컴파일 오류가 남 -> 수정 시 api는 영향 안 받음 // 엔티티 변경해도 API의 스팩이 변하지 않는다!

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberResponseV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        // 수정 시 변경감지를 사용하자!
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id); // command와 query를 분리한다.
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {

        @NotEmpty // 각각의 API에서 요구하는 validation들을 DTO에 따로 넣으면 됨! -> Entity에 넣는 것보다 나음 (Entity는 여러 곳에서 사용)
        private String name;
    }


    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

}
