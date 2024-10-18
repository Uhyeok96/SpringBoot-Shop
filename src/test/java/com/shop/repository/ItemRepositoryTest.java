package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // 스프링 부트 테스트
@TestPropertySource(locations = "classpath:application-test.properties")
// application-test.properties에 우선순위를 부여하여 mariadb 대신 H2 데이터베이스를 사용하게 함.
class ItemRepositoryTest {
    
    @Autowired  // ItemRepository를 사용하기 위해서 Bean 주입
    ItemRepository itemRepository;
    
    @PersistenceContext // 영속성 컨텍스트를 사용하기 위함(db연결용)
    EntityManager em;   // EntityManager 빈 주입

    @Test
    @DisplayName("상품 저장 테스트")   // 테스트 실행 시 지정한 테스트명 노출
    public void createItemTest(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item); // Jpa를 이용해 insert 처리
        System.out.println(savedItem.toString());   // 출력

//        Hibernate:
//        insert
//                into
//        item
//                (item_detail, item_nm, item_sell_status, price, reg_time, stock_number, update_time, item_id)
//        values
//                (?, ?, ?, ?, ?, ?, ?, ?)
//        Item(id=1, itemNm=테스트 상품, price=10000, stockNumber=100, itemDetail=테스트 상품 상세 설명, itemSellStatus=SELL, regTime=2024-10-18T12:54:47.947654500, updateTime=2024-10-18T12:54:47.947654500)
    }

    public void createItemList(){   // 아래 메서드에서 호출하여 실행됨
        for(int i=1;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100); item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest(){
        this.createItemList();  // 48행 메서드 불러와 데이터 생성
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        for(Item item : itemList){
            System.out.println(item.toString());
        }

//        Hibernate:
//        select
//        i1_0.item_id,
//                i1_0.item_detail,
//                i1_0.item_nm,
//                i1_0.item_sell_status,
//                i1_0.price,
//                i1_0.reg_time,
//                i1_0.stock_number,
//                i1_0.update_time
//        from
//        item i1_0
//        where
//        i1_0.item_nm=?
//        Item(id=1, itemNm=테스트 상품1, price=10001, stockNumber=100, itemDetail=테스트 상품 상세 설명1, itemSellStatus=SELL, regTime=2024-10-18T13:05:39.123612, updateTime=2024-10-18T13:05:39.123612)
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for(Item item : itemList){
            System.out.println(item.toString());
        }

//        Hibernate:
//        select
//        i1_0.item_id,
//                i1_0.item_detail,
//                i1_0.item_nm,
//                i1_0.item_sell_status,
//                i1_0.price,
//                i1_0.reg_time,
//                i1_0.stock_number,
//                i1_0.update_time
//        from
//        item i1_0
//        where
//        i1_0.item_nm=?
//        or i1_0.item_detail=?
//        Item(id=1, itemNm=테스트 상품1, price=10001, stockNumber=100, itemDetail=테스트 상품 상세 설명1, itemSellStatus=SELL, regTime=2024-10-18T13:12:28.798636, updateTime=2024-10-18T13:12:28.798636)
//        Item(id=5, itemNm=테스트 상품5, price=10005, stockNumber=100, itemDetail=테스트 상품 상세 설명5, itemSellStatus=SELL, regTime=2024-10-18T13:12:28.902510, updateTime=2024-10-18T13:12:28.902510)
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }

//        Hibernate:
//        select
//        i1_0.item_id,
//                i1_0.item_detail,
//                i1_0.item_nm,
//                i1_0.item_sell_status,
//                i1_0.price,
//                i1_0.reg_time,
//                i1_0.stock_number,
//                i1_0.update_time
//        from
//        item i1_0
//        where
//        i1_0.price<?
//                Item(id=1, itemNm=테스트 상품1, price=10001, stockNumber=100, itemDetail=테스트 상품 상세 설명1, itemSellStatus=SELL, regTime=2024-10-18T13:14:57.827920, updateTime=2024-10-18T13:14:57.827920)
//        Item(id=2, itemNm=테스트 상품2, price=10002, stockNumber=100, itemDetail=테스트 상품 상세 설명2, itemSellStatus=SELL, regTime=2024-10-18T13:14:57.952580, updateTime=2024-10-18T13:14:57.952580)
//        Item(id=3, itemNm=테스트 상품3, price=10003, stockNumber=100, itemDetail=테스트 상품 상세 설명3, itemSellStatus=SELL, regTime=2024-10-18T13:14:57.954574, updateTime=2024-10-18T13:14:57.954574)
//        Item(id=4, itemNm=테스트 상품4, price=10004, stockNumber=100, itemDetail=테스트 상품 상세 설명4, itemSellStatus=SELL, regTime=2024-10-18T13:14:57.963924, updateTime=2024-10-18T13:14:57.963924)
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }

