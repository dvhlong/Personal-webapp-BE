package dvhlong.be.feature.login;

public record LoginResponse(
	String accessToken,
	String refreshToken,
	String name,
	String email
	// String role
) {}
