package kr.co.jparangdev.jpa_springboot.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.jpa_springboot.domain.Member;
import kr.co.jparangdev.jpa_springboot.repository.MemberRepository;

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
class MemberServiceTest {

	@Autowired
	MemberService memberService;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	EntityManager em;

	@Test
	void 회원가입() {
		//given
		Member member = new Member();
		member.setName("kim");

		//when
		Long savedId = memberService.join(member);

		//then
		assertThat(member)
			.isEqualTo(memberService.findMember(savedId));
	}

	@Test
	void 중복_회원_확인() {
		//given
		Member member1 = new Member();
		member1.setName("kim1");

		Member member2 = new Member();
		member2.setName("kim1");

		//when

		//then
		assertThatThrownBy(() -> {
			memberService.join(member1);
			memberService.join(member2);
		}).isInstanceOf(IllegalStateException.class)
			.hasMessage("이미 존재하는 회원입니다.");

	}

}