package kr.co.jparangdev.jpa_springboot.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleOrderDto {
	private Long orderId;
	private String name;
	private LocalDateTime orderDate;
	private OrderStatus orderStatus;
	private Address address;

	public SimpleOrderDto(Order o) {
		orderId = o.getId();
		name = o.getMember().getName();
		orderDate = o.getOrderDate();
		orderStatus = o.getStatus();
		address = o.getDelivery().getAddress();
	}}
