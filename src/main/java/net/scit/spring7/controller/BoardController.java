package net.scit.spring7.controller;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.spring7.dto.BoardDTO;
import net.scit.spring7.dto.LoginUserDetails;
import net.scit.spring7.service.BoardService;
import net.scit.spring7.util.FileService;
import net.scit.spring7.util.PageNavigator;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;
    
    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;
    
    @Value("${user.board.pageLimit}")	// 1.14
    private int pageLimit;

    //게시글 목록 요청 게시글 전체조회, 게시글의 특저 조건에 맞춘 조회
    @GetMapping("/boardList")
    public String boardList(
    		 @AuthenticationPrincipal LoginUserDetails loginUser,
    		@PageableDefault(page=1) Pageable pageable,
            @RequestParam(name = "searchItem", defaultValue = "boardTitle") String searchItem,
            @RequestParam(name = "searchWord", defaultValue = "") String searchWord,
            Model model) {
    	
    	// 2) 검색 기능 + 페이징 기능   
        Page<BoardDTO> list = boardService.selectAll(pageable, searchItem, searchWord);
    
        int totalPages = list.getTotalPages();		//DB가 계산해준 총 페이지 수
        int page = pageable.getPageNumber();		//현재 사용자가 요청한 페이지
        
        PageNavigator navi = new PageNavigator(pageLimit, page, totalPages);
        /*
         * 1) 검색 기능 추가
         * 	searchItem과 searchWord는 null인 상태로 service로 전달되면 안됨
         * 	selectAll을 수정
         * 	List<BoardDTO> list = boardService.selectAll(searchItem,searchWord);
         */
        
        model.addAttribute("list", list);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("searchItem", searchItem);
        model.addAttribute("navi", navi);
        model.addAttribute("totalPages", totalPages);
        
        if (loginUser != null) {
            model.addAttribute("loginName", loginUser.getUserName());
         }

        
        model.addAttribute("startintItemNum", (pageLimit * (page-1)));

        return "/board/boardList";
    }

    //게시글 쓰기 화면 요청
    @GetMapping("/boardWrite")
    public String boardWrite(@AuthenticationPrincipal LoginUserDetails loginUser, Model model) {
    	if (loginUser != null) {
            model.addAttribute("loginName", loginUser.getUserName());
         }
    	
        return "board/boardWrite";
    }

    /**
     * 게시글 쓰기 화면 요청
     */
    @PostMapping("/boardWrite")
    public String boardWrite(@ModelAttribute BoardDTO boardDTO) {
        boardService.insertBoard(boardDTO);

        return "redirect:/board/boardList";
    }
    
