package kr.co.jparangdev.jpa_springboot.repository.order.query;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

	private final EntityManager em;

	public List<OrderQueryDto> findOrderQueryDtos() {
		List<OrderQueryDto> result = findOrders();
		result.forEach(o -> {
			List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
			o.setOrderItems(orderItems);
		});
		return result;
	}

	private List<OrderItemQueryDto> findOrderItems(Long orderId) {
		return em.createQuery(
				"SELECT new kr.co.jparangdev.jpa_springboot.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
					+ " FROM OrderItem oi"
					+ " JOIN oi.item i"
					+ " WHERE oi.order.id = :orderId", OrderItemQueryDto.class)
			.setParameter("orderId", orderId)
			.getResultList();
	}

	private List<OrderQueryDto> findOrders() {
		return em.createQuery(
				"SELECT new kr.co.jparangdev.jpa_springboot.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
					+ " FROM Order o"
					+ " JOIN o.member m"
					+ " JOIN o.delivery d", OrderQueryDto.class)
			.getResultList();
	}
}
