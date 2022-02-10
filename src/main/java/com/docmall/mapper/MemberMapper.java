package com.docmall.mapper;

import com.docmall.domain.MemberVO;

public interface MemberMapper {

	public int join(MemberVO vo); 
	
	// ID 중복체크
	public String checkID(String mbsp_id);
	
	public MemberVO login(String mbsp_id);
}
