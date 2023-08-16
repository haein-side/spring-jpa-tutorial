package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "해인씨!!!");
        return "hello"; // 화면 이름 : resources > templates > 화면 이름 : 결과가 렌더링됨!
    }



}
