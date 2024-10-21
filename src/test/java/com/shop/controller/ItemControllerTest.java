package com.shop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemControllerTest {
    
    @Autowired
    MockMvc mockMvc;
    
    @Test
    @DisplayName("상품 등록 페이지 권한 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")  // 현재 회원의 이름이 admin이고 role이 ADMIN인 유저가 로그인된 상태로 테스트
    public void itemFormTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new"))  // 상품 등록 페이지 get 요청
                .andDo(print()) // 요청과 응답 메시지를 콘솔창으로 출력하여 확인
                .andExpect(status().isOk());    // 응답 상태 코드가 정상인지 확인
//        MockHttpServletResponse:
//        Status = 200
//        Error message = null
    }

    @Test
    @DisplayName("상품 등록 페이지 일반 회원 접근 테스트")
    @WithMockUser(username = "user", roles = "USER")
    public void itemFormNotAdminTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new"))
                .andDo(print())
                .andExpect(status().isForbidden()); // Forbidden 예외가 발생하면 테스트 성공
//        MockHttpServletResponse:
//        Status = 403
//        Error message = Forbidden
    }
}
