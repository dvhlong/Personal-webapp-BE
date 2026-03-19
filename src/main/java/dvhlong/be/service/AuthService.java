package dvhlong.be.service;

import dvhlong.be.constant.AppConstant;
import dvhlong.be.dto.RegisterRequest;
import dvhlong.be.entity.User;
import dvhlong.be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final StringRedisTemplate redisTemplate;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final MessageService messageService;
	private static final SecureRandom RANDOM = new SecureRandom();

	public void register(RegisterRequest request, Locale locale) {
		if (userRepository.existsByEmail(request.email())) {
			throw new ResponseStatusException(
				HttpStatus.CONFLICT,
				messageService.get("error.email.exists", locale)
			);
		}

		String otp = generateOtp();
		redisTemplate.opsForValue().set(
			AppConstant.OTP_PREFIX + request.email(),
			passwordEncoder.encode(request.password()) + ":" + otp,
			Duration.ofMinutes(AppConstant.OTP_EXPIRY_MINUTES)
		);

		emailService.sendOtp(request.email(), otp, locale);
	}

	public void verifyOtp(String email, String otp, Locale locale) {
		String key = AppConstant.OTP_PREFIX + email;
		String value = redisTemplate.opsForValue().get(key);

		if (value == null) {
			throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				messageService.get("error.otp.expired", locale)
			);
		}

		String[] parts = value.split(":");
		if (!parts[1].equals(otp)) {
			throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				messageService.get("error.otp.invalid", locale)
			);
		}

		User user = new User();
		user.setEmail(email);
		user.setPassword(parts[0]);
		user.setEnabled(true);
		userRepository.save(user);

		redisTemplate.delete(key);
	}

	public void resendOtp(String email, Locale locale) {
		String key = AppConstant.OTP_PREFIX + email;
		String value = redisTemplate.opsForValue().get(key);

		if (value == null) {
			throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				messageService.get("error.otp.session.expired", locale)
			);
		}

		String encodedPassword = value.split(":")[0];
		String newOtp = generateOtp();
		redisTemplate.opsForValue().set(key,
			encodedPassword + ":" + newOtp,
			Duration.ofMinutes(AppConstant.OTP_EXPIRY_MINUTES)
		);

		emailService.sendOtp(email, newOtp, locale);
	}

	private String generateOtp() {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder otp = new StringBuilder();
		for (int i = 0; i < AppConstant.OTP_LENGTH; i++) {
			otp.append(chars.charAt(RANDOM.nextInt(chars.length())));
		}
		return otp.toString();
	}
}