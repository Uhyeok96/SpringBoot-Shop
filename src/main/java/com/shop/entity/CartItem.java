package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cart_item")
public class CartItem extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // 하나의 장바구니에는 여러 개의 상품을 담을 수 있으므로 다대일 관계 매핑
    @JoinColumn(name = "cart_id")   // foreign key (cart_id) references cart(card_id)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)  // 하나의 상품은 여러 장바구니에 담길 수 있으므로 다대일 관계 매핑
    @JoinColumn(name = "item_id")   // foreign key (item_id) references item(item_id)
    private Item item;

    private int count;  // 같은 상품을 장바구니에 몇 개 담을지 저장

//    Hibernate:
//    create table cart_item (
//            cart_item_id bigint not null,
//            count integer not null,
//            cart_id bigint,
//            item_id bigint,
//            primary key (cart_item_id)
//    ) engine=InnoDB
//    Hibernate:
//    create sequence cart_item_seq start with 1 increment by 50 nocache
//    Hibernate:
//    alter table if exists cart_item
//    add constraint FK1uobyhgl1wvgt1jpccia8xxs3
//    foreign key (cart_id)
//    references cart (cart_id)
//    Hibernate:
//    alter table if exists cart_item
//    add constraint FKdljf497fwm1f8eb1h8t6n50u9
//    foreign key (item_id)
//    references item (item_id)

    public static CartItem createCartItem(Cart cart, Item item, int count) {  // 331 추가 장바구니용
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    public void addCount(int count){

        this.count += count;
    }  // 장바구니에 기존 담겨 있는 상품인데, 해당 상품을 추가한경우 누적

    // 장바구니에 담겨있는 상품의 수량 변경
    public void updateCount(int count){  // 351 추가
        this.count = count;
    }
}
