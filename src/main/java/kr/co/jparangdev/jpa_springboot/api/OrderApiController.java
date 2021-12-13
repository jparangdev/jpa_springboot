package kr.co.jparangdev.jpa_springboot.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.jparangdev.jpa_springboot.domain.Order;
import kr.co.jparangdev.jpa_springboot.domain.OrderSearch;
import kr.co.jparangdev.jpa_springboot.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

	private final OrderRepository orderRepository;

	@GetMapping("/api/v1/simple-orders")
	public List<Order> orderV1() {
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		for (Order order : orders) {
			order.getMember().getName(); // lazy 강제 초기화
			order.getDelivery().getStatus();
		}
		return orders;
	}

}
