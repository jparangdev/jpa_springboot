package kr.co.jparangdev.jpa_springboot.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.jpa_springboot.domain.Address;
import kr.co.jparangdev.jpa_springboot.domain.Member;
import kr.co.jparangdev.jpa_springboot.domain.Order;
import kr.co.jparangdev.jpa_springboot.domain.OrderStatus;
import kr.co.jparangdev.jpa_springboot.domain.item.Book;
import kr.co.jparangdev.jpa_springboot.exception.NotEnoughStockException;
import kr.co.jparangdev.jpa_springboot.repository.OrderRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

	@Autowired
	EntityManager em;

	@Autowired
	OrderService orderService;

	@Autowired
	OrderRepository orderRepository;

	@Test
	void 상품주문() {
		//given
		Member member = createMember("이정호", new Address("서울", "순화궁로", "????"));

		Book book = createBook("시골 jpa", 10000, 10);

		//when
		int orderCount = 2;
		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

		//then
		Order getOrder = orderRepository.findOne(orderId);

		assertThat(getOrder.getStatus()).as("상품 주문 상태는 ORDER").isEqualTo(OrderStatus.ORDER);
		assertThat(getOrder.getOrderItems().size()).as("주문한 상품수가 정확해야한다").isEqualTo(1);
		assertThat(getOrder.getTotalPrice()).as("주문가격은 수량*가격이다").isEqualTo(book.getPrice() * orderCount);
		assertThat(book.getStockQuantity()).as("주문 수량만큼 재고가 줄어야한다.").isEqualTo(8);

	}

	private Book createBook(String name, int price, int stockQuantity) {
		Book book = new Book();
		book.setName(name);
		book.setPrice(price);
		book.setStockQuantity(stockQuantity);
		em.persist(book);
		return book;
	}

	private Member createMember(String name, Address address) {
		Member member = new Member();
		member.setName(name);
		member.setAddress(address);
		em.persist(member);
		return member;
	}

	@Test
	void 상품주문_재고수량초과() {
		//given
		Member member = createMember("이정호", new Address("서울", "순화궁로", "????"));

		Book book = createBook("시골 jpa", 10000, 10);

		//when
		int orderCount = 11;

		assertThatThrownBy(() -> orderService.order(member.getId(), book.getId(), orderCount))
			.isInstanceOf(NotEnoughStockException.class);

		//then
	}

	@Test
	void 주문취소() {

		//given
		Member member = createMember("이정호", new Address("서울", "순화궁로", "????"));

		Book book = createBook("시골 jpa", 10000, 10);

		//when
		int orderCount = 2;
		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
		orderService.cancelOrder(orderId);

		//then
		Order getOrder = orderRepository.findOne(orderId);

		//then
		assertThat(getOrder.getStatus()).as("상품 주문 상태는 CANCEL").isEqualTo(OrderStatus.CANCEL);
		assertThat(book.getStockQuantity()).as("주문 수량만큼 재고가 줄어야한다.").isEqualTo(10);

	}

}