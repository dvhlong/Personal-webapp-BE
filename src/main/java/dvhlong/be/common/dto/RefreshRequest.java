package dvhlong.be.common.dto;

import dvhlong.be.common.constant.I18nConstant;
import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
	@NotBlank(message = I18nConstant.I18N_ERROR_REQUIRED)
	String refreshToken
) {}
