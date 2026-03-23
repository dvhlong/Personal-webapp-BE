package dvhlong.be;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import dvhlong.be.common.constant.AppConstant;
import dvhlong.be.common.dto.MessageResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<List<MessageResponse>> handleResponseStatusException(
		ResponseStatusException ex
	) {
		return ResponseEntity.status(ex.getStatusCode())
			.body(List.of(new MessageResponse(null, ex.getReason(), null)));
	}

	@ExceptionHandler(ErrorResponseException.class)
	public ResponseEntity<List<MessageResponse>> handleErrorResponse(
		ErrorResponseException ex
	) {
		Map<String, Object> properties = ex.getBody().getProperties();
		String field = properties != null ? (String) properties.get(AppConstant.MESSAGE_RESPONSE_FIELD_KEY) : null;
		String code = properties != null ? (String) properties.get(AppConstant.MESSAGE_RESPONSE_CODE_KEY) : null;
		@SuppressWarnings("unchecked")
		Map<String, Object> params = properties != null ? (Map<String, Object>) properties.get(AppConstant.MESSAGE_RESPONSE_PARAMS_KEY) : null;
		return ResponseEntity.status(ex.getStatusCode())
			.body(List.of(new MessageResponse(field, code, params)));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<MessageResponse>> handleValidation(MethodArgumentNotValidException ex) {
		List<MessageResponse> errors = ex.getBindingResult().getFieldErrors().stream()
			.map(fe -> {
				@SuppressWarnings("unchecked")
				Map<String, Object> attrs = new HashMap<>(
					ex.getBindingResult().getAllErrors().stream()
						.filter(e -> e instanceof FieldError f && f.getField().equals(fe.getField()))
						.findFirst()
						.map(e -> e.unwrap(ConstraintViolation.class).getConstraintDescriptor().getAttributes())
						.orElse(Map.of())
				);
				attrs.remove("message");
				attrs.remove("groups");
				attrs.remove("payload");
				return new MessageResponse(fe.getField(), fe.getDefaultMessage(), attrs);
			})
			.toList();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<List<MessageResponse>> handleGenericException(Exception ex) throws Exception {
		if (ex instanceof ResponseStatusException
			|| ex instanceof ErrorResponseException
			|| ex instanceof MethodArgumentNotValidException) {
			throw ex;
		}
		log.error("Unexpected error: {}", ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(List.of(new MessageResponse(null, "error.server.internal", null)));
	}
}
