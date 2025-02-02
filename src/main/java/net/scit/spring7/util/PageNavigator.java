package net.scit.spring7.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageNavigator {

	private final int pagePerGroup = 10;		//그룹 당 페이지 수
	private int pageLimit;						//한 페이지당 글개수
	private int page;							//사용자가 요청한 페이지
	private int totalPages;						//총 페이지 수
	private int totalGroupCount;				//총 그룹수
	private int currentGroup;					//사용자가 요청한 페이지가 속한 그룹
	private int startPageGroup;					//현재 그룹의 첫 페이지
	private int endPageGroup;					//현재 그룹의 마지막 페이지
	
	
	public PageNavigator(int pageLimit, int page, int totalPages) {
		//맴버 초기화
		this.pageLimit = pageLimit;
		this.page = page;
		this.totalPages = totalPages;
		
		//총 그룹수 계산
		//총글개수 152개 이면 16펭지, 2그룹
		totalGroupCount = totalPages / pagePerGroup;
		totalGroupCount += (totalPages % pagePerGroup == 0) ? 0 : 1;  
		System.out.println("총 그룹수 : " + totalGroupCount);   
		
		//사용자가 요청한 페이지의 첫 번쨰, 마지막
		//page : 15, pageLimit : 10 :1
		//15.0 / 10 ==> 1.5  ==> Math.ceil(1.5) ==> 2 ==> 2-1 = 1
		
		// 1~10페이지 요청하면  ==> 1
		// 12 페이지 요청하면  ==> 11
		startPageGroup = ((int)(Math.ceil((double)page / pageLimit)) - 1) * pageLimit + 1;
		System.out.println("첫 페이지 총 그룹수 : " + startPageGroup);   
		
		
		// 1~10 사이의 페이지 요청하면 epg  ==> 10
		// 11~16 사이의 페이지 요청하면 spg ==> 16
		// 3p를 요청했따면 spg = 1, epg = 10
		// 14p를 요청했다면 spg = 11, epg = 20
		endPageGroup = (startPageGroup + pagePerGroup -1) >= totalPages
				 	? totalPages : (startPageGroup + pagePerGroup - 1);
		if(endPageGroup == 0 ) endPageGroup = 1;
		System.out.println("요청한 마지막 (총 그룹수 : " + endPageGroup);   
		
		// 16p를 요청했다면 현재 그룹? 2
		//(16-1) / 10 ==? 1 + 1 ==> 2
		currentGroup = (page - 1) / pagePerGroup + 1;
		System.out.println("현재 (총 그룹수 : " + currentGroup);   
	}
}
