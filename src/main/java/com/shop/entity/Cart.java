package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "cart")
@Getter
@Setter
@ToString
public class Cart extends BaseEntity{

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)   // 1:1 매핑 (회원과 카트)
    @JoinColumn(name = "member_id") // 조인은 회원의 id와(매핑할 외래키 foreign key(member_id) references member )
    // name을 명시하지 않으면 JPA가 알아서 ID를 찾지만 컬럼명이 원하는 대로 생성되지 않을 수 있다.
    private Member member;

//    Hibernate:
//    create table cart (
//            cart_id bigint not null,
//            member_id bigint,
//            primary key (cart_id)
//    ) engine=InnoDB
//    Hibernate:
//    alter table if exists cart
//    drop index if exists UK_7dds3r67nkhxm9sbs9r5obd46
//    Hibernate:
//    alter table if exists cart
//    add constraint UK_7dds3r67nkhxm9sbs9r5obd46 unique (member_id)
//    Hibernate:
//    create sequence cart_seq start with 1 increment by 50 nocache
//    Hibernate:
//    alter table if exists cart
//    add constraint FKix170nytunweovf2v9137mx2o
//    foreign key (member_id)
//    references member (member_id)
}
