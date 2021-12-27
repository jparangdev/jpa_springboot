package kr.co.jparangdev.jpa_springboot.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.jparangdev.jpa_springboot.domain.Address;
import kr.co.jparangdev.jpa_springboot.domain.Order;
import kr.co.jparangdev.jpa_springboot.domain.OrderItem;
import kr.co.jparangdev.jpa_springboot.domain.OrderSearch;
import kr.co.jparangdev.jpa_springboot.domain.OrderStatus;
import kr.co.jparangdev.jpa_springboot.domain.item.Item;
import kr.co.jparangdev.jpa_springboot.repository.OrderRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

	private final OrderRepository orderRepository;

	@GetMapping("/api/v1/orders")
	public List<Order> orderV1() {
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		for (Order order : orders) {
			order.getMember().getName();
			order.getDelivery().getAddress().getCity();
			List<OrderItem> orderItems = order.getOrderItems();
			orderItems.forEach(oi->oi.getItem().getName());
		}
		return orders;
	}

	/*
	* 모두 fetch.Lazy로 걸려있기 때문에 1+n+n+n... 문제가 발생한다.
	* 컬랙션을 쓰게되면 쿼리가 많이 발생하게 된다.
	* */
	@GetMapping("/api/v2/orders")
	public List<OrderDto> orderV2() {
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		List<OrderDto> list = orders.stream()
			.map(o -> OrderDto.from(o))
			.collect(Collectors.toList());
		return list;
	}

	@Getter
	static class OrderDto {

		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;
		private List<OrderItemDto> orderItems = new ArrayList<>();

		public OrderDto(Long orderId, String name, LocalDateTime orderDate,
			OrderStatus orderStatus, Address address,
			List<OrderItem> orderItems) {
			this.orderId = orderId;
			this.name = name;
			this.orderDate = orderDate;
			this.orderStatus = orderStatus;
			this.address = address;
			this.orderItems = orderItems.stream().map(oi->OrderItemDto.from(oi)).collect(Collectors.toList());
		}

		public static OrderDto from(Order o) {
			return new OrderDto(o.getId(),o.getMember().getName(), o.getOrderDate(), o.getStatus(), o.getDelivery()
				.getAddress(), o.getOrderItems());
		}
	}

	@Getter
	static class OrderItemDto {

		private String itemName;
		private int orderPrice;
		private int count;

		public OrderItemDto(String itemName, int orderPrice, int count) {
			this.itemName = itemName;
			this.orderPrice = orderPrice;
			this.count = count;
		}

		public static OrderItemDto from(OrderItem oi) {
			return new OrderItemDto(oi.getItem().getName(), oi.getOrderPrice(), oi.getOrderPrice());

		}
	}
}
