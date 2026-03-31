package dvhlong.be.common.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import dvhlong.be.common.constant.AppConstant;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
	private final JwtEncoder jwtEncoder;
	private final StringRedisTemplate redisTemplate;

	@Value("${jwt.expiration}")
	private long expiration;

	@Value("${jwt.refresh-expiration}")
	private long refreshExpiration;

	public String generateAccessToken(String userId) {
		Instant now = Instant.now();
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		JwtClaimsSet claims = JwtClaimsSet.builder()
			.subject(userId)
			.issuedAt(now)
			.expiresAt(now.plusSeconds(expiration))
			.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

	public String generateRefreshToken(String userId) {
		String token = UUID.randomUUID().toString();
		redisTemplate.opsForValue().set(
			AppConstant.REFRESH_TOKEN_PREFIX + token,
			userId,
			Duration.ofSeconds(refreshExpiration)
		);
		return token;
	}

	public String getUserIdFromRefreshToken(String token) {
		return redisTemplate.opsForValue().get(AppConstant.REFRESH_TOKEN_PREFIX + token);
	}
}
