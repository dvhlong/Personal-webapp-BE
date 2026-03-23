package dvhlong.be.common.dto;

import dvhlong.be.common.constant.AppConstant;
import dvhlong.be.common.constant.I18nConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyOtpRequest(
	@NotBlank(message = I18nConstant.I18N_ERROR_REQUIRED)
	@Email(message = I18nConstant.I18N_ERROR_EMAIL_INVALID)
	String email,

	@NotBlank(message = I18nConstant.I18N_ERROR_REQUIRED)
	@Size(
		min = AppConstant.OTP_LENGTH,
		max = AppConstant.OTP_LENGTH,
		message = I18nConstant.I18N_ERROR_EQUAL_LENGTH
	)
	String otp
) {}
