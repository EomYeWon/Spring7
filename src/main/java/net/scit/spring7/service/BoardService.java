package net.scit.spring7.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.spring7.dto.BoardDTO;
import net.scit.spring7.entity.BoardEntity;
import net.scit.spring7.repository.BoardRepository;
import net.scit.spring7.util.FileService;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

	private final BoardRepository boardRepository;
	
	@Value("${spring.servlet.multipart.location}")
	private String uploadPath;
	
	@Value("${user.board.pageLimit}")
	private int pageLimit;
	
	/*
	 * 1) 단순조회: 게시글 전체 목록 조회
	 * 2) 검색조회: 쿼리 메소드 사용
	 * 3) 페이징 추
	 * 
	 * @param searchWord : boardTitle findByBoardTitleContains("쥐")
	 * @param searcItem : 
	 * 3
	 * 
	 */
	
	
	public Page<BoardDTO> selectAll(Pageable pageable, String searchItem, String searchWord) {
		//-1을 한 이유 : DB의 Page는 0부터 시작함. 사용자는 1을 요청하기 떄문에!!		
		//사용자가 요청한 페이지 번호
		int pageNumber = pageable.getPageNumber()-1;
		
		
		Page<BoardEntity> temp = null;
	
		
		//검색하는거.
		switch (searchItem) {
			case"boardWriter":
				temp = boardRepository.findByBoardWriterContains(
						searchWord, 
						PageRequest.of(pageNumber, pageLimit,Sort.Direction.DESC,"createDate" ));
				break;
			case"boardTitle":
				temp = boardRepository.findByBoardTitleContains(
						searchWord, 
						PageRequest.of(pageNumber, pageLimit,Sort.Direction.DESC,"createDate" ));
						break;
				
			case"boardContent":
				temp = boardRepository.findByBoardContentContains(
						searchWord, 
						PageRequest.of(pageNumber, pageLimit,Sort.Direction.DESC,"createDate" ));
						break;
			
				
		}
		
		
		
		
		
		//1)단순조회
		//List<BoardEntity> temp = boardRepository.findAll(Sort.by(Sort.Direction.DESC,"createDate")); //맴버명을 써라 
		Page<BoardDTO> list = null;
		
		list = temp.map((entity) -> BoardDTO.toDTO(entity));
		
		/*
		log. info("getSize: {}", list.getSize());											//한 페이지당 글개수
		log. info("getTotalElements: {}", list.getTotalElements());							//전체 글개수
		log. info("getTotalPages: {}", list.getTotalPages());								//총 페이지 수
		log. info("getNumber: {}", list. getNumber());										//요청한 페이지
		log. info("getNumberOfElements: {}", list.getNumberOfElements());					//현재페이지의 총 글개수
		log. info("isFirst: {}", list. isFirst());											//첫번째 페이지냐?
		log. info("isLast: {}", list. isLast());											//마지막 페이지냐?
		log. info("getContent().get(0):{}", list.getContent().get(0).toString());			//
		*/
		
		
	
//		temp.forEach((entity) -> list.add(BoardDTO.toDTO(entity))); 밑에 페이지 넘기는거 할 때는 이걸 쓸 수가 없음.
		
		// 2) Lamba 객체, Stream : List, Set, Map / 중간연산, 최종연산 
		
		return list;
	}
	
	
	
	
	
	
	
	
	
