package dvhlong.be.controller;

import dvhlong.be.dto.RegisterRequest;
import dvhlong.be.dto.VerifyOtpRequest;
import dvhlong.be.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request, Locale locale) {
		authService.register(request, locale);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<Void> verifyOtp(@Valid @RequestBody VerifyOtpRequest request, Locale locale) {
		authService.verifyOtp(request.email(), request.otp(), locale);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<Void> resendOtp(@RequestParam String email, Locale locale) {
		authService.resendOtp(email, locale);
		return ResponseEntity.ok().build();
	}
}