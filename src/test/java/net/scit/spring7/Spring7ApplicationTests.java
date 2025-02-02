package net.scit.spring7;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import net.scit.spring7.entity.BoardEntity;
import net.scit.spring7.repository.BoardRepository;

@SpringBootTest
class Spring7ApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Autowired
	BoardRepository repo;
	
	@Test
	void testInsertBoard() {
		String[] w = {"콩쥐" , "팥쥐", "장화","홍련", "햇님", "달님","호랭"};
		String[] c = {"외모 췍!!", 
				 "지금 날 바보로 아는거야?!?!? (쿵떡 쿵떡)", 
				 "영기어마가 텃밭에서 고구마 호박을", 
				 "고구마 호박이 아니라 호박고구마요",
				 "그래! 호구..!!",
				 "ㅋㅋㅋㅋㅋ호구마요ㅋㅋㅋ??? 호.박..", 
				 "호박고구마.. 호박고구마!!! 호.박.고.구.마!!!!",
				 "츠요쿠~ 나레루~ 리유오 시잇따~~",
				 "보쿠오~ 쯔레테~~ 스스메~~~~", 
				 "카와이 다케쟈 다메데스카?",
				 "아타리마에다~!!!!!",
				 "오마이갓 비상사태~ 큰일났다 ㅈ됐다~"					 
		};
		
		for(int i=0; i<30; ++i) {
			int idxW = (int)(Math.random() * w.length);
			int idxC = (int)(Math.random() * c.length);
			
			String writer = w[idxW];
			String content = c[idxC];
			String title = "제목 : " + content.substring(0, 3) + "...";
			
			BoardEntity entity = new BoardEntity();
			entity.setBoardWriter(writer);
			entity.setBoardContent(content);
			entity.setBoardTitle(title);
			
			repo.save(entity);
			
		}
		System.out.println("저장완료~");
	}

}
