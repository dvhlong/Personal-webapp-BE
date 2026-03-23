package dvhlong.be.common.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import dvhlong.be.common.constant.AppConstant;
import dvhlong.be.common.constant.I18nConstant;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender mailSender;
	private final MessageService messageService;
	@Value("${spring.mail.from}")
	private String mailFrom;

	public void sendOtp(String to, String otp, Locale locale) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(mailFrom);
		message.setTo(to);
		message.setSubject(messageService.get(I18nConstant.I18N_EMAIL_OTP_SUBJECT, locale));
		message.setText(messageService.get(I18nConstant.I18N_EMAIL_OTP_BODY, locale, otp, AppConstant.OTP_EXPIRY_MINUTES));
		mailSender.send(message);
	}
}
