package kr.co.jparangdev.jpa_springboot.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

	@GetMapping("/{itemId}/edit")
	public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
		Book item = (Book)service.findItem(itemId);
		BookForm form = new BookForm();
		form.setId(item.getId());
		form.setName(item.getName());
		form.setPrice(item.getPrice());
		form.setStockQuantity(item.getStockQuantity());
		form.setAuthor(item.getAuthor());
		form.setIsbn(item.getIsbn());

		model.addAttribute("form", form);
		return "items/updateItemForm";
	}

	@PostMapping("/{itemId}/edit")
	public String updateItem(@ModelAttribute("form") BookForm form, @PathVariable String itemId) {

		Book book = new Book();

		book.setId(form.getId());
		book.setName(form.getName());
		book.setPrice(form.getPrice());
		book.setStockQuantity(form.getStockQuantity());
		book.setAuthor(form.getAuthor());
		book.setIsbn(form.getIsbn());

		service.saveItem(book);
		return "redirect:/items";
	}
}
