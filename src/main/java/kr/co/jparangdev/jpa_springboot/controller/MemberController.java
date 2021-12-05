package kr.co.jparangdev.jpa_springboot.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.co.jparangdev.jpa_springboot.domain.Address;
import kr.co.jparangdev.jpa_springboot.domain.Member;
import kr.co.jparangdev.jpa_springboot.service.MemberService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

	private final MemberService service;

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("memberForm", new MemberForm());
		return "members/createMemberForm";
	}

	@PostMapping("/new")
	public String create(@Valid MemberForm memberForm, BindingResult result) {

		if (result.hasErrors()) {
			return "members/createMemberForm";
		}

		Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());

		Member member = new Member();
		member.setName(memberForm.getName());
		member.setAddress(address);

		service.join(member);
		return "redirect:/";
	}

	@GetMapping("")
	public String list(Model model) {
		List<Member> members = service.findAll();
		model.addAttribute("members", members);
		return "members/memberList";
	}
}
