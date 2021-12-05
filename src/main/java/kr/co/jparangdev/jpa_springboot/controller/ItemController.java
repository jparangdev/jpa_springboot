package kr.co.jparangdev.jpa_springboot.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.co.jparangdev.jpa_springboot.domain.item.Book;
import kr.co.jparangdev.jpa_springboot.domain.item.Item;
import kr.co.jparangdev.jpa_springboot.service.ItemService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

	private final ItemService service;

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("form", new BookForm());
		return "items/createItemForm";
	}

	@PostMapping("/new")
	public String create(BookForm form) {
		Book book = new Book();
		book.setIsbn(form.getIsbn());
		book.setName(form.getName());
		book.setStockQuantity(form.getStockQuantity());
		book.setAuthor(form.getAuthor());
		book.setPrice(form.getPrice());

		service.saveItem(book);
		return "redirect:/";
	}

	@GetMapping("")
	public String list(Model model) {
		List<Item> list = service.findItems();
		model.addAttribute("items", list);
		return "items/itemList";
	}
}
