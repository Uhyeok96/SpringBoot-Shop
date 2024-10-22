package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name="member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity{

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)  // 유니크 속성 지정
    private String email;   // 회원은 이메일을 통해 유일하게 구분

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)    // enum 타입을 엔티티의 속성으로 지정할 수 있음
    private Role role;  // constant.Role 사용자, 관리자 구분용

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){

        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());  // 패스워드 암호화 처리
        // 스프링 시큐리티 설정 클래스에 BCryptPasswordEncoder Bean을 파라미터로 넘겨서 암호화
        member.setPassword(password);
        member.setRole(Role.ADMIN); // USER 회원계정 생성, ADMIN 관리자계정 생성
        return member;
    }   // 회원 생성용 메서드 (dto와 암호화를 받아 Member 객체 리턴)
}
