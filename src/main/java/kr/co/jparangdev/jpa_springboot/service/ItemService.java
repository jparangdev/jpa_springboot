package kr.co.jparangdev.jpa_springboot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.jpa_springboot.domain.item.Item;
import kr.co.jparangdev.jpa_springboot.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;

	@Transactional
	public void saveItem(Item item) {
		itemRepository.save(item);
	}

	public List<Item> findItems() {
		return itemRepository.findAll();
	}

	public Item findItem(Long itemId) {
		return itemRepository.findOne(itemId);
	}
}
