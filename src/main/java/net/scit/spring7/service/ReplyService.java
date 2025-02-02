package net.scit.spring7.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.spring7.dto.ReplyDTO;
import net.scit.spring7.entity.BoardEntity;
import net.scit.spring7.entity.ReplyEntity;
import net.scit.spring7.repository.BoardRepository;
import net.scit.spring7.repository.ReplyRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReplyService {
	private final BoardRepository boardRepository;	//boardRepository 가져오는거.
	private final ReplyRepository replyRepository;
	
	/*
	 * 전달받은 값을 entity로 수정한 후에 DB에 저장
	 */
	public void insert(ReplyDTO replyDTO) {
		//1) 부모글이 있는지 조회
		Optional<BoardEntity> temp = boardRepository.findById(replyDTO.getBoardSeq());
				
		if(!temp.isPresent()) return;
		
		//2) 부모글을 꺼냄
		BoardEntity boardEntity = temp.get();
		
		//3) 두개를 전달해서 entity를 반환
		ReplyEntity replyEntity = ReplyEntity.toEntity(replyDTO, boardEntity);
		//log.info("~~~!!!:{}",replyEntity.toString());
		
		//4) DB에 저장
		replyRepository.save(replyEntity);
	

	}
	public List<ReplyDTO> replyAll(Long boardSeq) {
		//1)) 부모글이 있는지 조회
		Optional<BoardEntity> temp = boardRepository.findById(boardSeq);
		
		//2)) 댓글 조회를 위한 Query Method
		List<ReplyEntity> entityList = 
				replyRepository.findAllByBoardEntity(temp, Sort.by(Sort.Direction.DESC,"replySeq"));
				
		log.info("댓글 개수: {}",entityList.size() );
		
		//3)) List<ReplyDTO>를 선언
		List<ReplyDTO> list = new ArrayList<>();
		
		// 4))Entity --> DTO
		entityList.forEach((entity) -> list.add(ReplyDTO.toDTO(entity, boardSeq)));
		
			return list;
	}
	
	
	
	/*
	 * 댓글 삭제 처리
	 */
	public void replyDelete(Long replySeq) {
		Optional<ReplyEntity> temp = replyRepository.findById(replySeq);
		
		if(!temp.isPresent()) return;
		
		replyRepository.deleteById(replySeq);
		
	}
	
	/*
	 * 댓글조회
	 */
	public ReplyDTO replySelectOne(long replySeq) {

		Optional<ReplyEntity> temp = replyRepository.findById(replySeq);
		
		if(!temp.isPresent()) return null;
		
		ReplyEntity entity = temp.get();
		ReplyDTO replyDTO = ReplyDTO.toDTO(entity, entity.getBoardEntity().getBoardSeq());
		
		return replyDTO;
	}
	
	
	/*
	 * 댓글 수정
	 */
	public Boolean replyUpdate(Long replySeq, String replyContent) {
		Optional<ReplyEntity> temp = replyRepository.findById(replySeq);
		
		if(!temp.isPresent()) return null;
		
		ReplyEntity entity = temp.get();
		entity.setReplyContent(replyContent);
		replyRepository.save(entity);

		return true;
	}
	
	
}
