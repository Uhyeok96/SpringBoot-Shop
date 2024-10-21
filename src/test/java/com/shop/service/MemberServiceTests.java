package com.shop.service;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional  // 테스트 클래스에 @Transactional 선언 시 테스트 실행 후 롤백 처리됌. 같은 메서드를 반복적으로 테스트 가능
@TestPropertySource(locations="classpath:application-test.properties")
public class MemberServiceTests {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void saveMemberTest(){
        Member member = createMember();
        Member savedMember = memberService.saveMember(member);

        assertEquals(member.getEmail(), savedMember.getEmail());
        assertEquals(member.getName(), savedMember.getName());
        assertEquals(member.getAddress(), savedMember.getAddress());
        assertEquals(member.getPassword(), savedMember.getPassword());
        assertEquals(member.getRole(), savedMember.getRole());
        // Junit의 Assertions 클래스의 assertEquals 메서드를 이용하여 저장하려고 요청했던 값과 실제 저장된 데이터를 비교함.
    }

    @Test
    @DisplayName("중복 회원 가입 테스트")
    public void saveDuplicateMemberTest(){
        Member member1 = createMember();    // 같은 정보의 멤버 객체를 회원가입 테스트
        Member member2 = createMember();
        memberService.saveMember(member1);  // member1 먼저 회원가입

        Throwable e = assertThrows(IllegalStateException.class, () -> { // Junit의 Assertions 클래스의 assertThrows를 이용하면 예외 처리 테스트 가능
            memberService.saveMember(member2);  // member1의 정보와 같기 때문에 회원가입 되지 않고 예외발생
        });

        assertEquals("이미 가입된 회원입니다.", e.getMessage());
    }
}
