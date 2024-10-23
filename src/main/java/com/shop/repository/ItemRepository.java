package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {

    List<Item> findByItemNm(String itemNm); // 상품명으로 조회

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);  // 상품명과 상품 상세 설명을 or 조건 이용 검색하는 쿼리 메소드
    
    List<Item> findByPriceLessThan(Integer price);  // 파라미터로 넘어온 price 변수보다 값이 작은 상품 데이터 조회(LessThan)

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);  // 상품의 가격이 높은 순으로 조회(OrderBy Desc)
    
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);    // @Param 어노테이션을 이용하여 파라미터로 넘어온 값을 JPQL에 들어갈 변수로 지정
                                                                            // itemDetail 변수를 "like % %" 사이에 ":itemDetail" 로 값이 들어가도록 작성

    @Query(value = "select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail); // nativeQuery = true -> JPA가 아니라 엔티티를 사용하지 않음 -> 순수한 쿼리문

}
