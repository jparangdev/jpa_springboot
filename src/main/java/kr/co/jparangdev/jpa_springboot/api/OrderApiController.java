package kr.co.jparangdev.jpa_springboot.api;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.jparangdev.jpa_springboot.domain.Address;
import kr.co.jparangdev.jpa_springboot.domain.Order;
import kr.co.jparangdev.jpa_springboot.domain.OrderItem;
import kr.co.jparangdev.jpa_springboot.domain.OrderSearch;
import kr.co.jparangdev.jpa_springboot.domain.OrderStatus;
import kr.co.jparangdev.jpa_springboot.repository.OrderRepository;
import kr.co.jparangdev.jpa_springboot.repository.order.query.OrderFlatDto;
import kr.co.jparangdev.jpa_springboot.repository.order.query.OrderItemQueryDto;
import kr.co.jparangdev.jpa_springboot.repository.order.query.OrderQueryDto;
import kr.co.jparangdev.jpa_springboot.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

	private final OrderRepository orderRepository;

	private final OrderQueryRepository queryRepository;

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


	/**
	* default_bath_fetch_size 를 통한 컬렉션 조인 페이징 해결
	 * 약 100~500 정도로 두고 사용한다.
	 * 만약 클래스별로 별도로 사이즈를 두고싶다면 @BatchSize 를 설정해주도록 한다.
	 * XtoOne 관계는 데이터 수에 영향을 주지 않기때문에 페치조인을 통해 쿼리수를 줄일 수 있다.
	 * 나머지는 배치사이즈를 이용하자
	*/
	@GetMapping("/api/v3.1/orders")
	public List<OrderDto> orderV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
		@RequestParam(value = "limit", defaultValue = "100") int limit) {
		List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
		List<OrderDto> list = orders.stream()
			.map(o -> OrderDto.from(o))
			.collect(Collectors.toList());
		return list;
	}

	/**
	* toOne 관계는 조인을 통해 같이 조회 해주고
	 * otMany는 쿼리를 별도로 만들어 반복을 통해 따로 조회를 한다.
	 * 하지만 이것도 결국 N+1
	*/
	@GetMapping("/api/v4/orders")
	public List<OrderQueryDto> ordersV4() {
		return queryRepository.findOrderQueryDtos();
	}

	/**
	 * In 절을 만들이서 파라미터를 넣어서 조회를 해준다... 이것도 맞을까?
	 *
	*/
	@GetMapping("/api/v5/orders")
	public List<OrderQueryDto> ordersV5() {
		return queryRepository.findAllByDto_optimization();
	}

	@GetMapping("/api/v5/orders")
	public List<OrderQueryDto> ordersV6() {
		List<OrderFlatDto> flat = queryRepository.findAllByDto_flat();
		return flat.stream()
			.collect(groupingBy(o-> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
				mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
			)).entrySet().stream()
			.map(e-> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey()
				.getOrderStatus(), e.getKey().getAddress(), e.getValue()))
			.collect(toList());
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
