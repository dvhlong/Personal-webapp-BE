package dvhlong.be.feature.register;

import dvhlong.be.common.constant.AppConstant;
import dvhlong.be.common.constant.I18nConstant;
import dvhlong.be.common.service.EmailService;
import dvhlong.be.common.service.MessageService;
import dvhlong.be.common.service.OtpService;
import dvhlong.be.domain.user.User;
import dvhlong.be.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
	private final MessageService messageService;
	private final OtpService otpService;

	public void register(RegisterRequest request, Locale locale) {
		if (userRepository.existsByEmail(request.email())) {
			throw new ResponseStatusException(
				HttpStatus.CONFLICT,
				messageService.get(I18nConstant.I18N_ERROR_EMAIL_EXISTS, locale)
			);
		}

		String otp = otpService.generateOtp();
		redisTemplate.opsForValue().set(
			AppConstant.OTP_PREFIX + request.email(),
			passwordEncoder.encode(request.password())
				+ ":" + otp
				+ ":" + request.name(),
			Duration.ofMinutes(AppConstant.OTP_EXPIRY_MINUTES)
		);

		try {
			emailService.sendOtp(request.email(), otp, locale);
		} catch (MailSendException e) {
			log.error("Failed to send OTP email to {}: {}", request.email(), e.getMessage());
			redisTemplate.delete(AppConstant.OTP_PREFIX + request.email());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
				messageService.get(I18nConstant.I18N_ERROR_EMAIL_NOT_FOUND, locale));
		}
	}

	public void verifyOtp(String email, String otp, Locale locale) {
		String key = AppConstant.OTP_PREFIX + email;
		String value = redisTemplate.opsForValue().get(key);

		if (value == null) {
			throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				messageService.get(I18nConstant.I18N_ERROR_OTP_EXPIRED, locale)
			);
		}

		String[] parts = value.split(":");
		if (!parts[1].equals(otp)) {
			throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				messageService.get(I18nConstant.I18N_ERROR_OTP_INVALID, locale)
			);
		}

		User user = new User();
		user.setEmail(email);
		user.setPassword(parts[0]);
		user.setName(parts[2]);
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
				messageService.get(I18nConstant.I18N_ERROR_OTP_SESSION_EXPIRED, locale)
			);
		}

		String[] parts = value.split(":");
		String encodedPassword = parts[0];
		String name = parts[2];
		String newOtp = otpService.generateOtp();
		redisTemplate.opsForValue().set(key,
			encodedPassword + ":" + newOtp + ":" + name,
			Duration.ofMinutes(AppConstant.OTP_EXPIRY_MINUTES)
		);

		emailService.sendOtp(email, newOtp, locale);
	}
}