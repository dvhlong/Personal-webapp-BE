package dvhlong.be.config;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import dvhlong.be.common.constant.AppConstant;

@Configuration
public class MessageConfig {

	@Bean
	MessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("messages");
		source.setDefaultEncoding("UTF-8");
		source.setDefaultLocale(Locale.forLanguageTag(AppConstant.LOCALE_VIETNAMESE));
		return source;
	}

	@Bean
	AcceptHeaderLocaleResolver localeResolver() {
		AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
		resolver.setSupportedLocales(List.of(
			Locale.forLanguageTag(AppConstant.LOCALE_VIETNAMESE),
			Locale.ENGLISH,
			Locale.JAPANESE
		));
		resolver.setDefaultLocale(Locale.forLanguageTag(Locale.ENGLISH.getLanguage()));
		return resolver;
	}
}
