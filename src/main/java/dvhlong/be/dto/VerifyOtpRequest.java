package dvhlong.be.dto;

import dvhlong.be.constant.AppConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyOtpRequest(
	@NotBlank @Email String email,
	@NotBlank @Size(min = AppConstant.OTP_LENGTH, max = AppConstant.OTP_LENGTH) String otp
) {}
