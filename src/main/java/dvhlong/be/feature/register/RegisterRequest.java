package dvhlong.be.feature.register;

import dvhlong.be.common.constant.AppConstant;
import dvhlong.be.common.constant.I18nConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
	@NotBlank(message = I18nConstant.I18N_ERROR_REQUIRED)
	@Email(message = I18nConstant.I18N_ERROR_EMAIL_INVALID)
	String email,

	@NotBlank(message = I18nConstant.I18N_ERROR_REQUIRED)
	@Size(
		min = AppConstant.PASSWORD_MIN_LENGTH,
		message = I18nConstant.I18N_ERROR_MIN_LENGTH
	)
	String password,
	
	@NotBlank(message = I18nConstant.I18N_ERROR_REQUIRED)
	String name
) {}
