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

	/**
	* jpa의 distinct를 이용하면 우리가 원하는 컬렉션 데이터 조회가 가능하다
	 * 디비의 distinct키워드를 날려주고 jpa에서 중복을 걸러서 컬렉션에 담아준다. 3중일땐 어떨까??
	 * 다만 페이징이 불가능하다 ㅡㅡ 페치조인을 하는 순간 페이징을 이용한 쿼리가 안된다.
	 * 강제로 시행하는 경우 데이터를 다가지고 와서 메모리에서 페이징을 진행하기 때문에 성능상 이슈가 있다.
	 * 3중인 경우 데이터의 정합성이 안맞을 수 있다. 그러니 컬렉션 패치조인은 딱 하나만 쓰자!
	*/
	@GetMapping("/api/v3/orders")
	public List<OrderDto> orderV3() {
		List<Order> orders = orderRepository.findAllWithItem();
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
