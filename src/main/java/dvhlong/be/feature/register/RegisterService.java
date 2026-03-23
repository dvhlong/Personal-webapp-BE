package dvhlong.be.feature.register;

import dvhlong.be.common.constant.AppConstant;
import dvhlong.be.common.constant.I18nConstant;
import dvhlong.be.common.service.EmailService;
import dvhlong.be.common.service.OtpService;
import dvhlong.be.domain.user.User;
import dvhlong.be.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Locale;
@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService {

	private final UserRepository userRepository;
	private final StringRedisTemplate redisTemplate;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final OtpService otpService;

	public void register(RegisterRequest request, Locale locale) {
		if (userRepository.existsByEmail(request.email())) {
			ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
			detail.setProperty(AppConstant.MESSAGE_RESPONSE_FIELD_KEY, RegisterConstant.FIELD_EMAIL);
			detail.setProperty(AppConstant.MESSAGE_RESPONSE_CODE_KEY, I18nConstant.I18N_ERROR_EMAIL_EXISTS);
			throw new ErrorResponseException(HttpStatus.CONFLICT, detail, null);
		}

		// Store the registration data in Redis
		redisTemplate.opsForValue().set(
			AppConstant.REGISTER_PREFIX + request.email(),
			passwordEncoder.encode(request.password()) + RegisterConstant.REDIS_STR_DELIMITER + request.name(),
			Duration.ofMinutes(AppConstant.REGISTER_EXPIRY_MINUTES)
		);

		// Generate OTP and store it in Redis
		String otp = otpService.generateOtp();
		redisTemplate.opsForValue().set(
			AppConstant.OTP_PREFIX + request.email(),
			otp,
			Duration.ofMinutes(AppConstant.OTP_EXPIRY_MINUTES)
		);

		try {
			emailService.sendOtp(request.email(), otp, locale);
		} catch (Exception e) {
			log.error("Failed to send OTP email to {}: {}", request.email(), e.getMessage());
			redisTemplate.delete(AppConstant.OTP_PREFIX + request.email());
			throw e;
		}
	}

	public void verifyOtp(String email, String otp) {
		String otpKey = AppConstant.OTP_PREFIX + email;
		String registerKey = AppConstant.REGISTER_PREFIX + email;

		String otpValue = redisTemplate.opsForValue().get(otpKey);
		String registerValue = redisTemplate.opsForValue().get(registerKey);

		if (otpValue == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, I18nConstant.I18N_ERROR_OTP_EXPIRED);
		}

		if (!otpValue.equals(otp)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, I18nConstant.I18N_ERROR_OTP_INVALID);
		}

		if (registerValue == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, I18nConstant.I18N_ERROR_OTP_SESSION_EXPIRED);
		}

		String[] parts = registerValue.split(RegisterConstant.REDIS_STR_DELIMITER);
		User user = new User();
		user.setEmail(email);
		user.setPassword(parts[RegisterConstant.REDIS_STR_INDEX_PASSWORD]);
		user.setName(parts[RegisterConstant.REDIS_STR_INDEX_NAME]);
		user.setEnabled(true);
		userRepository.save(user);

		redisTemplate.delete(otpKey);
		redisTemplate.delete(registerKey);
	}

	public void resendOtp(String email, Locale locale) {
		String key = AppConstant.REGISTER_PREFIX + email;
		String value = redisTemplate.opsForValue().get(key);

		if (value == null) {
			throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				I18nConstant.I18N_ERROR_OTP_SESSION_EXPIRED
			);
		}

		String newOtp = otpService.generateOtp();
		redisTemplate.opsForValue().set(
			AppConstant.OTP_PREFIX + email,
			newOtp,
			Duration.ofMinutes(AppConstant.OTP_EXPIRY_MINUTES)
		);

		emailService.sendOtp(email, newOtp, locale);
	}
}