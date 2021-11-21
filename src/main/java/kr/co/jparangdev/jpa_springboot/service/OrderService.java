package kr.co.jparangdev.jpa_springboot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.jpa_springboot.domain.Delivery;
import kr.co.jparangdev.jpa_springboot.domain.Member;
import kr.co.jparangdev.jpa_springboot.domain.Order;
import kr.co.jparangdev.jpa_springboot.domain.OrderItem;
import kr.co.jparangdev.jpa_springboot.domain.item.Item;
import kr.co.jparangdev.jpa_springboot.repository.ItemRepository;
import kr.co.jparangdev.jpa_springboot.repository.MemberRepository;
import kr.co.jparangdev.jpa_springboot.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final MemberRepository memberRepository;
	private final ItemRepository itemRepository;

	// 주문

	/**
	 * 주문
	 * */
	@Transactional
	public Long order(Long memberId, Long itemId, int count) {
		Member member = memberRepository.findOne(memberId);
		Item item = itemRepository.findOne(itemId);

		Delivery delivery = new Delivery();
		delivery.setAddress(member.getAddress());

		OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

		Order order = Order.creatOrder(member, delivery, orderItem);

		orderRepository.save(order);
		return order.getId();
	}

	// 취소
	// 검색
}
