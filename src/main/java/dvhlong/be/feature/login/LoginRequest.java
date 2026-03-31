package dvhlong.be.feature.login;

import dvhlong.be.common.constant.I18nConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
	@NotBlank(message = I18nConstant.I18N_ERROR_REQUIRED)
	@Email(message = I18nConstant.I18N_ERROR_EMAIL_INVALID)
	String email,

	@NotBlank(message = I18nConstant.I18N_ERROR_REQUIRED)
	String password
) {}
