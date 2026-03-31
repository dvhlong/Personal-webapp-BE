package dvhlong.be.common.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dvhlong.be.common.constant.I18nConstant;
import dvhlong.be.domain.user.User;
import dvhlong.be.domain.user.UserRepository;
import dvhlong.be.feature.login.LoginInfo;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Controller {
	private final UserRepository userRepository;

	@GetMapping("/me")
	public ResponseEntity<LoginInfo> me(@AuthenticationPrincipal Jwt jwt) {
		String userId = jwt.getSubject();
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, I18nConstant.I18N_ERROR_REFRESH_TOKEN_INVALID));
		return ResponseEntity.ok(new LoginInfo(user.getName(), user.getEmail()));
	}
}
