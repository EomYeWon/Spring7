package net.scit.spring7.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import net.scit.spring7.entity.BoardEntity;


public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

//	List<BoardEntity> findByBoardWriterContains(String searchWord, Sort by);
//
//	List<BoardEntity> findByBoardTitleContains(String searchWord, Sort by);
//
//	List<BoardEntity> findByBoardContentContains(String searchWord, Sort by);

	// 검색 기능할때 쓴거.
	Page<BoardEntity> findByBoardTitleContains(String searchWord, PageRequest of);

	Page<BoardEntity> findByBoardWriterContains(String searchWord, PageRequest of);

	Page<BoardEntity> findByBoardContentContains(String searchWord, PageRequest of);

}
