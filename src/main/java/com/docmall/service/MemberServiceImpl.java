package com.docmall.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.docmall.domain.MemberVO;
import com.docmall.mapper.MemberMapper;

import lombok.Setter;

@Service
public class MemberServiceImpl implements MemberService {

	@Setter(onMethod_= @Autowired)
	private MemberMapper mapper;
	
	@Override
	public int join(MemberVO vo) {
		
		return mapper.join(vo);
	}

	// ID중복체크
	@Override
	public String checkID(String mbsp_id) {
		
		return mapper.checkID(mbsp_id);
	}

	@Override
	public MemberVO login(String mbsp_id) {
		
		return mapper.login(mbsp_id);
	}

}
