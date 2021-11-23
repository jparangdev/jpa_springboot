package kr.co.jparangdev.jpa_springboot.controller;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {
	@NotEmpty()
	private String name;
	private String city;
	private String street;
	private String zipCode;
}