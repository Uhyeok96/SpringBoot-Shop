package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;


    public Item createItem(){   // 상품 데이터 생성 메서드
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest(){

        Order order = new Order();

        for(int i = 0 ; i < 3 ; i++){
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);   // 아직 영속성 컨텍스트에 저장되지 않은 orderItem 엔티티를 order 엔티티에 담음.
        }

        orderRepository.saveAndFlush(order);    // order 엔티티를 저장하면서 강제로 flush를 호출하여 영속성 컨텍스트에 있는 객체들 데이터베이스에 반영
        em.clear(); // 영속성 컨텍스트의 상태 초기화

        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size());

//        Hibernate:
//        insert
//                into
//        orders
//                (member_id, order_date, order_status, reg_time, update_time, order_id)
//        values
//                (?, ?, ?, ?, ?, ?)
//        Hibernate:
//        insert
//                into
//        order_item
//                (count, item_id, order_id, order_price, reg_time, update_time, order_item_id)
//        values
//                (?, ?, ?, ?, ?, ?, ?)
//        Hibernate:
//        insert
//                into
//        order_item
//                (count, item_id, order_id, order_price, reg_time, update_time, order_item_id)
//        values
//                (?, ?, ?, ?, ?, ?, ?)
//        Hibernate:
//        insert
//                into
//        order_item
//                (count, item_id, order_id, order_price, reg_time, update_time, order_item_id)
//        values
//                (?, ?, ?, ?, ?, ?, ?)
    }

    public Order createOrder(){ // 주문 데이터를 생성해서 저장하는 메서드
        Order order = new Order();

        for(int i=0;i<3;i++){
            Item item = createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = this.createOrder();
        order.getOrderItems().remove(0);    // order 엔티티에서 관리하고 있는 orderItem 리스트의 0번째 인덱스 요소 제거
        em.flush();

//        Hibernate:
//        delete
//                from
//        order_item
//                where
//        order_item_id=?
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder();   // 기존에 만들었던 주문 생성 메서드를 이용하여 주문 데이터 저장
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear(); // 영속성 컨텍스트의 상태 초기화 후

        OrderItem orderItem = orderItemRepository.findById(orderItemId) // 주문상품 아이디를 이용하여 orderItem을 데이터베이스에서 조회
                .orElseThrow(EntityNotFoundException::new);             // 즉시 로딩 : orderItem 엔티티 하나를 조회했을 뿐인데 orderItem, item, orders, member 테이블을 조인해서 한꺼번에 가지고 옴.(성능 저하 문제)
        System.out.println("Order class : " + orderItem.getOrder().getClass());   // orderItem 엔티티에 있는 order 객체의 클래스를 출력. Order class : class com.shop.entity.Order
        System.out.println("=================================");        // @ManyToOne(fetch = FetchType.LAZY) 지연 로딩을 사용한 결과 orderItem 엔티티만 조회함.
        orderItem.getOrder().getOrderDate();                            // Order class : class com.shop.entity.Order$HibernateProxy$8sCPSERW 지연로딩으로 설정하면 실제 엔티티 대신에 프록시 객체를 넣어둠
        System.out.println("=================================");        // 프록시 객체는 실제로 사용되기 전까지 데이터 로딩을 하지 않고, 실제 사용 시점에 조회 쿼리문이 실행.
                                                                        // 주문일(orderDate)을 조회할 때 select 쿼리문이 실행되는 것을 확인.
    }
}
