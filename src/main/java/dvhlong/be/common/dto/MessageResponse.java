package dvhlong.be.common.dto;

import java.util.Map;

public record MessageResponse(
	String field,
	String code,
	Map<String, Object> params
) {
	public MessageResponse(String field, String code) {
		this(field, code, Map.of());
	}
}
