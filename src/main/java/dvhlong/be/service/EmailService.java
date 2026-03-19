package dvhlong.be.service;

import java.util.Locale;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import dvhlong.be.constant.AppConstant;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender mailSender;
	private final MessageService messageService;

	public void sendOtp(String to, String otp, Locale locale) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(messageService.get("email.otp.subject", locale));
		message.setText(messageService.get("email.otp.body", locale, otp, AppConstant.OTP_EXPIRY_MINUTES));
		mailSender.send(message);
	}
}
