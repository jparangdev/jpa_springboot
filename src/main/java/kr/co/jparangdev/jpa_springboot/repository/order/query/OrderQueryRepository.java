package kr.co.jparangdev.jpa_springboot.repository.order.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import kr.co.jparangdev.jpa_springboot.domain.OrderItem;
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

	public List<OrderQueryDto> findAllByDto_optimization() {
		List<OrderQueryDto> orders = findOrders();

		List<Long> orderIds = toOrderids(orders);

		Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

		orders.forEach(o->o.setOrderItems(orderItemMap.get(o.getOrderId())));

			return orders;
	}

	private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
		List<OrderItemQueryDto> orderItems = em.createQuery(
			"SELECT new kr.co.jparangdev.jpa_springboot.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
				+ " FROM OrderItem oi"
				+ " JOIN oi.item i"
				+ " WHERE oi.order.id in :orderIds", OrderItemQueryDto.class)
			.setParameter("orderIds", orderIds)
			.getResultList();

		Map<Long, List<OrderItemQueryDto>> orderItemMap =orderItems.stream()
		.collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
		return orderItemMap;
	}

	private List<Long> toOrderids(List<OrderQueryDto> orders) {
		return orders.stream().map(o -> o.getOrderId())
			.distinct().collect(Collectors.toList());
	}

	public List<OrderFlatDto> findAllByDto_flat() {
		return em.createQuery(""
			+ "SELECT new kr.co.jparangdev.jpa_springboot.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)"
			+ " from Order o"
			+ " join o.member m"
			+ " join o.delivery d"
			+ " join o.orderItems oi"
			+ " join oi.item i", OrderFlatDto.class)
			.getResultList();
	}
}
