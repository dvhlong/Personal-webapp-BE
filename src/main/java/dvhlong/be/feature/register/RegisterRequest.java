package dvhlong.be.feature.register;

import dvhlong.be.common.constant.AppConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
	@NotBlank @Email String email,
	@NotBlank @Size(min = AppConstant.PASSWORD_MIN_LENGTH) String password,
	@NotBlank String name
) {}
