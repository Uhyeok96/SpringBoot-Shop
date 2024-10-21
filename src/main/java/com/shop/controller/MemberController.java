package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/members") // http://localhost:80/members
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    // 회원가입 페이지
    @GetMapping(value = "/new") // http://localhost:80/members/new
    public String memberForm(Model model){  // 회원가입 페이지
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    // 회원가입 정보 post 방식으로 전달
    @PostMapping(value = "/new")    // http://localhost:80/members/new
    public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){
        // spring-boot-starter-validation를 활용한 검증 bindingResult객체 추가
        if(bindingResult.hasErrors()){  // 폼에 입력된 데이터들을 검증 처리
            return "member/memberForm";
            // 검증 후 결과를 bindingResult에 담아 준다.
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        }catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage()); // 가입 처리시 이메일이 중복이면 메시지를 전달한다.
            return "member/memberForm";
        }

        return "redirect:/";
    }
    
    // 로그인 페이지 불러옴
    @GetMapping(value = "/login")   // http://localhost:80/members/login
    public String loginMember(){
        return "/member/memberLoginForm";
    }
    
    // 로그인 실패 시 다시 로그인 페이지
    @GetMapping(value = "/login/error") // http://localhost:80/members/login/error
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }
}
