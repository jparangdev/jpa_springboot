package kr.co.jparangdev.jpa_springboot.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.jparangdev.jpa_springboot.domain.Order;
import kr.co.jparangdev.jpa_springboot.domain.OrderSearch;
import kr.co.jparangdev.jpa_springboot.repository.order.simpleQuery.OrderSimpleQueryDto;
import kr.co.jparangdev.jpa_springboot.domain.SimpleOrderDto;
import kr.co.jparangdev.jpa_springboot.repository.OrderRepository;
import kr.co.jparangdev.jpa_springboot.repository.order.simpleQuery.OrderSimpleQueryRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

	private final OrderRepository orderRepository;
	private final OrderSimpleQueryRepository orderSimpleQueryRepository;

	@GetMapping("/api/v1/simple-orders")
	public List<Order> orderV1() {
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		for (Order order : orders) {
			order.getMember().getName(); // lazy 강제 초기화
			order.getDelivery().getStatus();
		}
		return orders;
	}

	@GetMapping("/api/v2/simple-orders")
	public List<SimpleOrderDto> ordersV2() {
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		List<SimpleOrderDto> resultList = orders.stream()
			.map(SimpleOrderDto::new)
			.collect(Collectors.toList());
		return resultList;
	}

	@GetMapping("/api/v3/simple-orders")
	public List<SimpleOrderDto> ordersV3() {
		List<Order> orders = orderRepository.findAllWithMemberDelivery();
		List<SimpleOrderDto> resultList = orders.stream()
			.map(SimpleOrderDto::new)
			.collect(Collectors.toList());
		return resultList;
	}

	@GetMapping("/api/v4/simple-orders")
	public List<OrderSimpleQueryDto> ordersV4() {
		return orderSimpleQueryRepository.findOrderDtos();
	}

}
