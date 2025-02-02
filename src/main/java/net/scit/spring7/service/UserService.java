package net.scit.spring7.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.scit.spring7.dto.UserDTO;
import net.scit.spring7.entity.UserEntity;
import net.scit.spring7.repository.UserRepository;

@Service
@RequiredArgsConstructor //레파지토리가 필요
public class UserService {
	
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;		//비밀번호 암호화 할떄 한거.

	/*
	 * 전달받은 userId가 DB에 존재하는지 여부 확인
	 */
	public boolean existId(String userId) {
		boolean result = userRepository.existsById(userId);
		
		return !result;
	}

	
	//회원가입 처리
	public boolean joinProc(UserDTO userDTO) {
		//비밀번호 암호화
		userDTO.setUserPwd(bCryptPasswordEncoder.encode(userDTO.getUserPwd()));
		
		UserEntity entity = UserEntity.toEntity(userDTO);
		userRepository.save(entity);	//회원가입 완료
		
		boolean result = userRepository.existsById(userDTO.getUserId());
		 
		return result;
	}

	/*
	 * 입력한 비밀번호가 맞는지 확인 
	 */
	public UserDTO pwdCheck(String userId, String userPwd) {
		Optional<UserEntity> temp = userRepository.findById(userId);
		
		if(temp.isPresent()) {
			UserEntity entity = temp.get();
			String UserPwd = entity.getUserPwd();	// 암호화 된 비번 		//userPwd 는 raw data 
			
			boolean result = bCryptPasswordEncoder.matches(userPwd, UserPwd);		//입력된 비번, DB암호화된 비번 확인하는거.
			
		
			if(result) {
				return UserDTO.toDTO(entity);
			}
		}
		return null;
	}
	/*
	 * DB에서 개인정보 수정 처리
	 */
	@Transactional
	public void updateProc(UserDTO userDTO) {
		String id = userDTO.getUserId(); 		//id뽑아내기
		
		Optional<UserEntity> temp = userRepository.findById(id);
		if(temp.isPresent()) {
			UserEntity entity = temp.get();
			
			//사용자가 입력한 정보 (비번과 이메일)
			String encodePwd = bCryptPasswordEncoder.encode(userDTO.getUserPwd());  
			String email = userDTO.getEmail();
			
			// DB에서 뽑아낸 정보 (비번과 이메일)
			entity.setUserPwd(encodePwd);
			entity.setEmail(email);
		}
	}
}