//        Hibernate:
//        select
//        i1_0.item_id,
//                i1_0.item_detail,
//                i1_0.item_nm,
//                i1_0.item_sell_status,
//                i1_0.price,
//                i1_0.reg_time,
//                i1_0.stock_number,
//                i1_0.update_time
//        from
//        item i1_0
//        where
//        i1_0.price<?
//                order by
//        i1_0.price desc
//        Item(id=4, itemNm=테스트 상품4, price=10004, stockNumber=100, itemDetail=테스트 상품 상세 설명4, itemSellStatus=SELL, regTime=2024-10-18T13:17:30.125705, updateTime=2024-10-18T13:17:30.125705)
//        Item(id=3, itemNm=테스트 상품3, price=10003, stockNumber=100, itemDetail=테스트 상품 상세 설명3, itemSellStatus=SELL, regTime=2024-10-18T13:17:30.119721, updateTime=2024-10-18T13:17:30.119721)
//        Item(id=2, itemNm=테스트 상품2, price=10002, stockNumber=100, itemDetail=테스트 상품 상세 설명2, itemSellStatus=SELL, regTime=2024-10-18T13:17:30.106621, updateTime=2024-10-18T13:17:30.107752)
//        Item(id=1, itemNm=테스트 상품1, price=10001, stockNumber=100, itemDetail=테스트 상품 상세 설명1, itemSellStatus=SELL, regTime=2024-10-18T13:17:29.906164, updateTime=2024-10-18T13:17:29.906164)
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for(Item item : itemList){
            System.out.println(item.toString());
        }

//        Hibernate:
//        select
//        i1_0.item_id,
//                i1_0.item_detail,
//                i1_0.item_nm,
//                i1_0.item_sell_status,
//                i1_0.price,
//                i1_0.reg_time,
//                i1_0.stock_number,
//                i1_0.update_time
//        from
//        item i1_0
//        where
//        i1_0.item_detail like ? escape ''
//        order by
//        i1_0.price desc
//        Item(id=10, itemNm=테스트 상품10, price=10010, stockNumber=100, itemDetail=테스트 상품 상세 설명10, itemSellStatus=SELL, regTime=2024-10-18T14:27:09.888503, updateTime=2024-10-18T14:27:09.888503)
//        Item(id=9, itemNm=테스트 상품9, price=10009, stockNumber=100, itemDetail=테스트 상품 상세 설명9, itemSellStatus=SELL, regTime=2024-10-18T14:27:09.879421, updateTime=2024-10-18T14:27:09.879421)
//        Item(id=8, itemNm=테스트 상품8, price=10008, stockNumber=100, itemDetail=테스트 상품 상세 설명8, itemSellStatus=SELL, regTime=2024-10-18T14:27:09.879421, updateTime=2024-10-18T14:27:09.879421)
//        Item(id=7, itemNm=테스트 상품7, price=10007, stockNumber=100, itemDetail=테스트 상품 상세 설명7, itemSellStatus=SELL, regTime=2024-10-18T14:27:09.867744, updateTime=2024-10-18T14:27:09.867744)
//        Item(id=6, itemNm=테스트 상품6, price=10006, stockNumber=100, itemDetail=테스트 상품 상세 설명6, itemSellStatus=SELL, regTime=2024-10-18T14:27:09.867744, updateTime=2024-10-18T14:27:09.867744)
//        Item(id=5, itemNm=테스트 상품5, price=10005, stockNumber=100, itemDetail=테스트 상품 상세 설명5, itemSellStatus=SELL, regTime=2024-10-18T14:27:09.862923, updateTime=2024-10-18T14:27:09.862923)
//        Item(id=4, itemNm=테스트 상품4, price=10004, stockNumber=100, itemDetail=테스트 상품 상세 설명4, itemSellStatus=SELL, regTime=2024-10-18T14:27:09.853646, updateTime=2024-10-18T14:27:09.853646)
//        Item(id=3, itemNm=테스트 상품3, price=10003, stockNumber=100, itemDetail=테스트 상품 상세 설명3, itemSellStatus=SELL, regTime=2024-10-18T14:27:09.850835, updateTime=2024-10-18T14:27:09.850835)
//        Item(id=2, itemNm=테스트 상품2, price=10002, stockNumber=100, itemDetail=테스트 상품 상세 설명2, itemSellStatus=SELL, regTime=2024-10-18T14:27:09.844880, updateTime=2024-10-18T14:27:09.844880)
//        Item(id=1, itemNm=테스트 상품1, price=10001, stockNumber=100, itemDetail=테스트 상품 상세 설명1, itemSellStatus=SELL, regTime=2024-10-18T14:27:09.722121, updateTime=2024-10-18T14:27:09.722121)
    }

    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    public void findByItemDetailByNative(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
        // 위 메서드 findByItemDetailTest()와 같은 결과 출력
    }
    
    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queryDslTest(){
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);                         // JPAQueryFactory를 이용하여 쿼리를 동적 생성
        QItem qItem = QItem.item;                                                       // Querydsl로 자동 생성된 객체 생성
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)                           // select * from Item
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))                    // where itemSellStatus=SELL And
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))   // itemDetail = *테스트 상품 상세 설명*
                .orderBy(qItem.price.desc());                                           // orderBy price 내림차순
                                                                                        // 자바 소스이지만 sql문과 유사
        List<Item> itemList = query.fetch();                                            // 위에 만든 쿼리 실행하여 list로 받음
        // List<T> fetch() : 조회 결과 리스트 반환
        // T fetchOne : 조회 대상이 1건인 경우 제네릭으로 지정한 타입 반환
        // T fetchFirst() : 조회 대상 중 1건만 반환
        // Long fetchCount() : 조회 대상 개수 반환
        // QueryResult<T> fetchResult() : 조회한 리스트와 전체 개수를 포함한 QueryResult 반환

        for(Item item : itemList){
            System.out.println(item.toString());
        }
