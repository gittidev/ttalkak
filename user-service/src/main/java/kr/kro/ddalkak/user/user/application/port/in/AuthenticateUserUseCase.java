package kr.kro.ddalkak.user.user.application.port.in;

import kr.kro.ddalkak.user.user.domain.JwtToken;

public interface AuthenticateUserUseCase {
    /**
     * 사용자가 입력한 아이디와 비밀번호로 인증 처리를 하고 JWT 토큰을 반환한다.
     *
     * @param username 사용자 ID
     * @param password 사용자 Password
     * @return {@link JwtToken} 인증에 사용될 JWT 토큰 
     */
    JwtToken authenticate(String username, String password);
}
