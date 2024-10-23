package com.shop.dto;

import com.shop.constant.OrderStatus;
import com.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class OrderHistDto {
    // 주문 히스토리

    public OrderHistDto(Order order){
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
    }  // 생성자

    private Long orderId; //주문아이디
    private String orderDate; //주문날짜
    private OrderStatus orderStatus; //주문 상태

    //주문 상품리스트
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    
    public void addOrderItemDto(OrderItemDto orderItemDto){ // orderItemDto 객체를 주문 상품 리스트에 추가
        orderItemDtoList.add(orderItemDto);
    }
}