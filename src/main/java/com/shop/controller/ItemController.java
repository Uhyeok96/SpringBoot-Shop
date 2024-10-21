package com.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ItemController {

    // 상품등록 페이지 (관리자)
    @GetMapping(value = "/admin/item/new")
    public String itemForm(){
        return "/item/itemForm";
    }
}
