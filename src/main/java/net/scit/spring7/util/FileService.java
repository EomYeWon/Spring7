package net.scit.spring7.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileService {
	//저장파일명을 반환
	public static String saveFile(MultipartFile uploadFile, String uploadPath) {
		
		// 저장 디렉토리 생성
		if(!uploadFile.isEmpty()) {
			File path = new File(uploadPath); 	//디렉토리도 파일임!
			if(!path.isDirectory());		//경로가 존재하지 않으면 생성
			path.mkdirs();
		}
		
		//예) jaquery-3.7.1.min     .js -> 파일명
		//. 이없는 확장자가 없는 파일도 있을 수 있음.
		String originalFileName = uploadFile.getOriginalFilename();		//원본파일
		String savedFileName = null;		//저장할 때 사용할 파일명
		String filename = null;				//bts
		String ext = null;					//jpb  뒤에서 첫 . 다음단어
		String uuid = UUID.randomUUID().toString(); //난수 만드는거
		
		// .의 위치 찾기
		int position = originalFileName.lastIndexOf(".");	//12 0~11 까지가 파일명.
		if(position == 1) {// 확장자가 없는 파일
			ext = "";
			filename = originalFileName;
		} else {
			ext = "." + originalFileName.substring(position+1);
			filename = originalFileName.substring(0, position);
		}
		
		savedFileName = filename + "_" + uuid + "." + ext;
		
		
		//디렉토리에 저장하기
		//c:/uploadPath / savedFileName
		String fullPath = uploadPath + "/" + savedFileName;	//문자열
		
		File serverFile = null;
		serverFile = new File(fullPath);
		
		
		try {
			uploadFile.transferTo(serverFile);
		} catch (IOException e) { //저장이 안된것이므로, DB도 저장하면 안됨.
			savedFileName = null;			
			e.printStackTrace();
		}
		
		
		return savedFileName; // 가짜문자열을 반환
	}
	
	//파일 삭제
	public static boolean deleteFile(String fullPath) {
		boolean result = false; 	//삭제여부 반환
		
		File file = new File(fullPath);
		if(file.isFile()) {
			result = file.delete();
		}
		return result;
	}

}
