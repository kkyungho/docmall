package com.docmall.controller;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docmall.domain.EmailDTO;
import com.docmall.domain.MemberVO;
import com.docmall.service.MemberService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@AllArgsConstructor
@RequestMapping("/member/*")
@Controller
public class MemberController {

	@Inject
	private PasswordEncoder cryptPassEnc;
	
	@Inject
	private MemberService service;
	
	@Inject
	private JavaMailSender mailSender;
	
	// 주요기능 : 회원기능
	
	
	// 회원가입 폼 : /member/join -> jsp파일명
	@GetMapping("/join")
	public void join() {
		
	}
	
	
	// 회원가입저장
	@PostMapping("/join")
	public String joinOk(MemberVO vo, RedirectAttributes rttr) throws Exception{
		
		// 비밀번호(평문) -> 암호화
		// 비밀번호 암호화.(시프링 시큐리티 기능)
		
		vo.setMbsp_password(cryptPassEnc.encode(vo.getMbsp_password()));			
		
		// StringUtils.isEmpty(매개변수) : 매개변수의 값이 널 또는 빈문자열일 경우를 확인하는 기능.
		vo.setMbsp_receive(!StringUtils.isEmpty(vo.getMbsp_receive()) ? "Y" : "N");
		
		log.info("MemberVo: " + vo);
		
		service.join(vo);		
		
		return "redirect:/member/login";
	}
	
	
	// 아이디중복체크
	@ResponseBody
	@GetMapping("/checkID")
	public ResponseEntity<String> checkID(@RequestParam("mbsp_id") String mbsp_id) throws Exception{
		
		String result = "";
		ResponseEntity<String> entity = null;		
		
		result = StringUtils.isEmpty(service.checkID(mbsp_id)) ? "Y" : "N";
		
		entity = new ResponseEntity<String>(result, HttpStatus.OK);
		
		return entity;
		
	}
	
	// 메일인증요청
	@ResponseBody
	@GetMapping("/sendMailAuth")
	public ResponseEntity<String> sendMailAuth(@RequestParam("mbsp_email") String mbsp_email, HttpSession session){
		
		ResponseEntity<String> entity = null;
		
		String authCode = makeAuthCode();
		session.setAttribute("authCode", authCode);
		
		// 인증코드를 세션에 임시적으로 저장
		
		EmailDTO dto = new EmailDTO("docmall", "newcomsa@nate.com", mbsp_email, "docmall 인증메일", authCode);
		
		// 메일내용을 구성하는 클래스
		MimeMessage message = mailSender.createMimeMessage();
		
		
		try {
			// 받는 사람 메일설정
			message.addRecipient(RecipientType.TO, new InternetAddress(mbsp_email));
			// 보내 사람 설정(메일, 이름)
			message.addFrom(new InternetAddress[] {new InternetAddress(dto.getSenderMail(), dto.getSenderName())});
			// 제목
			message.setSubject(dto.getMessage(), "utf-8");
			// 본문내용(인증코드)
			message.setText(dto.getMessage(), "utf-8");
			
			mailSender.send(message);
			
			entity = new ResponseEntity<String>("success", HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			entity = new ResponseEntity<String>("fail", HttpStatus.BAD_REQUEST);
		}
		
		
		
		return entity;
	}
	
	// 메일인증요청확인
	@ResponseBody
	@GetMapping("/mailAuthConfirm")
	public ResponseEntity<String> MailAuthConfirm(@RequestParam("uAuthCode") String uAuthCode, HttpSession session){
		
		ResponseEntity<String> entity = null;		
		
		String authCode = (String) session.getAttribute("authCode");
		
		if(authCode.equals(uAuthCode)) {
			entity = new ResponseEntity<String>("success", HttpStatus.OK);
		}else {
			entity = new ResponseEntity<String>("fail", HttpStatus.OK);
		}			
		
		return entity;
	}
	
	
	// 회원가입시 메일인증코드 생성
	private String makeAuthCode() {
		
		String authCode = "";
		
		for(int i=0; i<6; i++) {
			authCode += String.valueOf((int) (Math.random() * 9) + 1);
		}
			
		return authCode;
	}
	
	// 회원수정 폼	
	@GetMapping("/modify")
	public void modify() {
		
	}
	
	// 회원수정 저장
	
	
	// 로그인  /member/login
	@GetMapping("/login")
	public void login() {
		
	}
	
	// 로그인
	@ResponseBody
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestParam("mbsp_id") String mbsp_id, @RequestParam("mbsp_password") String mbsp_password, HttpSession session) throws Exception{
		
		String result = "";
		ResponseEntity<String> entity = null;	
		
		MemberVO vo = service.login(mbsp_id);
		
		if(vo == null) { // id가 존재안하는 의미.
			result = "ID fail";	 		
		}else { // id가 존재하는 의미.
			
			if(cryptPassEnc.matches(mbsp_password, vo.getMbsp_password())) {
				result = "success";
				
				session.setAttribute("loginStatus", vo); // 로그인 성공 상태정보를 세션으로 저장
			}else {
				result = "PW fail";
			}
		}		
		
		
		entity = new ResponseEntity<String>(result, HttpStatus.OK);
		
		return entity;
		
	}
	
	
	// 마이페이지
	@GetMapping("/mypage")
	public void mypage() {
		
	}
	
	
	
	// 7)아이디 및 비밀번호 찾기
	@GetMapping("/searchIDPW")
	public void searchIDPW() {
		
	}
	
	
}
