package kr.co.jparangdev.jpa_springboot.repository.order.simpleQuery;

import java.time.LocalDateTime;

import kr.co.jparangdev.jpa_springboot.domain.Address;
import kr.co.jparangdev.jpa_springboot.domain.Order;
import kr.co.jparangdev.jpa_springboot.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSimpleQueryDto {
	private Long orderId;
	private String name;
	private LocalDateTime orderDate;
	private OrderStatus orderStatus;
	private Address address;

	public OrderSimpleQueryDto(Order o) {
		orderId = o.getId();
		name = o.getMember().getName();
		orderDate = o.getOrderDate();
		orderStatus = o.getStatus();
		address = o.getDelivery().getAddress();
	}

	public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate,
		OrderStatus orderStatus, Address address) {
		this.orderId = orderId;
		this.name = name;
		this.orderDate = orderDate;
		this.orderStatus = orderStatus;
		this.address = address;
	}
}
