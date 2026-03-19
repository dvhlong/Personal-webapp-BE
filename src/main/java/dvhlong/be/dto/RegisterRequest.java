package dvhlong.be.dto;

import dvhlong.be.constant.AppConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
	@NotBlank @Email String email,
	@NotBlank @Size(min = AppConstant.PASSWORD_MIN_LENGTH) String password
) {}
