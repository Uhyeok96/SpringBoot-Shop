package com.shop.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional  // 에러 발생 시 변경된 데이터를 로직 수행 전으로 콜백 시켜줌
@RequiredArgsConstructor    // 빈 주입 방법 = @Autowired, 필드 주입(Setter 주입), 생성자 주입
// @RequiredArgsConstructor -> final이나 @NonNull이 붙은 필드에 생성자 생성
public class MemberService implements UserDetailsService {
    // implements UserDetailsService : 데이터베이스에서 화원 정보를 가져오는 역할을 담당
    // loadUserByUsername를 제정의하여 활용함 리턴은 UserDetails 인터페이스로 반환
    // UserDetails : 시큐리티에서 회원의 정보를 담기 위해서 사용되는 인터페이스 User 클래스를 사용함.
    // User 클래스는 UserDetails 인터페이스를 구현하고 있는 클래스임.

    private final MemberRepository memberRepository;

    public Member saveMember(Member member){
        validateDuplicateMember(member);    // 이메일 검증을 받고 (아래 메서드 실행)
        return memberRepository.save(member);   // 회원 저장
    }

    private void validateDuplicateMember(Member member){    // 이메일 중복 검사
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
            //IllegalStateException -> 사용자가 값을 제대로 입력했지만, 개발자 코드가 값을 처리할 준비가 안된 경우에 발생한다.
            //예를 들어, 로또 게임 진행 후 게임이 종료된 상태에서 사용자가 추가 진행을 위해 금액을 입력하는 경우.
            //이미 로또 게임 로직이 종료되었기 때문에 사용자의 입력에 대응할 수 없다.
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{

        Member member = memberRepository.findByEmail(email);
        // 이메일을 받아 찾아오고 Member 객체로 담음

        if(member == null){ // member에 값이 비어 있으면 없는 회원으로 예외 발생
            throw new UsernameNotFoundException(email);
        }

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
}
