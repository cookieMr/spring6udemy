package mr.cookie.spring6udemy.services;

import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class Spring6WebAppApplicationTests {

	@Test
	void contextLoads(@NotNull ApplicationContext context) {
		Assertions.assertThat(context).isNotNull();
	}

}
