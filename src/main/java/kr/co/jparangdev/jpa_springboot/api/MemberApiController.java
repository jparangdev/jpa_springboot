package kr.co.jparangdev.jpa_springboot.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.jparangdev.jpa_springboot.domain.Member;
import kr.co.jparangdev.jpa_springboot.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

	private final MemberService memberService;

	@PostMapping("/api/v1/members")
	public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}

	@PostMapping("/api/v2/members")
	public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
		Member member = new Member();
		member.setName(request.getName());

		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}

	@PutMapping("/api/v2/members/{id}")
	public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid MemberApiController.UpdateMemberRequest request) {
		memberService.update(id, request.getName());
		Member member = memberService.findMember(id);
		return new UpdateMemberResponse(id, member.getName());
	}

	@GetMapping("/api/v1/members")
	public List<Member> membersV1() {
		return memberService.findAll();
	}

	@GetMapping("/api/v2/members")
	public Result memberV2() {
		List<Member> findMembers = memberService.findAll();
		List<String> members = findMembers.stream().map(Member::getName).collect(Collectors.toList());
		return new Result(members.size(), members);
	}



	@Data
	static class CreateMemberResponse {
		private Long Id;

		public CreateMemberResponse(Long id) {
			this.Id = id;
		}
	}

	@Data
	static class CreateMemberRequest {
		private String name;
	}

	@Data
	@AllArgsConstructor
	static class UpdateMemberResponse {
		private Long id;
		private String name;
	}

	@Data
	static class UpdateMemberRequest {
		private String name;
	}

	@Data
	@AllArgsConstructor
	static class Result<T> {
		private int count;
		private T data;
	}

	@Data
	@AllArgsConstructor
	static class MemberDto {
		String name;
	}
}
