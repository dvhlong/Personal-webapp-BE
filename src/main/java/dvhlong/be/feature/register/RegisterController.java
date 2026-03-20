package dvhlong.be.feature.register;

import dvhlong.be.common.dto.VerifyOtpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegisterController {

	private final RegisterService registerService;

	@PostMapping("/register")
	public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request, Locale locale) {
		registerService.register(request, locale);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<Void> verifyOtp(@Valid @RequestBody VerifyOtpRequest request, Locale locale) {
		registerService.verifyOtp(request.email(), request.otp(), locale);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<Void> resendOtp(@RequestParam String email, Locale locale) {
		registerService.resendOtp(email, locale);
		return ResponseEntity.ok().build();
	}
}