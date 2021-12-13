package kr.co.jparangdev.jpa_springboot;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.jpa_springboot.domain.Address;
import kr.co.jparangdev.jpa_springboot.domain.Delivery;
import kr.co.jparangdev.jpa_springboot.domain.Member;
import kr.co.jparangdev.jpa_springboot.domain.Order;
import kr.co.jparangdev.jpa_springboot.domain.OrderItem;
import kr.co.jparangdev.jpa_springboot.domain.item.Book;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitDB {

	private final InitService initService;

	@PostConstruct
	public void init() {
		initService.dbInit1();
		initService.dbInit2();
	}


	@Component
	@Transactional
	@RequiredArgsConstructor
	static class InitService {
		private final EntityManager em;
		public void dbInit1() {
			Member member = new Member();
			member.setName("userA");
			member.setAddress(new Address("seoul","tehe","12333"));
			em.persist(member);

			Book book = getBook("JPA1 BOOK", 10000, 100);
			em.persist(book);

			Book book2 = getBook("JPA2 BOOK", 20000, 100);
			em.persist(book2);


			OrderItem orderItem1 = OrderItem.createOrderItem(book, 10000, 1);
			OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

			Delivery delivery = getDelivery(member);
			Order order = Order.creatOrder(member, delivery, orderItem1, orderItem2);
			em.persist(order);
		}

		public void dbInit2() {
			Member member = new Member();
			member.setName("userB");
			member.setAddress(new Address("busan","se","14433"));
			em.persist(member);

			Book book = getBook("SPRING1 BOOK", 20000, 300);
			em.persist(book);

			Book book2 = getBook("SPRING2 BOOK", 40000, 300);
			em.persist(book2);


			OrderItem orderItem1 = OrderItem.createOrderItem(book, 20000, 3);
			OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

			Delivery delivery = getDelivery(member);
			Order order = Order.creatOrder(member, delivery, orderItem1, orderItem2);
			em.persist(order);
		}

		private Delivery getDelivery(Member member) {
			Delivery delivery = new Delivery();
			delivery.setAddress(member.getAddress());
			return delivery;
		}

		private Book getBook(String name, int price, int stockQuantity) {
			Book book2 = new Book();
			book2.setName(name);
			book2.setPrice(price);
			book2.setStockQuantity(stockQuantity);
			return book2;
		}

	}
}


