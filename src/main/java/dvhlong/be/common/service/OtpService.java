package dvhlong.be.common.service;

import dvhlong.be.common.constant.AppConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;
@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

	private static final SecureRandom RANDOM = new SecureRandom();

	public String generateOtp() {
		String chars = AppConstant.OTP_ALLOWED_CHARS;
		StringBuilder otp = new StringBuilder();
		for (int i = 0; i < AppConstant.OTP_LENGTH; i++) {
			otp.append(chars.charAt(RANDOM.nextInt(chars.length())));
		}
		return otp.toString();
	}
}