//        Hibernate:
//        select
//        i1_0.item_id,
//                i1_0.item_detail,
//                i1_0.item_nm,
//                i1_0.item_sell_status,
//                i1_0.price,
//                i1_0.reg_time,
//                i1_0.stock_number,
//                i1_0.update_time
//        from
//        item i1_0
//        where
//        i1_0.item_sell_status=?
//        and i1_0.item_detail like ? escape '!'
//        order by
//        i1_0.price desc
    }

    public void createItemList2(){  // 1~5번은 판매중, 6~10번은 품절 상태 데이터 생성, 아래 메서드에서 호출하여 사용
        for(int i=1;i<=5;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);    // 5개는 판매중 
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for(int i=6;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);       // 5개는 판매완료
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2(){

        this.createItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder();   // 쿼리에 들어갈 조건을 만들어주는 빌더 Predicate를 구현(메서드 체인형식)
        QItem item = QItem.item;                                // 쿼리dsl로 객체 item 생성
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStatus = "SELL";

        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));
        booleanBuilder.and(item.price.gt(price));

        if(StringUtils.equals(itemSellStatus, ItemSellStatus.SELL)){
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0, 5);
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);
        System.out.println("total elements : " + itemPagingResult.getTotalElements());  // Predicate를 이용해서 검색된 객체 수 알아옴

        List<Item> resultItemList = itemPagingResult.getContent();
        for(Item resultItem : resultItemList){
            System.out.println(resultItem.toString());
        }

//        Hibernate:
//        select
//        i1_0.item_id,
//                i1_0.item_detail,
//                i1_0.item_nm,
//                i1_0.item_sell_status,
//                i1_0.price,
//                i1_0.reg_time,
//                i1_0.stock_number,
//                i1_0.update_time
//        from
//        item i1_0
//        where
//        i1_0.item_detail like ? escape '!'
//        and i1_0.price>?
//                and i1_0.item_sell_status=?
//        offset
//                ? rows
//        fetch
//        first ? rows only
//        total elements : 2
//        Item(id=4, itemNm=테스트 상품4, price=10004, stockNumber=100, itemDetail=테스트 상품 상세 설명4, itemSellStatus=SELL, regTime=2024-10-18T15:38:33.042139, updateTime=2024-10-18T15:38:33.042139)
//        Item(id=5, itemNm=테스트 상품5, price=10005, stockNumber=100, itemDetail=테스트 상품 상세 설명5, itemSellStatus=SELL, regTime=2024-10-18T15:38:33.046126, updateTime=2024-10-18T15:38:33.046126)
    }


}