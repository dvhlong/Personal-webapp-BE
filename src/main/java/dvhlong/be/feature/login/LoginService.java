package dvhlong.be.feature.login;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dvhlong.be.common.constant.I18nConstant;
import dvhlong.be.common.service.JwtService;
import dvhlong.be.domain.user.User;
import dvhlong.be.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public LoginResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.email())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, I18nConstant.I18N_ERROR_LOGIN_FAILED));

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, I18nConstant.I18N_ERROR_LOGIN_FAILED);
		}

		if (!user.isEnabled()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, I18nConstant.I18N_ERROR_ACCOUNT_DISABLED);
		}

		return new LoginResponse(
			jwtService.generateAccessToken(user.getId()),
			jwtService.generateRefreshToken(user.getId()),
			user.getName(),
			user.getEmail()
		);
	}

	public LoginResponse refresh(String refreshToken) {
		String userId = jwtService.getUserIdFromRefreshToken(refreshToken);
		if (userId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, I18nConstant.I18N_ERROR_REFRESH_TOKEN_INVALID);
		}
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, I18nConstant.I18N_ERROR_REFRESH_TOKEN_INVALID));
		return new LoginResponse(
			jwtService.generateAccessToken(userId),
			refreshToken,
			user.getName(),
			user.getEmail()
		);
	}
}