//	public List<BoardDTO> selectAll(Pageable pageable, String searchItem, String searchWord) {
//		//2) 검색조회
//		List<BoardEntity> temp = null;
//		
//		switch (searchItem) {
//			case"boardWriter":
//				temp = boardRepository.findByBoardWriterContains(searchWord, Sort.by(Sort.Direction.DESC,"createDate"));
//				break;
//			case"boardTitle":
//				temp = boardRepository.findByBoardTitleContains(searchWord, Sort.by(Sort.Direction.DESC,"createDate"));
//				break;
//			case"boardContent":
//				temp = boardRepository.findByBoardContentContains(searchWord, Sort.by(Sort.Direction.DESC,"createDate"));
//				break;
//				
//		}
//		
//		
//		
//		
//		
//		//1)단순조회
//		//List<BoardEntity> temp = boardRepository.findAll(Sort.by(Sort.Direction.DESC,"createDate")); //맴버명을 써라 
//		List<BoardDTO> list = new ArrayList<>();
//		
//		
//		
//		temp.forEach((entity) -> list.add(BoardDTO.toDTO(entity)));
//		
//		return list;
//	}

	
	//전달인자로 전달받은 boardDTO 를 DB에 저장하는 코드
	//첨부파일 여부에 따라 DB의 두 컬럼값을 수정
	public void insertBoard(BoardDTO boardDTO) {
		MultipartFile uploadFile = boardDTO.getUploadFile();
		String savedFileName = null;
		String originalFileName = null;
		
		if(!uploadFile.isEmpty()) {		
		 savedFileName = FileService.saveFile(boardDTO.getUploadFile(), uploadPath);
		 originalFileName = uploadFile.getOriginalFilename();
		}
		
		boardDTO.setSavedFileName(savedFileName);
	    boardDTO.setOriginalFileName(originalFileName);
		BoardEntity entity = BoardEntity.toEntity(boardDTO);
		
		
		boardRepository.save(entity);		
	}
	

	/*
	 * baordSeq에 해당하는 글을 조회
	 */
	public BoardDTO selectOne(Long boardSeq) {
		Optional<BoardEntity> temp = boardRepository.findById(boardSeq);
		
		if(!temp.isPresent()) return null;
		
		
		return BoardDTO.toDTO(temp.get());
	}


	/*
	 * boardSeq레코드에 대한 조회수 증가
	 * 1) 조회 : findbyId(boardSeq)
	 * 2) hitcount를 get한 후 +1 ==> set
	 * save() : 
	 * update from board set hit_count = hit_count+1 where board_seq = ?;
	 * @param bordSeq
	 */
	@Transactional
	public void incrementHitcount(Long boardSeq) {
		Optional<BoardEntity> temp = boardRepository.findById(boardSeq); //조회하는거
		
		if(!temp.isPresent()) return ;
		BoardEntity entity = temp.get();
		
		int hitcount = entity.getHitCount() +1;
		entity.setHitCount(hitcount);
		
	}

	/*
	 * DB에 게시글 삭제요청 + 첨부파일도 삭제
	 */
	@Transactional
	public void deleteOne(Long boardSeq) {
		Optional<BoardEntity> temp = boardRepository.findById(boardSeq);
		
		if(!temp.isPresent()) return ;
		
		//temp를 뒤져서 savefilename이 존재하면 물리적으로 삭제 풀페스를 만들어야함.
		String savedFileName = temp.get().getSavedFileName();
		
		if(savedFileName != null) { 
			String fullPath = uploadPath + "/" + savedFileName;			
			FileService.deleteFile(fullPath);
		}

		boardRepository.deleteById(boardSeq);
		
	}


	/*
	 * DB에 수정처리
	 */
	@Transactional
	public void updateBoard(BoardDTO boardDTO) {
		
		// 1))첨부파일이 있는지 확인.
		MultipartFile file = boardDTO.getUploadFile();

		
		String newFile = !file.isEmpty() ? file.getOriginalFilename() : null;	//파일이 비었는지 확인
		//1) 수정하려는 데이터가 있는지 확인
		Optional<BoardEntity> temp = boardRepository.findById(boardDTO.getBoardSeq()); 
				
		if(!temp.isPresent()) return ;
		
		//2)) 조회된 데이터를꺼냄
		BoardEntity entity = temp.get();
		
		//기존파일이 DB에 저장되어있는지 확인
		String oldFile = entity.getSavedFileName();	
		
			//(1) 기존 파일이 있고, 업로드한 파일도 있다면 
			//	-- 하드 디스크에서는 기존파일을 삭제, 업로드한 파일 저장 (물리적)
			//	-- DB에서는 업로드한 파일로 두개의 컬럼을 업데이트
			//(2) 기존 파일이 없고, 업로드한 파일도 있다면 
			//	-- 하드 디스크에서는 업로드한 파일 저장
			//	-- DB에서는 업로드한 파일로 두개의 컬럼을 업데이트
		
		//업로드한 파일이 있다면
		if(newFile != null) {
			String savedFileName = null;
			savedFileName = FileService.saveFile(file, uploadPath);
			
			entity.setSavedFileName(savedFileName);
			entity.setOriginalFileName(newFile);
			
			//삭제하는 작업
			if(oldFile != null ) {
				String fullPath = uploadPath + "/" + oldFile;
				FileService.deleteFile(fullPath);
			}
		}	
		
		entity.setBoardTitle(boardDTO.getBoardTitle());
		entity.setBoardContent(boardDTO.getBoardContent());
	
	}
	
	
	/*
	 * file명이 들어있는 2개의 ㅋ컬럼의 값을 null로!
	 */
	//첨부파일 삭제하는거
	 @Transactional
	   public void deleteFile(Long boardSeq) {
	      Optional<BoardEntity> temp = boardRepository.findById(boardSeq);
	      
	      if (temp.isPresent()) {
	         BoardEntity entity = temp.get();
	         
	         String savedFileName = entity.getSavedFileName();
			 String fullPath = uploadPath +"/"+ savedFileName;
				
			// 1) 물리적으로 존재하는 파일을 삭제
			boolean result = FileService.deleteFile(fullPath);
			log.info("삭제결과: {}", result);
				
	         
	         entity.setOriginalFileName(null);
	         entity.setSavedFileName(null);
	      }
	 }
	
	
}
