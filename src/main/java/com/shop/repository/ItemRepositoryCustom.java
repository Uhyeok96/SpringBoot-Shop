package com.shop.repository;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    // 상품 조회 조건을 담고 있는 itemSearchDto,  페이징 처리가 되는 pageable을 파라미터로 받음
    // 리턴 타입은 Page<객체>

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    // QueryDsl에서 제공하는 @QueryProjection 기능 사용
}
