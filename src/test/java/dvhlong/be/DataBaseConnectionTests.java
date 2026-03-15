package dvhlong.be;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
@ActiveProfiles("test")
class DataBaseConnectionTests {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Test
	void testPostgreSQLConnection() {
		assertThatNoException().isThrownBy(() -> jdbcTemplate.execute("SELECT 1"));
	}

	@Test
	void testRedisConnection() {
		redisTemplate.opsForValue().set("testKey", "testValue");
		assertThat(redisTemplate.opsForValue().get("testKey")).isEqualTo("testValue");
	}

}
