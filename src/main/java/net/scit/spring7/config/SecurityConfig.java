package net.scit.spring7.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import net.scit.spring7.handler.LoginFalluerHandler;
import net.scit.spring7.handler.LoginSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	//Hadler 사용
	private final LoginSuccessHandler loginSuccessHandler;	//로그인 성공 처리 Handler
	private final LoginFalluerHandler loginFalluerHandler;	//로그인 실패 처리 Handler
	

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests((auth) -> auth
						.requestMatchers(
								"/",
								"/board/boardList",
								"/board/boardDetail",
								"/board/download",
								"/user/join",
								"/user/idCheck",
								"/user/joinProc",
								"/user/login",
								"/reply/replyAll",
								 "/images/**",
								 "/css/**",
								 "/js/**")
						.permitAll()
						.requestMatchers("/admin").hasRole("ADMIN")
						.requestMatchers("/user/mypage").hasAnyRole("ADMIN", "USER")
						.anyRequest().authenticated());

		// Custom Login 설정
		http
			.formLogin((auth) -> auth
					.loginPage("/user/login")
					.loginProcessingUrl("/user/loginProc")
					.usernameParameter("userId")
					.passwordParameter("userPwd")
					.successHandler(loginSuccessHandler)
					.failureHandler(loginFalluerHandler)
					//.failureUrl("/user/login?error=true") 	FailureHandler가 있으면 이코드는 없어야 함.
					.permitAll());

		// logout 설정
		http
				.logout((auth) -> auth
						.logoutUrl("/user/logout")					
						.logoutSuccessUrl("/")
						.invalidateHttpSession(true)
						.clearAuthentication(true));

		// POST 요청시 CSRF 토큰을 요청하므로 (Cross-Site Request Forgery) 비활성화(개발환경)
		http
				.csrf((auth) -> auth.disable());

		return http.build();
	}

	// 단방향 비밀번호 암호화
	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
