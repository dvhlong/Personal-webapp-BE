package dvhlong.be.common.service;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
	private final MessageSource messageSource;

	public String get(String key, Locale locale, Object... args) {
		return messageSource.getMessage(key, args, locale);
	}
}