/*
 * 	게시글검색
 */
    @GetMapping("/boardDetail")
    public String boardDetail(
    		@AuthenticationPrincipal LoginUserDetails loginUser,
            @RequestParam(name = "boardSeq") Long boardSeq,
            @RequestParam(name = "searchItem", defaultValue = "boardTitle") String searchItem,
            @RequestParam(name = "searchWord", defaultValue = "") String searchWord,
            Model model) {
        //DB에서 boardSeq에 해당하는 하나의 게시글을 조회
        BoardDTO boardDTO = boardService.selectOne(boardSeq);
        boardService.incrementHitcount(boardSeq); 		//조회수 증가

        model.addAttribute("board", boardDTO);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("searchItem", searchItem);

        if (loginUser != null) {
            model.addAttribute("loginName", loginUser.getUserName());
         }
        return "/board/boardDetail";
    }

    /*
	  * 삭제요청
     */
    @GetMapping("/boardDelete")
    public String boardDelete(
            @RequestParam(name = "boardSeq") Long boardSeq,
            @RequestParam(name = "searchItem", defaultValue = "boardTitle") String searchItem,
            @RequestParam(name = "searchWord", defaultValue = "") String searchWord, RedirectAttributes redirectAttributes) {
        boardService.deleteOne(boardSeq);

        //redirect시 model을 사용할 수 없음!!
        redirectAttributes.addAttribute("searchItem", searchItem);
        redirectAttributes.addAttribute("searchWord", searchWord);
        return "redirect:/board/boardList";
    }

    /*
	  * 수정을 위한 화면요청
     */
    @GetMapping("/boardUpdate")
    public String boardUpdate(
    		@AuthenticationPrincipal LoginUserDetails loginUser,
            @RequestParam(name = "boardSeq") Long boardSeq,
            @RequestParam(name = "searchItem", defaultValue = "boardTitle") String searchItem,
            @RequestParam(name = "searchWord", defaultValue = "") String searchWord,
            Model model) {
        BoardDTO boardDTO = boardService.selectOne(boardSeq);

        model.addAttribute("board", boardDTO);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("searchItem", searchItem);
        
        if (loginUser != null) {
            model.addAttribute("loginName", loginUser.getUserName());
         }
        return "/board/boardUpdate";
    }

    /*
	  * 게시글 수정 처리 요청
     */
    @PostMapping("/boardUpdate")
    public String boardUpdate(@ModelAttribute BoardDTO boardDTO,
            @RequestParam(name = "searchItem", defaultValue = "boardTitle") String searchItem,
            @RequestParam(name = "searchWord", defaultValue = "") String searchWord, RedirectAttributes redirectAttributes) {
    	
        boardService.updateBoard(boardDTO);
        redirectAttributes.addAttribute("searchItem", searchItem);
        redirectAttributes.addAttribute("searchWord", searchWord);
        return "redirect:/board/boardList";
    }
    /*
     * 쓰레기통 아이콘을 클릭하여 파일만 삭제하는 작업
     */
    @GetMapping("/deleteFile")
    public String deleteFile(
    		@RequestParam(name="boardSeq") Long boardSeq, 
    		@RequestParam(name = "searchItem", defaultValue = "boardTitle") String searchItem,
            @RequestParam(name = "searchWord", defaultValue = "") String searchWord,
            RedirectAttributes rttr) {
    	 
    	BoardDTO boardDTO = boardService.selectOne(boardSeq);
    	
    	String savedFileName = boardDTO.getOriginalFileName();
    	String fullPath = uploadPath + "/" + savedFileName;
    	
    	
    	//1)물리적으로 존재하는 파일을 삭제
    	boolean  result = FileService.deleteFile(fullPath);
    	log.info("삭제결과: {}", result);
    	
    	//2) DB도 수정 --> file컬럼 두개의값을 null로
    	//서비스단으로 삭제요청
    	boardService.deleteFile(boardSeq);
    	rttr.addAttribute("boardSeq", boardSeq);
    	rttr.addAttribute("searchItem", searchItem);
    	rttr.addAttribute("searchWord", searchWord);
    	//서치아이템, 서치워드도 보내야함.
    	//클라이언트에서 보내야함
    	
    	return "redirect:/board/boardDetail";
    }


    
    //첨부파일 다운받는거
    @GetMapping("/download")
    public String download(@RequestParam(name="boardSeq") Long boardSeq
          , HttpServletResponse response) {
       BoardDTO boardDTO = boardService.selectOne(boardSeq);
       
       String savedFileName = boardDTO.getSavedFileName();
       String originalFileName = boardDTO.getOriginalFileName();
       
       try {
          String tempName = URLEncoder.encode(originalFileName
                , StandardCharsets.UTF_8.toString());
          
          response.setHeader("Content-Disposition", "attachment;filename=" + tempName);
       } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
       }
       
       String fullPath = uploadPath + "/" + savedFileName;
       
       FileInputStream fin = null;         // 로컬에서 input
       ServletOutputStream fout = null;   // 네트워크로 output
       
       try {
          fin = new FileInputStream(fullPath);
          fout = response.getOutputStream();
          
          FileCopyUtils.copy(fin, fout);
          
          fout.close();
          fin.close();
       } catch (Exception e) {
          e.printStackTrace();
       }
       
       return null;
    }
  }


